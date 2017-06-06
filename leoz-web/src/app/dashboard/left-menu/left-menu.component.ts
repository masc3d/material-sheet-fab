import { Component, Inject, Renderer2 } from '@angular/core';
import { DOCUMENT } from '@angular/platform-browser';

import { MenuItem } from 'primeng/primeng';

import { RoleGuard } from '../../core/auth/role.guard';
import { TranslateService } from "app/core/translate/translate.service";
import { AbstractTranslateComponent } from "app/core/translate/abstract-translate.component";

@Component( {
  selector: 'app-left-menu',
  template: `
    <div id="main-menu">
      <p-panelMenu [model]="items"></p-panelMenu>
    </div>
  `

} )
export class LeftMenuComponent extends AbstractTranslateComponent {

  items: MenuItem[];

  constructor( private renderer: Renderer2,
               @Inject( DOCUMENT ) private document: any,
               private roleGuard: RoleGuard,
               protected translate: TranslateService ) {
    super(translate, () => this.items = this.createItems());
  }

  ngOnInit() {
    super.ngOnInit();
    this.items = this.createItems();
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
            label: this.translate.instant( 'users' ),
            icon: 'fa-wheelchair',
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

    return items;
  }
}
