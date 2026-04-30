import { Component, input } from '@angular/core';

import { PackageModel } from '../../core/models/package.model';

@Component({
  selector: 'app-package-card',
  standalone: true,
  templateUrl: './package-card.component.html',
  styleUrl: './package-card.component.css'
})
export class PackageCardComponent {
  readonly packageItem = input.required<PackageModel>();
}
