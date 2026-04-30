import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, catchError, finalize, of, tap, throwError } from 'rxjs';

import {
  CreatePackageRequest,
  PackageModel,
  PackageStatus,
  PackagesByStatus,
  UpdatePackageStatusRequest
} from '../../core/models/package.model';

const PACKAGES_URL = 'http://localhost:8080/api/paquetes';
const PACKAGE_STATUSES: readonly PackageStatus[] = ['RECIBIDO', 'EN_TRANSITO', 'ENTREGADO'];

@Injectable()
export class PackageService {
  private readonly http = inject(HttpClient);
  private readonly packagesState = signal<PackageModel[]>([]);
  private readonly loadingState = signal(false);
  private readonly errorState = signal<string | null>(null);
  private readonly updatingPackageIdState = signal<number | null>(null);

  readonly statuses = PACKAGE_STATUSES;
  readonly packages = this.packagesState.asReadonly();
  readonly isLoading = this.loadingState.asReadonly();
  readonly errorMessage = this.errorState.asReadonly();
  readonly updatingPackageId = this.updatingPackageIdState.asReadonly();
  readonly packagesByStatus = computed<PackagesByStatus>(() => ({
    RECIBIDO: this.packages().filter((packageItem) => packageItem.estado === 'RECIBIDO'),
    EN_TRANSITO: this.packages().filter((packageItem) => packageItem.estado === 'EN_TRANSITO'),
    ENTREGADO: this.packages().filter((packageItem) => packageItem.estado === 'ENTREGADO')
  }));

  loadPackages(): Observable<PackageModel[]> {
    this.loadingState.set(true);
    this.errorState.set(null);

    return this.http.get<PackageModel[]>(PACKAGES_URL).pipe(
      tap((packages) => this.packagesState.set(packages)),
      catchError((error: HttpErrorResponse) => {
        this.errorState.set('No fue posible cargar los paquetes.');
        return throwError(() => error);
      }),
      finalize(() => this.loadingState.set(false))
    );
  }

  updateStatus(packageId: number, nuevoEstado: PackageStatus): Observable<PackageModel> {
    const previousSnapshot = this.packages();
    const packageToUpdate = previousSnapshot.find((packageItem) => packageItem.id === packageId);

    this.errorState.set(null);

    if (!packageToUpdate || packageToUpdate.estado === nuevoEstado) {
      return of(packageToUpdate as PackageModel);
    }

    this.updatingPackageIdState.set(packageId);
    this.packagesState.update((packages) =>
      packages.map((packageItem) =>
        packageItem.id === packageId ? { ...packageItem, estado: nuevoEstado } : packageItem
      )
    );

    const body: UpdatePackageStatusRequest = { nuevoEstado };

    return this.http.patch<PackageModel>(`${PACKAGES_URL}/${packageId}/estado`, body).pipe(
      tap((updatedPackage) => this.replacePackage(updatedPackage)),
      catchError((error: HttpErrorResponse) => {
        this.packagesState.set(previousSnapshot);
        this.errorState.set(
          error.status === 422
            ? 'Movimiento no permitido por la regla de estados.'
            : 'No fue posible actualizar el estado del paquete.'
        );
        return throwError(() => error);
      }),
      finalize(() => this.updatingPackageIdState.set(null))
    );
  }

  createPackage(packageRequest: CreatePackageRequest): Observable<PackageModel> {
    this.errorState.set(null);
    return this.http.post<PackageModel>(PACKAGES_URL, packageRequest).pipe(
      catchError((error: HttpErrorResponse) => {
        this.errorState.set('No fue posible crear el paquete.');
        return throwError(() => error);
      })
    );
  }

  private replacePackage(updatedPackage: PackageModel): void {
    this.packagesState.update((packages) =>
      packages.map((packageItem) => (packageItem.id === updatedPackage.id ? updatedPackage : packageItem))
    );
  }
}
