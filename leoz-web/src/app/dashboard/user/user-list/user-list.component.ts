import { Component, OnDestroy, OnInit } from '@angular/core';
import { UserService } from '../user.service';
import { User } from '../user.model';
import { Observable } from 'rxjs/Observable';

@Component( {
  selector: 'app-user-list',
  templateUrl: './user-list.component.html'
} )
export class UserListComponent implements OnInit, OnDestroy {

  users: Observable<User[]>;

  constructor( private userService: UserService ) {
  }

  ngOnInit() {
    console.log( '-------------- UserListComponent ngOnInit' );
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

}
