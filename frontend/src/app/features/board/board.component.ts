import { CdkDragDrop, DragDropModule } from '@angular/cdk/drag-drop';
import { Component, OnInit, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth/auth.service';
import { PackageModel, PackageStatus } from '../../core/models/package.model';
import { PackageCardComponent } from './package-card.component';
import { PackageService } from './package.service';

interface BoardColumn {
  status: PackageStatus;
  title: string;
}

@Component({
  selector: 'app-board',
  standalone: true,
  imports: [DragDropModule, PackageCardComponent, RouterLink],
  providers: [PackageService],
  templateUrl: './board.component.html',
  styleUrl: './board.component.css'
})
export class BoardComponent implements OnInit {
  protected readonly authService = inject(AuthService);
  protected readonly packageService = inject(PackageService);
  private readonly router = inject(Router);

  protected readonly columns: readonly BoardColumn[] = [
    { status: 'RECIBIDO', title: 'Recibido' },
    { status: 'EN_TRANSITO', title: 'En transito' },
    { status: 'ENTREGADO', title: 'Entregado' }
  ];

  protected readonly dropListIds = this.columns.map((column) => this.dropListId(column.status));

  ngOnInit(): void {
    this.packageService.loadPackages().subscribe({
      error: () => {
        // The service owns the visible error signal.
      }
    });
  }

  drop(event: CdkDragDrop<PackageModel[]>, targetStatus: PackageStatus): void {
    const packageItem = event.item.data as PackageModel;

    if (packageItem.estado === targetStatus) {
      return;
    }

    this.packageService.updateStatus(packageItem.id, targetStatus).subscribe({
      error: () => {
        // The service rolls back and exposes the error message.
      }
    });
  }

  dropListId(status: PackageStatus): string {
    return `packages-${status}`;
  }

  logout(): void {
    this.authService.logout();
    void this.router.navigate(['/login']);
  }
}
