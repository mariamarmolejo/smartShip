export type PackageStatus = 'RECIBIDO' | 'EN_TRANSITO' | 'ENTREGADO';

export interface PackageDimensions {
  largo: number;
  ancho: number;
  alto: number;
}

export interface PackageModel {
  id: number;
  trackingId: string;
  peso: number;
  dimensiones: PackageDimensions;
  destinatario: string;
  estado: PackageStatus;
  creadoEn: string;
  actualizadoEn: string;
}

export interface UpdatePackageStatusRequest {
  nuevoEstado: PackageStatus;
}

export interface CreatePackageRequest {
  peso: number;
  dimensiones: PackageDimensions;
  destinatario: string;
}

export type PackagesByStatus = Record<PackageStatus, PackageModel[]>;
