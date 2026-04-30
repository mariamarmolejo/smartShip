import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';

import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('stores token and role after login', () => {
    service.login({ username: 'admin', password: 'secret' }).subscribe();

    const request = httpMock.expectOne('http://localhost:8080/api/auth/login');
    expect(request.request.method).toBe('POST');
    request.flush({ token: 'jwt-token', rol: 'ADMINISTRADOR' });

    expect(service.token()).toBe('jwt-token');
    expect(service.role()).toBe('ADMINISTRADOR');
    expect(service.isAuthenticated()).toBeTrue();
    expect(service.isAdmin()).toBeTrue();
    expect(localStorage.getItem('smartship_token')).toBe('jwt-token');
    expect(localStorage.getItem('smartship_role')).toBe('ADMINISTRADOR');
  });
});
