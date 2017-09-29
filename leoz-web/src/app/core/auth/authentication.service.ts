import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { environment } from '../../../environments/environment';
import { RoleGuard } from './role.guard';
import 'rxjs/add/operator/map';
import { MsgService } from '../../shared/msg/msg.service';
import { User } from '../../dashboard/user/user.model';

@Injectable()
export class AuthenticationService {

  private authUrl = `${environment.apiUrl}/internal/v1/authorize/web`;

  constructor( private router: Router,
               private http: HttpClient,
               private roleGuard: RoleGuard,
               private msgService: MsgService ) {
  }

  logout() {
    this.roleGuard.userRole = null;
    localStorage.removeItem( 'currentUser' );
    this.router.navigate( [ 'login' ] );
  }

  login( username: string, password: string ): Observable<HttpResponse<any>> {

    const body = {
      email: `${username}`,
      password: `${password}`
    } ;

    return this.http.patch( this.authUrl, body, {
      observe: 'response'
    } ).map( ( response: HttpResponse<any> ) => {
      if (response.status === 200) {
        const userJson = response.body;
        const user: User = userJson['user'];
        this.roleGuard.userRole = user.role;
        if (user.active) {
          localStorage.setItem( 'currentUser', JSON.stringify( userJson ) );
        } else {
          this.msgService.error( 'user account deactivated' );
        }
      }
      return response;
    } );
  }
}
