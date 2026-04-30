import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { PackageModel } from '../../core/models/package.model';
import { PackageService } from './package.service';

describe('PackageService', () => {
  let service: PackageService;
  let httpMock: HttpTestingController;

  const packages: PackageModel[] = [
    buildPackage(1, 'REC-1', 'RECIBIDO'),
    buildPackage(2, 'TRA-1', 'EN_TRANSITO'),
    buildPackage(3, 'ENT-1', 'ENTREGADO')
  ];

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PackageService, provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(PackageService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('loads packages', () => {
    service.loadPackages().subscribe();

    const request = httpMock.expectOne('http://localhost:8080/api/paquetes');
    expect(request.request.method).toBe('GET');
    request.flush(packages);

    expect(service.packages()).toEqual(packages);
  });

  it('updates package status', () => {
    service.loadPackages().subscribe();
    httpMock.expectOne('http://localhost:8080/api/paquetes').flush(packages);

    service.updateStatus(1, 'EN_TRANSITO').subscribe();

    const request = httpMock.expectOne('http://localhost:8080/api/paquetes/1/estado');
    expect(request.request.method).toBe('PATCH');
    expect(request.request.body).toEqual({ nuevoEstado: 'EN_TRANSITO' });
    request.flush({ ...packages[0], estado: 'EN_TRANSITO' });

    expect(service.packages()[0].estado).toBe('EN_TRANSITO');
  });

  it('groups packages by status with a computed signal', () => {
    service.loadPackages().subscribe();
    httpMock.expectOne('http://localhost:8080/api/paquetes').flush(packages);

    expect(service.packagesByStatus().RECIBIDO.length).toBe(1);
    expect(service.packagesByStatus().EN_TRANSITO.length).toBe(1);
    expect(service.packagesByStatus().ENTREGADO.length).toBe(1);
  });

  it('rolls back optimistic update when status update returns 422', () => {
    service.loadPackages().subscribe();
    httpMock.expectOne('http://localhost:8080/api/paquetes').flush(packages);

    service.updateStatus(1, 'ENTREGADO').subscribe({
      error: () => {
        // Expected for invalid state transition.
      }
    });

    expect(service.packages()[0].estado).toBe('ENTREGADO');

    httpMock.expectOne('http://localhost:8080/api/paquetes/1/estado').flush(
      { message: 'Transicion no permitida' },
      { status: 422, statusText: 'Unprocessable Entity' }
    );

    expect(service.packages()[0].estado).toBe('RECIBIDO');
    expect(service.errorMessage()).toBe('Movimiento no permitido por la regla de estados.');
  });
});

function buildPackage(id: number, trackingId: string, estado: PackageModel['estado']): PackageModel {
  return {
    id,
    trackingId,
    peso: 2.5,
    dimensiones: {
      largo: 10,
      ancho: 8,
      alto: 6
    },
    destinatario: `Cliente ${id}`,
    estado,
    creadoEn: '2026-04-30T09:00:00',
    actualizadoEn: '2026-04-30T09:00:00'
  };
}
