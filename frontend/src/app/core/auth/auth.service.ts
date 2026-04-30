import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

import { LoginRequest, LoginResponse, UserRole } from '../models/auth.model';

const LOGIN_URL = 'http://localhost:8080/api/auth/login';
const TOKEN_STORAGE_KEY = 'smartship_token';
const ROLE_STORAGE_KEY = 'smartship_role';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenState = signal<string | null>(this.readStorage(TOKEN_STORAGE_KEY));
  private readonly roleState = signal<UserRole | null>(this.readRole());

  readonly token = this.tokenState.asReadonly();
  readonly role = this.roleState.asReadonly();
  readonly isAuthenticated = computed(() => Boolean(this.token()));
  readonly isAdmin = computed(() => this.role() === 'ADMINISTRADOR');
  readonly isRepartidor = computed(() => this.role() === 'REPARTIDOR');

  login(credentials: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(LOGIN_URL, credentials).pipe(
      tap((response) => this.storeSession(response))
    );
  }

  logout(): void {
    this.tokenState.set(null);
    this.roleState.set(null);
    this.removeStorage(TOKEN_STORAGE_KEY);
    this.removeStorage(ROLE_STORAGE_KEY);
  }

  private storeSession(response: LoginResponse): void {
    this.tokenState.set(response.token);
    this.roleState.set(response.rol);
    this.writeStorage(TOKEN_STORAGE_KEY, response.token);
    this.writeStorage(ROLE_STORAGE_KEY, response.rol);
  }

  private readRole(): UserRole | null {
    const storedRole = this.readStorage(ROLE_STORAGE_KEY);

    if (storedRole === 'ADMINISTRADOR' || storedRole === 'REPARTIDOR') {
      return storedRole;
    }

    return null;
  }

  private readStorage(key: string): string | null {
    try {
      return localStorage.getItem(key);
    } catch {
      return null;
    }
  }

  private writeStorage(key: string, value: string): void {
    try {
      localStorage.setItem(key, value);
    } catch {
      // The in-memory signal state still keeps the user authenticated for this session.
    }
  }

  private removeStorage(key: string): void {
    try {
      localStorage.removeItem(key);
    } catch {
      // Storage can be unavailable in restricted browser modes.
    }
  }
}
