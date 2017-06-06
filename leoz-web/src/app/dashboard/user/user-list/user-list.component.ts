import { Component, OnDestroy, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { User } from '../user.model';
import { Subscription } from 'rxjs/Subscription';
import { Response } from '@angular/http';
import { MsgService } from '../../../shared/msg/msg.service';
import { PermissionCheck } from "app/core/auth/permission-check";
import { RoleGuard } from '../../../core/auth/role.guard';

@Component( {
  selector: 'app-user-list',
  template: `
    <p-dataTable [value]="users">
      <p-column field="firstName" header="{{'firstname' | translate}}" [sortable]="true"></p-column>
      <p-column field="lastName" header="{{'surname' | translate}}"></p-column>
      <p-column field="role" header="{{'role' | translate}}"></p-column>
      <p-column header="">
        <ng-template let-user="rowData" pTemplate="body">
          <i class="fa fa-pencil fa-fw" aria-hidden="true" (click)="selected(user)"></i>
          <i class="fa fa-trash-o fa-fw" aria-hidden="true" (click)="deactivate(user)"></i>
        </ng-template>
      </p-column>
    </p-dataTable>
  `
} )
export class UserListComponent implements OnInit, OnDestroy {

  users: User[];

  private subscription: Subscription;
  private subscriptionCRUD: Subscription;

  constructor( private userService: UserService,
               private msgService: MsgService,
               private roleGuard: RoleGuard ) {
  }

  ngOnInit() {
    console.log( '-------------- UserListComponent ngOnInit' );
    this.deactivate( <User> {} );
    this.selected( <User> {} );
    this.subscription = this.userService.users.subscribe((users: User[]) => {
      this.users = users.filter( item => PermissionCheck.isAllowedRole( this.roleGuard.userRole, item.role ) );
    });
    this.userService.getUsers();
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
    if (this.subscriptionCRUD) {
      this.subscriptionCRUD.unsubscribe();
    }
  }

  selected( selectedUser: User ) {
    this.userService.changeActiveUser( selectedUser );
  }

  deactivate( selectedUser: User ) {
    this.deactivateUser( selectedUser.email );
  }

  protected deactivateUser( originEmail: string ) {
    if(originEmail && originEmail.length > 0) {
      this.subscriptionCRUD = this.userService.update( { active: false }, originEmail )
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
}
