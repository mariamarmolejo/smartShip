import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter, Router, UrlTree } from '@angular/router';

import { authGuard } from './auth.guard';
import { AuthService } from './auth.service';

describe('authGuard', () => {
  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideRouter([])]
    });
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('redirects unauthenticated users to login', () => {
    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as never, { url: '/board' } as never)
    );

    expect(result instanceof UrlTree).toBeTrue();
    expect(TestBed.inject(Router).serializeUrl(result as UrlTree)).toBe('/login?returnUrl=%2Fboard');
  });

  it('allows authenticated users', async () => {
    localStorage.setItem('smartship_token', 'jwt-token');
    localStorage.setItem('smartship_role', 'REPARTIDOR');

    const authService = TestBed.inject(AuthService);
    expect(authService.isAuthenticated()).toBeTrue();

    const result = TestBed.runInInjectionContext(() =>
      authGuard({} as never, { url: '/board' } as never)
    );

    expect(result).toBeTrue();
  });
});
