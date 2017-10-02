import { Component, Inject, OnInit, Renderer2 } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';

import { MenuItem } from 'primeng/primeng';

import { RoleGuard } from '../../core/auth/role.guard';
import { TranslateService } from 'app/core/translate/translate.service';
import { AbstractTranslateComponent } from 'app/core/translate/abstract-translate.component';
import { environment } from '../../../environments/environment';

@Component( {
  selector: 'app-left-menu',
  template: `
    <div id="main-menu">
      <p-panelMenu [model]="items"></p-panelMenu>
      <div style="margin-top: 15px; margin-left: 10px">
        {{ 'loggedinas' | translate }}:<br>{{myEmail}}
      </div>
    </div>
  `

} )

export class LeftMenuComponent extends AbstractTranslateComponent implements OnInit {

  private usedMenu = `${environment.defMenu}`;

  items: MenuItem[];
  myEmail: string;

  constructor( private renderer: Renderer2,
               @Inject( DOCUMENT ) private document: any,
               private roleGuard: RoleGuard,
               protected translate: TranslateService ) {
    super( translate, () => {
      // this.items = this.createItems();
      if (this.usedMenu === 'leoz') {
        this.items = this.createItems();
      } else if (this.usedMenu === 'leo-old') {
        this.items = this.createItemsLeo1();
      }
    } );
  }

  ngOnInit() {
    super.ngOnInit();
    if (this.usedMenu === 'leoz') {
      this.items = this.createItems();
    } else if (this.usedMenu === 'leo-old') {
      this.items = this.createItemsLeo1();
    }
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    this.myEmail = currUser.user.email;
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
            icon: 'fa fa-bus',
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
            icon: 'fa-smile-o',
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
          routerLink: '',
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
          routerLink: '',
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
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'i-point' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'loadingscan' ),
          icon: '',
          routerLink: '/dashboard/stationloading/loadinglistscan',
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
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'driver' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'deliverydispo' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'deliveryscan' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'driver-assignment' ),
          icon: '',
          routerLink: '',
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
          routerLink: '',
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
      items.push( {
        label: this.translate.instant( 'office-management' ),
        items: [
          {
            label: this.translate.instant( 'co-worker' ),
            icon: 'fa-smile-o',
            routerLink: 'user',
            command: closeMenu
          },
          {
            label: this.translate.instant( 'printer-setup' ),
            icon: '',
            routerLink: '',
            command: closeMenu
          },
          {
            label: this.translate.instant( 'password-management' ),
            icon: '',
            routerLink: '',
            command: closeMenu
          }
        ]
      } );
    }

    items.push( {
      label: this.translate.instant( 'communication' ),
      items: [
        {
          label: this.translate.instant( 'directinfo-to-partner' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'supportrequest' ),
          icon: '',
          routerLink: '',
          command: closeMenu
        },
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
        },
        {
          label: this.translate.instant( 'calendar' ),
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
            icon: 'fa fa-bus',
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
