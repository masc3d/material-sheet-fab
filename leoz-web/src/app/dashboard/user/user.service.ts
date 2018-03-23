import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { distinctUntilChanged } from 'rxjs/operators';

import { User } from './user.model';
import { environment } from '../../../environments/environment';
import { MsgService } from '../../shared/msg/msg.service';
import { InetConnectionService } from 'app/core/inet-connection.service';

@Injectable()
export class UserService {

  private userListUrl = `${environment.apiUrl}/internal/v1/user`;

  private usersSubject = new BehaviorSubject<User[]>( [] );
  public users$ = this.usersSubject.asObservable().pipe(distinctUntilChanged());

  private activeUserSubject = new BehaviorSubject<User>( <User> {} );
  public activeUser$ = this.activeUserSubject.asObservable().pipe(distinctUntilChanged());

  constructor( private http: HttpClient,
               private msgService: MsgService,
               private ics: InetConnectionService ) {
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
          this.ics.isOffline();
          this.msgService.handleResponse( error );
          this.usersSubject.next( <User[]> [] );
        } );
  }

  changeActiveUser( selectedUser ): void {
    this.activeUserSubject.next( selectedUser );
  }
}
