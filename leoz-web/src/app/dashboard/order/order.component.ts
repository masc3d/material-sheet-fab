import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/primeng';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';

@Component( {
  selector: 'app-order',
  template: `
    <p-tabMenu [model]="items"></p-tabMenu>
    <div style="border: 1px solid gray; padding: 5px" class="mbDashboardContent">
        <router-outlet></router-outlet>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class OrderComponent extends AbstractTranslateComponent implements OnInit {

  items: MenuItem[];

  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef ) {
    super( translate, cd, () => this.items = this.createItems() );
  }

  ngOnInit() {
    super.ngOnInit();
    this.items = this.createItems();
  }

  private createItems(): MenuItem[] {
    const items = [];

    items.push( {
      label: this.translate.instant( 'orderform' ),
      icon: '',
      routerLink: 'orderform'
    } );

    items.push( {
      label: this.translate.instant( 'orderlist' ),
      icon: '',
      routerLink: 'orderlist'
    } );

    items.push( {
      label: this.translate.instant( 'orderprofile' ),
      icon: '',
      routerLink: 'orderprofile'
    } );

    return items;
  }
}

