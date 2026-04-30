import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs';

import { CreatePackageRequest } from '../../../core/models/package.model';
import { PackageService } from '../../board/package.service';

@Component({
  selector: 'app-create-package',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  providers: [PackageService],
  templateUrl: './create-package.component.html',
  styleUrl: './create-package.component.css'
})
export class CreatePackageComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly packageService = inject(PackageService);
  private readonly router = inject(Router);

  readonly isSubmitting = signal(false);
  readonly errorMessage = signal<string | null>(null);

  readonly packageForm = this.formBuilder.nonNullable.group({
    peso: [0, [Validators.required, Validators.min(0.01)]],
    dimensiones: this.formBuilder.nonNullable.group({
      largo: [0, [Validators.required, Validators.min(0.01)]],
      ancho: [0, [Validators.required, Validators.min(0.01)]],
      alto: [0, [Validators.required, Validators.min(0.01)]]
    }),
    destinatario: ['', [Validators.required]]
  });

  submit(): void {
    this.errorMessage.set(null);

    if (this.packageForm.invalid) {
      this.packageForm.markAllAsTouched();
      this.errorMessage.set('Completa los campos obligatorios con valores validos.');
      return;
    }

    this.isSubmitting.set(true);
    this.packageService
      .createPackage(this.buildRequest())
      .pipe(finalize(() => this.isSubmitting.set(false)))
      .subscribe({
        next: () => {
          void this.router.navigate(['/board']);
        },
        error: () => {
          this.errorMessage.set('No fue posible crear el paquete. Revisa los datos e intenta de nuevo.');
        }
      });
  }

  private buildRequest(): CreatePackageRequest {
    const formValue = this.packageForm.getRawValue();

    return {
      peso: Number(formValue.peso),
      dimensiones: {
        largo: Number(formValue.dimensiones.largo),
        ancho: Number(formValue.dimensiones.ancho),
        alto: Number(formValue.dimensiones.alto)
      },
      destinatario: formValue.destinatario.trim()
    };
  }
}
