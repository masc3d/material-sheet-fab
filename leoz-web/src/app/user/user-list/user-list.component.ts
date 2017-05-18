import { Component, OnInit } from '@angular/core';
import {UserService} from '../user.service';
import {User} from '../user.model';
import { Observable } from 'rxjs/Observable';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html'
})
export class UserListComponent implements OnInit {

  users: Observable<User[]>;

  constructor(private userService: UserService) { }

  ngOnInit() {
    console.log('-------------- UserListComponent ngOnInit');
    this.selected(new User());
    this.users = this.userService.getUsers();
  }

  selected(selectedUser: User) {
    this.userService.changeActiveUser(selectedUser);
  }

}
