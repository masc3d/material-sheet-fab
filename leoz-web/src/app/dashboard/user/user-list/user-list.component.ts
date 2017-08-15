import { Component, OnInit } from '@angular/core';
import { Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';

import { UserService } from '../user.service';
import { User } from '../user.model';
import { MsgService } from '../../../shared/msg/msg.service';
import { TranslateService } from '../../../core/translate/translate.service';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { PermissionCheck } from '../../../core/auth/permission-check';
import { RoleGuard } from '../../../core/auth/role.guard';

@Component( {
  selector: 'app-user-list',
  template: `
    <p-dataTable [value]="users | async | userfilter" resizableColumns="true" [responsive]="true">
      <p-column field="firstName" header="{{'firstname' | translate}}"></p-column>
      <p-column field="lastName" header="{{'surname' | translate}}" [sortable]="true"></p-column>
      <p-column field="role" header="{{'role' | translate}}" [sortable]="true">
        <ng-template let-user="rowData" pTemplate="body">
          {{ translate.role(user.role) }}
        </ng-template>
      </p-column>
      <p-column field="email" header="{{'email' | translate}}" [sortable]="true"></p-column>
      <p-column field="phone" header="{{'phone' | translate}}"></p-column>
      <p-column field="active" header="{{'active' | translate}}" sortable="true">
        <ng-template let-user="rowData" pTemplate="body">
          <span *ngIf="user.active; else inactivePart">
            {{ 'yes' | translate }}
          </span>
          <ng-template #inactivePart>
            {{ 'no' | translate }}
          </ng-template>
        </ng-template>
      </p-column>
      <p-column field="expiresOn" header="{{'expires_on' | translate}}" sortable="true">
        <ng-template let-user="rowData" pTemplate="body">
          {{user.expiresOn | date:dateFormat}}
        </ng-template>
      </p-column>
      <p-column header="">
        <ng-template let-user="rowData" pTemplate="body">
          <i *ngIf="myself(user) || checkPermission(user)" class="fa fa-pencil fa-fw" aria-hidden="true" (click)="selected(user)"></i>
          <i *ngIf="checkPermission(user)" class="fa fa-trash-o fa-fw" aria-hidden="true" (click)="deactivate(user)"></i>
        </ng-template>
      </p-column>
    </p-dataTable>
  `
} )
export class UserListComponent extends AbstractTranslateComponent implements OnInit {

  users: Observable<User[]>;
  dateFormat: string;

  constructor( private userService: UserService,
               private msgService: MsgService,
               public translate: TranslateService,
               private roleGuard: RoleGuard ) {
    super( translate );
  }

  ngOnInit() {
    super.ngOnInit();

    this.deactivate( <User> {} );
    this.selected( <User> {} );
    this.users = this.userService.users;
    this.userService.getUsers();
  }

  selected( selectedUser: User ) {
    this.userService.changeActiveUser( selectedUser );
  }

  deactivate( selectedUser: User ) {
    this.deactivateUser( selectedUser.email );
  }

  protected deactivateUser( originEmail: string ) {
    if (originEmail && originEmail.length > 0) {
      this.userService.update( { active: false }, originEmail )
        .subscribe(
          ( resp: Response ) => {
            if (resp.status === 204) {
              this.msgService.success( 'User successfully deactivated' );
              this.userService.getUsers();
              this.userService.changeActiveUser( <User> {} );
            } else {
              this.msgService.handleResponse( resp );
            }
          },
          ( error: Response ) => {
            this.msgService.handleResponse( error );
          } );
    }
  }

  myself(user: User): boolean {
    return PermissionCheck.myself(user);
  }

  checkPermission(user: User): boolean {
    return PermissionCheck.hasLessPermissions(this.roleGuard.userRole, user.role);
  }
}
