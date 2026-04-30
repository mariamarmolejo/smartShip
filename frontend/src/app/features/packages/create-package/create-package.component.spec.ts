import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter, Router } from '@angular/router';

import { CreatePackageComponent } from './create-package.component';

describe('CreatePackageComponent', () => {
  let fixture: ComponentFixture<CreatePackageComponent>;
  let component: CreatePackageComponent;
  let httpMock: HttpTestingController;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreatePackageComponent],
      providers: [provideHttpClient(), provideHttpClientTesting(), provideRouter([])]
    }).compileComponents();

    fixture = TestBed.createComponent(CreatePackageComponent);
    component = fixture.componentInstance;
    httpMock = TestBed.inject(HttpTestingController);
    router = TestBed.inject(Router);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('does not submit when form is invalid', () => {
    component.submit();

    httpMock.expectNone('http://localhost:8080/api/paquetes');
    expect(component.errorMessage()).toBe('Completa los campos obligatorios con valores validos.');
  });

  it('calls POST when form is valid', () => {
    spyOn(router, 'navigate').and.resolveTo(true);
    fillValidForm();

    component.submit();

    const request = httpMock.expectOne('http://localhost:8080/api/paquetes');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      peso: 3.5,
      dimensiones: {
        largo: 20,
        ancho: 12,
        alto: 8
      },
      destinatario: 'Cliente Uno'
    });
    request.flush(buildCreatedPackage());
  });

  it('navigates to board after successful creation', () => {
    const navigateSpy = spyOn(router, 'navigate').and.resolveTo(true);
    fillValidForm();

    component.submit();
    httpMock.expectOne('http://localhost:8080/api/paquetes').flush(buildCreatedPackage());

    expect(navigateSpy).toHaveBeenCalledWith(['/board']);
  });

  it('shows Spanish error message when backend returns error', () => {
    fillValidForm();

    component.submit();
    httpMock.expectOne('http://localhost:8080/api/paquetes').flush(
      { message: 'Validacion fallida' },
      { status: 400, statusText: 'Bad Request' }
    );

    expect(component.errorMessage()).toBe('No fue posible crear el paquete. Revisa los datos e intenta de nuevo.');
  });

  function fillValidForm(): void {
    component.packageForm.setValue({
      peso: 3.5,
      dimensiones: {
        largo: 20,
        ancho: 12,
        alto: 8
      },
      destinatario: ' Cliente Uno '
    });
  }

  function buildCreatedPackage(): object {
    return {
      id: 10,
      trackingId: 'PKG-10',
      peso: 3.5,
      dimensiones: {
        largo: 20,
        ancho: 12,
        alto: 8
      },
      destinatario: 'Cliente Uno',
      estado: 'RECIBIDO',
      creadoEn: '2026-04-30T09:00:00',
      actualizadoEn: '2026-04-30T09:00:00'
    };
  }
});
