import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { takeUntil } from 'rxjs/operators';

import { SortEvent } from 'primeng/api';

import { UserService } from '../user.service';
import { User } from '../../../core/models/user.model';
import { MsgService } from '../../../shared/msg/msg.service';
import { TranslateService } from '../../../core/translate/translate.service';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { PermissionCheck } from '../../../core/auth/permission-check';
import { RoleGuard } from '../../../core/auth/role.guard';
import { compareCustom } from '../../../core/compare-fn/custom-compare';
import { Station } from '../../../core/auth/station.model';

@Component( {
  selector: 'app-user-list',
  template: `
    <p-table [value]="users" [resizableColumns]="true" [paginator]="true" [rows]="10" [totalRecords]="users.length"
             [responsive]="true" (sortFunction)="customSort($event)" [customSort]="true">
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

  users: User[];
  dateFormat: string;

  latestSortField: string = null;
  latestSortOrder = 0;

  constructor( private userService: UserService,
               public translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               private roleGuard: RoleGuard ,
               private permissionCheck: PermissionCheck ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();

    this.deactivate( <User> {} );
    this.selected( <User> {} );
    this.users = [];
    this.userService.users$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( users => {
          users = this.filterUsers( users );
          this.users.length = 0;
          if (this.latestSortField !== null) {
            this.users.push( ...users.sort( ( data1, data2 ) => {
              return compareCustom( this.latestSortOrder, data1[ this.latestSortField ], data2[ this.latestSortField ] );
            } ) );
          } else {
            this.users.push( ...this.initialSort( users ) );
          }
          this.cd.markForCheck();
        }
      );
    this.userService.getUsers();
  }

  customSort( event: SortEvent ) {
    if (event.field) {
      this.latestSortField = event.field;
      this.latestSortOrder = event.order;
      event.data.sort( ( data1, data2 ) => {
        return compareCustom( event.order, data1[ event.field ], data2[ event.field ] );
      } );
    }
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
    return this.permissionCheck.myself( user );
  }

  checkPermission( user: User ): boolean {
    return this.permissionCheck.hasLessPermissions( this.roleGuard.userRole, user.role );
  }

  private initialSort( users: User[] ): User[] {
    // sorting active = true and lastName asc
    const activePart = users
      .filter( user => user.active )
      .sort( ( u1: User, u2: User ) => u1.lastName.localeCompare( u2.lastName ) );
    const inactivePart = users
      .filter( user => !user.active )
      .sort( ( u1: User, u2: User ) => u1.lastName.localeCompare( u2.lastName ) );
    return [ ...activePart, ...inactivePart ];
  }

  private filterUsers( users: User[] ): User[] {
    if (!users) {
      return [];
    }
    const activeStation: Station = JSON.parse( localStorage.getItem( 'activeStation' ) );
    const activeStationNo = activeStation && activeStation.stationNo ? activeStation.stationNo : -1;
    const predicateMethod = this.roleGuard.isPoweruser() ? this.poweruserPredicate : this.userPredicate;
    return users
      .filter( ( user: User ) => predicateMethod( user, activeStationNo ) )
      .filter( ( user: User ) => this.permissionCheck.isAllowedRole( this.roleGuard.userRole, user.role ) );
  }

  private poweruserPredicate( user: User, activeStationNo ) {
    return !user.allowedStations || user.allowedStations.length === 0 || user.allowedStations.indexOf( activeStationNo ) >= 0;
  }

  private userPredicate( user: User, activeStationNo ) {
    return user.allowedStations && user.allowedStations.indexOf( activeStationNo ) >= 0;
  }
}
