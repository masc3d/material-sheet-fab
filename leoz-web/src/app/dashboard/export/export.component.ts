import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/primeng';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';
import { Station } from '../../core/auth/station.model';

@Component( {
  selector: 'app-export',
  template: `
    <p-tabMenu [model]="items"></p-tabMenu>
    <div style="border: 1px solid gray; padding: 5px" class="mbDashboardContent">
        <router-outlet></router-outlet>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class ExportComponent extends AbstractTranslateComponent implements OnInit {
  items: MenuItem[];

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef) {
    super( translate, cd, () => this.items = this.createItems() );
  }

  ngOnInit() {
    super.ngOnInit();
    this.items = this.createItems();
  }

  private createItems(): MenuItem[] {
    const activeStation: Station = JSON.parse( localStorage.getItem( 'activeStation' ) );
    const items = [];

    items.push( {
      label: this.translate.instant( 'loadinglistscan' ),
      icon: '',
      routerLink: 'loadinglistscan'
    } );

    if (activeStation && activeStation.exportValuablesAllowed) {
      items.push( {
        label: this.translate.instant( 'bagscan' ),
        icon: '',
        routerLink: 'bagscan'
      } );
    }

    return items;
  }
}

