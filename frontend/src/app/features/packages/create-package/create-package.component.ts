import { Component } from '@angular/core';

@Component({
  selector: 'app-create-package',
  standalone: true,
  template: `
    <main class="page">
      <p>SmartShip</p>
      <h1>Crear paquete</h1>
      <section>El formulario de creacion de paquetes se implementara en una siguiente tarea.</section>
    </main>
  `,
  styles: `
    :host {
      color: #172033;
      display: block;
      font-family: Arial, Helvetica, sans-serif;
      min-height: 100vh;
    }

    .page {
      margin: 0 auto;
      max-width: 900px;
      padding: 32px 24px;
    }

    p {
      color: #2f6f9f;
      font-size: 0.78rem;
      font-weight: 700;
      margin: 0;
      text-transform: uppercase;
    }

    h1 {
      font-size: 1.8rem;
      line-height: 1.2;
      margin: 6px 0 24px;
    }

    section {
      border-top: 1px solid #d7deea;
      padding-top: 24px;
    }
  `
})
export class CreatePackageComponent {}
