import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { SortMeta } from 'primeng/api';

import { UserService } from '../user.service';
import { User } from '../user.model';
import { MsgService } from '../../../shared/msg/msg.service';
import { TranslateService } from '../../../core/translate/translate.service';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { PermissionCheck } from '../../../core/auth/permission-check';
import { RoleGuard } from '../../../core/auth/role.guard';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component( {
  selector: 'app-user-list',
  template: `
    <p-table [value]="users$ | async | userfilter" [resizableColumns]="true"
             [responsive]="true" sortField="lastName">
        <ng-template pTemplate="header">
          <tr>
            <th [pSortableColumn]="'firstName'" pResizableColumn>
              {{'firstname' | translate}}
              <p-sortIcon [field]="'firstName'"></p-sortIcon>
            </th>
            <th [pSortableColumn]="'lastName'" pResizableColumn>
              {{'surname' | translate}}
              <p-sortIcon [field]="'lastName'"></p-sortIcon>
            </th>
            <th [pSortableColumn]="'role'" pResizableColumn>
              {{'role' | translate}}
              <p-sortIcon [field]="'role'"></p-sortIcon>
            </th>
            <th [pSortableColumn]="'email'" pResizableColumn>
              {{'email' | translate}}
              <p-sortIcon [field]="'email'"></p-sortIcon>
            </th>
            <th [pSortableColumn]="'phone'" pResizableColumn>
              {{'phoneoffice' | translate}}
              <p-sortIcon [field]="'phone'"></p-sortIcon>
            </th>
            <th [pSortableColumn]="'phoneMobile'" pResizableColumn>
              {{'phonemobile' | translate}}
              <p-sortIcon [field]="'phoneMobile'"></p-sortIcon>
            </th>
            <th [pSortableColumn]="'active'" pResizableColumn>
              {{'active' | translate}}
              <p-sortIcon [field]="'active'"></p-sortIcon>
            </th>
            <th [pSortableColumn]="'expiresOn'" pResizableColumn>
              {{'expires_on' | translate}}
              <p-sortIcon [field]="'expiresOn'"></p-sortIcon>
            </th>
            <th></th>
          </tr>
        </ng-template>

      <ng-template pTemplate="body" let-user>
        <tr>
          <td>{{user.firstName}}</td>
          <td>{{user.lastName}}</td>
          <td>{{user.role}}</td>
          <td>{{user.email}}</td>
          <td>{{user.phone}}</td>
          <td>{{user.phoneMobile}}</td>
          <td>
            <span *ngIf="user.active; else inactivePart">
              {{ 'yes' | translate }}
            </span>
            <ng-template #inactivePart>
              {{ 'no' | translate }}
            </ng-template>
          </td>
          <td>{{user.expiresOn | date:dateFormat}}</td>
          <td>
          <span (click)="selected(user)">
          <i *ngIf="myself(user) || checkPermission(user)" class="fas fa-pencil-alt" aria-hidden="true"></i>
          </span>
            <span (click)="deactivate(user)">
          <i *ngIf="checkPermission(user)" class="far fa-trash-alt" aria-hidden="true"></i>
          </span>
          </td>
        </tr>
      </ng-template>
      <ng-template pTemplate="emptymessage">
        <tr>
          <td [attr.colspan]="9">
            {{'noDataAvailable' | translate}}
          </td>
        </tr>
      </ng-template>
    </p-table>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class UserListComponent extends AbstractTranslateComponent implements OnInit {

  users$: Observable<User[]>;
  dateFormat: string;

  multiSortMeta: SortMeta[] = [];

  constructor( private userService: UserService,
               public translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               private roleGuard: RoleGuard ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();

    this.multiSortMeta.push( { field: 'active', order: -1 } );
    this.multiSortMeta.push( { field: 'lastName', order: 1 } );
    this.deactivate( <User> {} );
    this.selected( <User> {} );
    this.users$ = this.userService.users$;
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
          ( resp: HttpResponse<any> ) => {
            if (resp.status === 204) {
              this.msgService.success( 'UserSuccessfulDeactive' );
              this.userService.getUsers();
              this.userService.changeActiveUser( <User> {} );
            } else {
              this.msgService.handleResponse( resp );
            }
          },
          ( error: HttpErrorResponse ) => {
            this.msgService.handleResponse( error );
          } );
    }
  }

  myself( user: User ): boolean {
    return PermissionCheck.myself( user );
  }

  checkPermission( user: User ): boolean {
    return PermissionCheck.hasLessPermissions( this.roleGuard.userRole, user.role );
  }
}
