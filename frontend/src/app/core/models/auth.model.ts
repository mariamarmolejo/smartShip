export type UserRole = 'ADMINISTRADOR' | 'REPARTIDOR';

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  rol: UserRole;
}
