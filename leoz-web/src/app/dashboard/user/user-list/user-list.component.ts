import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs/Observable';

import { UserService } from '../user.service';
import { User } from '../user.model';
import { MsgService } from '../../../shared/msg/msg.service';
import { TranslateService } from '../../../core/translate/translate.service';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { PermissionCheck } from '../../../core/auth/permission-check';
import { RoleGuard } from '../../../core/auth/role.guard';
import { SortMeta } from 'primeng/primeng';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

@Component( {
  selector: 'app-user-list',
  template: `
    <p-dataTable [value]="users$ | async | userfilter" resizableColumns="true" [responsive]="true"
                 sortMode="multiple" [multiSortMeta]="multiSortMeta">
      <p-column field="firstName" header="{{'firstname' | translate}}"></p-column>
      <p-column field="lastName" header="{{'surname' | translate}}" sortable="true"></p-column>
      <p-column field="role" header="{{'role' | translate}}" [sortable]="true">
        <ng-template let-user="rowData" pTemplate="body">
          {{ translate.role(user.role) }}
        </ng-template>
      </p-column>
      <p-column field="email" header="{{'email' | translate}}" [sortable]="true"></p-column>
      <p-column field="phone" header="{{'phoneoffice' | translate}}"></p-column>
      <p-column field="phoneMobile" header="{{'phonemobile' | translate}}"></p-column>
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
          <span (click)="selected(user)">
          <i *ngIf="myself(user) || checkPermission(user)" class="fas fa-pencil-alt fa-fw" aria-hidden="true"></i>
          </span>
          <span (click)="deactivate(user)">
          <i *ngIf="checkPermission(user)" class="far fa-trash-alt fa-fw" aria-hidden="true"></i>
          </span>
        </ng-template>
      </p-column>
    </p-dataTable>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class UserListComponent extends AbstractTranslateComponent implements OnInit {

  users$: Observable<User[]>;
  dateFormat: string;

  multiSortMeta: SortMeta[] = [];

  constructor( private userService: UserService,
               private msgService: MsgService,
               public translate: TranslateService,
               protected cd: ChangeDetectorRef,
               private roleGuard: RoleGuard ) {
    super( translate, cd );
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
