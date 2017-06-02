import { Component, OnDestroy, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { User } from '../user.model';
import { Observable } from 'rxjs/Observable';
import { Subscription } from 'rxjs/Subscription';
import { Response } from '@angular/http';
import { MsgService } from '../../../shared/msg/msg.service';

@Component( {
  selector: 'app-user-list',
  templateUrl: './user-list.component.html'
} )
export class UserListComponent implements OnInit, OnDestroy {

  users: Observable<User[]>;
  private subscriptionCRUD: Subscription;

  constructor( private userService: UserService,
               private msgService: MsgService ) {
  }

  ngOnInit() {
    console.log( '-------------- UserListComponent ngOnInit' );
    this.deactivate( <User> {} );
    this.selected( <User> {} );
    this.users = this.userService.users;
    this.userService.getUsers();
  }

  ngOnDestroy() {
    console.log( '----------- ngONDestroy UserListComponent' );
    if (this.users) {
      this.users = null;
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
