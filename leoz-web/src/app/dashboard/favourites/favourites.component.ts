import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { MsgService } from '../../shared/msg/msg.service';
import { SelectItem } from 'primeng/api';


interface DashboardItem {
  label: string;
  faClass: string;
}

@Component( {
  selector: 'app-favourite',
  styles: [ `
    .dashboard {
      display: grid;
      grid-template-columns: repeat(3, 210px);
      grid-gap: 10px;
    }

    @media (max-width: 681px) {
      .dashboard {
        grid-template-columns: repeat(3, 210px);
      }
    }

    @media (max-width: 680px) {
      .dashboard {
        grid-template-columns: repeat(2, 210px);
      }
    }

    @media (max-width: 460px) {
      .dashboard {
        grid-template-columns: repeat(1, 210px);
      }
    }
  ` ],
  template: `
    <div class="dashboard">
      <app-dashboardbox *ngFor="let item of dashboardboxItems" [label]="item.label"
                        [faClass]="item.faClass"></app-dashboardbox>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class FavouritesComponent extends AbstractTranslateComponent implements OnInit {

  dashboardboxItems: DashboardItem[];

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               public router: Router ) {
    super( translate, cd, msgService, () => {
      this.dashboardboxItems = this.createDashboardboxItems();
    } );
  }

  ngOnInit() {
    super.ngOnInit();
    this.dashboardboxItems = this.createDashboardboxItems();
  }

  private createDashboardboxItems(): DashboardItem[] {
    return [
      { label: this.translate.instant( 'dailybusiness' ), faClass: 'far fa-calendar' },
      { label: this.translate.instant( 'scans' ), faClass: 'fas fa-barcode' },
      { label: this.translate.instant( 'importdispo' ), faClass: 'fas fa-cubes' },
      { label: this.translate.instant( 'shipmentinfo' ), faClass: 'fas fa-cube' },
      { label: this.translate.instant( 'client-management' ), faClass: 'far fa-handshake' },
      { label: this.translate.instant( 'office-management' ), faClass: 'far fa-building' },
      { label: this.translate.instant( 'communication' ), faClass: 'far fa-comments' },
      { label: this.translate.instant( 'accessoires' ), faClass: 'fas fa-wrench' },
      { label: this.translate.instant( 'tracking' ), faClass: 'fas fa-map-marker-alt' }
    ];
  }

}
