import { Injectable } from '@angular/core';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

import { User } from './user.model';
import { environment } from '../../../environments/environment';
import { MsgService } from '../../shared/msg/msg.service';
import { HttpClient, HttpErrorResponse, HttpParams, HttpResponse } from '@angular/common/http';

@Injectable()
export class UserService {

  private userListUrl = `${environment.apiUrl}/internal/v1/user`;

  private usersSubject = new BehaviorSubject<User[]>( [] );
  public users$ = this.usersSubject.asObservable().distinctUntilChanged();

  private activeUserSubject = new BehaviorSubject<User>( <User> {} );
  public activeUser$ = this.activeUserSubject.asObservable().distinctUntilChanged();

  constructor( private http: HttpClient,
               private msgService: MsgService ) {
  }

  insert( userData: any ): Observable<HttpResponse<any>> {
    return this.http.post( this.userListUrl, userData, {
      observe: 'response'
    } );
  }

  update( userData: any, originEmail: string ): Observable<HttpResponse<any>> {
    return this.http.put( this.userListUrl, userData, {
      params: new HttpParams().set( 'email', originEmail ),
      observe: 'response'
    } );
  }

  getUsers(): void {
    this.http.get<User[]>( this.userListUrl )
      .subscribe( ( users ) => this.usersSubject.next( users ),
        ( error: HttpErrorResponse ) => {
          this.msgService.handleResponse( error );
          this.usersSubject.next( <User[]> [] );
        } );
  }

  changeActiveUser( selectedUser ): void {
    this.activeUserSubject.next( selectedUser );
  }
}
