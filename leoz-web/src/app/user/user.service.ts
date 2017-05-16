import { Inject, Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/observable/of';

import { User, Position } from './user.model';
import { environment } from '../../environments/environment';

@Injectable()
export class UserService {

  // private userListUrl = `${environment.apiUrl}/users`;
  private userListUrl = `${environment.apiUrl}/userlist.json`;

  private activeUserSubject = new BehaviorSubject<User>(new User());
  public activeUser = this.activeUserSubject.asObservable().distinctUntilChanged();

  constructor(private http: Http) {
  }

  getUsers() {
    return this.http.get(this.userListUrl)
      .map((response: Response) => {
        const userArr: User[] = [];
        response.json().forEach(function (json) {
          const user = Object.assign(new User(), json);
          user.position = Object.assign(new Position(), user.position);
          userArr.push(user);
        });
        return userArr;
      })
      .catch((error: Response) => this.errorHandler(error));
  }

  errorHandler(error: Response) {
    console.log(error);
    return Observable.of([]);
  }

  changeActiveUser(selectedUser) {
    this.activeUserSubject.next(selectedUser);
  }
}
