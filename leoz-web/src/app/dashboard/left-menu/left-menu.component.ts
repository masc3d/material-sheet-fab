import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnInit, Renderer2 } from '@angular/core';
import { DOCUMENT } from '@angular/common';
import { Router } from '@angular/router';
import { takeUntil } from 'rxjs/operators';

import { MenuItem, SelectItem } from 'primeng/api';

import { RoleGuard } from '../../core/auth/role.guard';
import { TranslateService } from 'app/core/translate/translate.service';
import { AbstractTranslateComponent } from 'app/core/translate/abstract-translate.component';
import { environment } from '../../../environments/environment';
import { AuthenticationService } from '../../core/auth/authentication.service';
import { Station } from '../../core/auth/station.model';
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
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
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
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
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
      routerLink: 'home',
      command: closeMenu
    } );

    if (this.roleGuard.isDriver() || this.roleGuard.isPoweruser() || this.roleGuard.isUser()) {
      items.push( {
        label: this.translate.instant( 'dispo' ),
        items: [
          {
            label: this.translate.instant( 'tour' ),
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
            routerLink: 'user',
            command: closeMenu
          }
        ]
      } );
    }

    items.push( {
      label: this.translate.instant( 'logout' ),
      routerLink: '/logout',
      command: closeMenu
    } );

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
      routerLink: 'home',
      command: closeMenu
    } );

    items.push( {
      label: this.translate.instant( 'orders' ),
      items: [
        {
          label: this.translate.instant( 'recording' ),
          routerLink: 'order/orderform',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'action dispatch' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'standing orders' ),
          routerLink: '/',
          command: closeMenu
        }
      ]
    } );

    items.push( {
      label: this.translate.instant( 'disposition' ),
      items: [
        {
          label: this.translate.instant( 'inbound disposition' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'tour planning' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'tour optimization' ),
          routerLink: 'touroptimizing/officedispo',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'outbound disposition' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'reprint stoplist' ),
          routerLink: '/',
          command: closeMenu
        }
      ]
    } );

    items.push( {
      label: this.translate.instant( 'scanning' ),
      items: [
        {
          label: this.translate.instant( 'inbound scan' ),
          routerLink: 'importscan/importscanquick',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'outbound scan' ),
          routerLink: 'export/loadinglistscan',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'depot entry' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'stoplist scan' ),
          routerLink: '/',
          command: closeMenu
        }
      ]
    } );

    items.push( {
      label: this.translate.instant( 'track & trace' ),
      items: [
        {
          label: this.translate.instant( 'shipment information' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'vehicle tracing' ),
          routerLink: 'tour',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'stoplist backinput' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'shipment cockpit' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'hiroglyph t&t' ),
          routerLink: '/',
          command: closeMenu
        }
      ]
    } );

    items.push( {
      label: this.translate.instant( 'administration' ),
      items: [
        {
          label: this.translate.instant( 'customer administration' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'user administration' ),
          routerLink: 'user',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'addresspool' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'mobileX' ),
          routerLink: '/',
          command: closeMenu
        }
      ]
    } );

    const settingsItems = [
      {
        label: this.translate.instant( 'startpage' ),
        routerLink: 'favourites',
        command: closeMenu
      },
      {
        label: this.translate.instant( 'communication' ),
        routerLink: '/',
        command: closeMenu
      }];
    if (this.electronService.isElectron()) {
      settingsItems.push( {
        label: this.translate.instant( 'printer administration' ),
        routerLink: '/',
        command: closeMenu
      } );
    }
    settingsItems.push({
        label: this.translate.instant( 'comports' ),
        routerLink: '/',
        command: closeMenu
      });
    settingsItems.push({
        label: this.translate.instant( 'interfaces' ),
        routerLink: '/',
        command: closeMenu
      });
    items.push( {
      label: this.translate.instant( 'settings' ),
      items: settingsItems
    } );
    items.push( {
      label: this.translate.instant( 'addons' ),
      items: [
        {
          label: this.translate.instant( 'routing' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'stationfinder' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'datafilter' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'print lists' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'search location' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'export/import data' ),
          routerLink: '/',
          command: closeMenu
        },
        {
          label: this.translate.instant( 'request data' ),
          routerLink: '/',
          command: closeMenu
        }
      ]
    } );

    items.push( {
      label: this.translate.instant( 'statistics' ),
      items: [
        {
          label: this.translate.instant( 'quality' ),
          routerLink: '/',
          command: closeMenu
        }
      ]
    } );

    items.push( {
      label: this.translate.instant( 'logout' ),
      routerLink: '/logout',
      command: closeMenu
    } );

    return items;
  }
}
