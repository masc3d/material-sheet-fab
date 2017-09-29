import { Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/primeng';

import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';
import { TranslateService } from '../../core/translate/translate.service';

@Component( {
  selector: 'app-stationloading',
  template: `
    <p-tabMenu [model]="items"></p-tabMenu>
    <div style="border: 1px solid gray; padding: 5px">
        <router-outlet></router-outlet>
    </div>
  `
} )

export class StationloadingComponent extends AbstractTranslateComponent implements OnInit {
  items: MenuItem[];

  constructor( protected translate: TranslateService ) {
    super( translate, () => this.items = this.createItems() );
  }

  ngOnInit() {
    super.ngOnInit();
    this.items = this.createItems();
  }

  private createItems(): MenuItem[] {
    const items = [];

    items.push( {
      label: this.translate.instant( 'loadinglistscan' ),
      icon: '',
      routerLink: 'loadinglistscan'
    } );

    items.push( {
      label: this.translate.instant( 'bagscan' ),
      icon: '',
      routerLink: 'bagscan'
    } );

    return items;
  }
}

