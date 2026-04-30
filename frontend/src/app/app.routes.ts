import { Routes } from '@angular/router';

import { authGuard } from './core/auth/auth.guard';
import { adminGuard } from './core/auth/role.guard';

export const routes: Routes = [
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then((component) => component.LoginComponent)
  },
  {
    path: 'board',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/board/board.component').then((component) => component.BoardComponent)
  },
  {
    path: 'packages/create',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./features/packages/create-package/create-package.component').then(
        (component) => component.CreatePackageComponent
      )
  },
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'board'
  },
  {
    path: '**',
    redirectTo: 'board'
  }
];
