import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnInit, Renderer2 } from '@angular/core';
import { DOCUMENT } from '@angular/common';

import { MenuItem, SelectItem } from 'primeng/api';

import { RoleGuard } from '../../core/auth/role.guard';
import { TranslateService } from 'app/core/translate/translate.service';
import { AbstractTranslateComponent } from 'app/core/translate/abstract-translate.component';
import { environment } from '../../../environments/environment';
import { AuthenticationService } from '../../core/auth/authentication.service';
import { Station } from '../../core/auth/station.model';
import { Router } from '@angular/router';
import { BagscanGuard } from '../../core/auth/bagscan.guard';
import { ElectronService } from '../../core/electron/electron.service';
import { MsgService } from '../../shared/msg/msg.service';

@Component( {
  selector: 'app-left-menu',
  template: `
    <div id="main-menu">
      <p-panelMenu [model]="items"></p-panelMenu>
      <div style="margin-top: 15px; margin-left: 10px">
        {{ 'loggedinas' | translate }}:<br>{{myEmail}}
      </div>
      <div *ngIf="debitorStations.length === 1" style="margin: 15px 0 35px 10px;">
        {{ 'forStation' | translate }}:<br>{{activeStation.stationNo}}
      </div>
      <div *ngIf="debitorStations.length > 1" style="margin: 15px 0 35px 10px;">
        {{ 'forStation' | translate }}:<br>
        <p-dropdown [options]="debitorStations" [ngModel]="activeStation"
                    (onChange)="changeActiveStation($event.value)"></p-dropdown>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )

export class LeftMenuComponent extends AbstractTranslateComponent implements OnInit {

  private usedMenu = `${environment.defMenu}`;

  items: MenuItem[];
  myEmail: string;

  debitorStations: SelectItem[];
  activeStation: Station;

  constructor( private renderer: Renderer2,
               @Inject( DOCUMENT ) private document: any,
               private roleGuard: RoleGuard,
               private auth: AuthenticationService,
               private router: Router,
               private bagscanGuard: BagscanGuard,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               protected translate: TranslateService,
               private electronService: ElectronService ) {
    super( translate, cd, msgService, () => {
      if (this.usedMenu === 'leoz') {
        this.items = this.createItems();
      } else if (this.usedMenu === 'leo-old') {
        this.items = this.createItemsLeo1();
      }
    } );
  }

  ngOnInit() {
    super.ngOnInit();
    this.debitorStations = [];
    if (this.usedMenu === 'leoz') {
      this.items = this.createItems();
    } else if (this.usedMenu === 'leo-old') {
      this.items = this.createItemsLeo1();
    }
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    this.myEmail = currUser.user.email;

    this.auth.debitorStations$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( debitorStations: Station[] ) => {
        this.debitorStations = [];
        if (debitorStations) {
          debitorStations.forEach( ( station: Station ) => {
            this.debitorStations.push( { label: station.stationNo.toString(), value: station } );
          } );
        }
        this.cd.markForCheck();
      } );

    this.auth.activeStation$
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( activeStation: Station ) => {
        this.activeStation = activeStation;
      } );
  }

  changeActiveStation( selected: Station ) {
    this.auth.changeActiveStation( selected );
    this.activeStation = selected;
    this.bagscanGuard.activeStation = selected;
    this.router.navigate( [ 'dashboard/home' ] );
  }

  private createItems(): MenuItem[] {
    const closeMenu = () => {
      if (this.document && this.document.body) {
        this.renderer.removeClass( this.document.body, 'isOpenMenu' );
        this.renderer.setProperty( this.document.body, 'scrollTop', 0 );
      }
    };

    const items = [];

    items.push( {
      label: this.translate.instant( 'home' ),
      icon: '',
      routerLink: 'home',
      command: closeMenu
    } );

    if (this.roleGuard.isDriver() || this.roleGuard.isPoweruser() || this.roleGuard.isUser()) {
      items.push( {
        label: this.translate.instant( 'dispo' ),
        items: [
          {
            label: this.translate.instant( 'tour' ),
            icon: '',
            routerLink: 'tour',
            command: closeMenu
          }
        ]
      } );
    }

    if (this.roleGuard.isPoweruser() || this.roleGuard.isUser()) {
      items.push( {
        label: this.translate.instant( 'management' ),
        items: [
          {
            label: this.translate.instant( 'co-worker' ),
            icon: '',
            routerLink: 'user',
            command: closeMenu
          }
        ]
      } );
    }

    items.push( {
      label: this.translate.instant( 'logout' ),
      icon: '',
      routerLink: '/logout',
      command: closeMenu
    } );

    // items.push( {
    //   label: this.translate.instant( 'stationloading' ),
    //   icon: '',
    //   routerLink: '/dashboard/stationloading/loadinglistscan',
    //   command: closeMenu
    // } );

    return items;
  }

  private createItemsLeo1(): MenuItem[] {
    const closeMenu = () => {
      if (this.document && this.document.body) {
        this.renderer.removeClass( this.document.body, 'isOpenMenu' );
        this.renderer.setProperty( this.document.body, 'scrollTop', 0 );
      }
    };

    const items = [];

    items.push( {
      label: this.translate.instant( 'home' ),
      icon: '',
      routerLink: 'home',
      command: closeMenu
    } );

    items.push( {
      label: this.translate.instant( 'dailybusiness' ),
      items: [
        {
          label: this.translate.instant( 'record-order' ),
          icon: '',
          routerLink: '/dashboard/order/orderform',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'lists' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'dispo' ),
          icon: '',
          routerLink: '/dashboard/pickupdispo',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'shipmentcockpit' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        }
      ]
    } );

    items.push( {
      label: this.translate.instant( 'scans' ),
      items: [
        {
          label: this.translate.instant( 'importscan' ),
          icon: '',
          routerLink: '/dashboard/importscan/importscanquick',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'i-point' ),
          icon: '',
          routerLink: '/dashboard/ipointscan/ipointscanlist',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'loadingscan' ),
          icon: '',
          routerLink: '/dashboard/export/loadinglistscan',
          command: closeMenu
        },
      ]
    } );

    items.push( {
      label: this.translate.instant( 'importdispo' ),
      items: [
        {
          label: this.translate.instant( 'tourplanning' ),
          icon: '',
          routerLink: '/dashboard/tourzipmapping',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'touroptimizing' ),
          icon: '',
          routerLink: '/dashboard/touroptimizing/officedispo',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'deliverydispo' ),
          icon: '',
          routerLink: '/dashboard/deliverydispo',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'deliveryscan' ),
          icon: '',
          routerLink: '/dashboard/deliveryscan',
          command: closeMenu
        },
      ]
    } );

    items.push( {
      label: this.translate.instant( 'shipmentinfo' ),
      items: [
        {
          label: this.translate.instant( 'shipmentstatus' ),
          icon: '',
          routerLink: '/dashboard/stateofshipments',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'datafilter' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'record-delivery-data' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'statistics' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'pod-reprints' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
      ]
    } );

    items.push( {
      label: this.translate.instant( 'client-management' ),
      items: [
        {
          label: this.translate.instant( 'client-data' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'addresspool' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'periodic-shipments' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'promotion-shipments' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
      ]
    } );

    if (this.roleGuard.isPoweruser() || this.roleGuard.isUser()) {
      const officeManagementItems = [
        {
          label: this.translate.instant( 'co-worker' ),
          icon: '',
          routerLink: 'user',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'favourites' ),
          icon: '',
          routerLink: 'favourites',
          command: closeMenu
        } ];
      if (this.electronService.isElectron()) {
        officeManagementItems.push( {
          label: this.translate.instant( 'printer-setup' ),
          icon: '',
          routerLink: 'printers',
          command: closeMenu
        } );
      }
      officeManagementItems.push( {
        label: this.translate.instant( 'password-management' ),
        icon: '',
        routerLink: '',
        command: closeMenu
      } );
      items.push( {
        label: this.translate.instant( 'office-management' ),
        items: officeManagementItems
      } );
    }

    items.push( {
      label: this.translate.instant( 'communication' ),
      items: [
        {
          label: this.translate.instant( 'notification' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        }
      ]
    } );

    items.push( {
      label: this.translate.instant( 'accessoires' ),
      items: [
        {
          label: this.translate.instant( 'stationlist' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'townsearch' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'dataexport' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'documents' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        }
      ]
    } );

    if (this.roleGuard.isDriver() || this.roleGuard.isPoweruser() || this.roleGuard.isUser()) {
      items.push( {
        label: this.translate.instant( 'tracking' ),
        items: [
          {
            label: this.translate.instant( 'tour' ),
            icon: '',
            routerLink: 'tour',
            command: closeMenu
          }
        ]
      } );
    }

    items.push( {
      label: this.translate.instant( 'logout' ),
      icon: '',
      routerLink: '/logout',
      command: closeMenu
    } );

    // items.push( {
    //   label: this.translate.instant( 'stationloading' ),
    //   icon: '',
    //   routerLink: '/dashboard/stationloading/loadinglistscan',
    //   command: closeMenu
    // } );

    return items;
  }
}
