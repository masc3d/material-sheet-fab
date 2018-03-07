import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { TranslateService } from '../../core/translate/translate.service';
import { MsgService } from '../../shared/msg/msg.service';
import { AbstractTranslateComponent } from '../../core/translate/abstract-translate.component';

@Component( {
  selector: 'app-touroptimizing',
  template: `
    <p-tabMenu [model]="items"></p-tabMenu>
    <div style="background-color: #efefef8c;" class="mbDashboardContent">
        <router-outlet></router-outlet>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class TouroptimizingComponent extends AbstractTranslateComponent implements OnInit {
  items: MenuItem[];
  constructor( protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService ) {
    super( translate, cd, msgService, () => this.items = this.createItems() );
  }

  ngOnInit() {
    super.ngOnInit();

    this.items = this.createItems();
  }

  private createItems(): MenuItem[] {
    const items = [];

    items.push( {
      label: this.translate.instant( 'optimization-tour' ),
      icon: '',
      routerLink: 'officedispo'
    } );

    /*items.push( {
      label: this.translate.instant( 'driverdispo' ),
      icon: '',
      routerLink: 'driverdispo'
    } );*/

    return items;
  }
}
