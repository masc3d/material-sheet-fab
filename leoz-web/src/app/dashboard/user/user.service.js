var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
  };
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
  };
import { Injectable } from '@angular/core';
import { Http } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/observable/of';
import { User, Position } from './user.model';
import { environment } from '../../environments/environment';
import { ErrormsgService } from '../error/errormsg.service';
var UserService = (function () {
  function UserService(http, errormsgService) {
    this.http = http;
    this.errormsgService = errormsgService;
    // private userListUrl = `${environment.apiUrl}/users`;
    this.userListUrl = environment.apiUrl + "/userlist.json";
    this.activeUserSubject = new BehaviorSubject(new User());
    this.activeUser = this.activeUserSubject.asObservable().distinctUntilChanged();
  }
  UserService.prototype.getUsers = function () {
    var _this = this;
    return this.http.get(this.userListUrl)
      .map(function (response) {
        var userArr = [];
        response.json().forEach(function (json) {
          var user = Object.assign(new User(), json);
          user.position = Object.assign(new Position(), user.position);
          userArr.push(user);
        });
        return userArr;
      })
      .catch(function (error) { return _this.errorHandler(error); });
  };
  UserService.prototype.errorHandler = function (error) {
    console.log(error);
    this.errormsgService.changeError(error);
    return Observable.of([]);
  };
  UserService.prototype.changeActiveUser = function (selectedUser) {
    this.activeUserSubject.next(selectedUser);
  };
  return UserService;
}());
UserService = __decorate([
  Injectable(),
  __metadata("design:paramtypes", [Http,
    ErrormsgService])
], UserService);
export { UserService };
//# sourceMappingURL=user.service.js.map
