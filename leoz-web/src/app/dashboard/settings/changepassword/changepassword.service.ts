import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ChangepasswordService {

  constructor( private http: HttpClient ) {
  }

  changePwd( userId: string, oldPwd: string, newPwd: string ) {
    const changePwdURL = `${environment.apiUrl}/internal/v1/user/${userId}/changePassword`;
    const params = new HttpParams()
      .set( 'old-password', oldPwd )
      .set( 'new-password', newPwd );

    return this.http.patch( changePwdURL, null, {
      observe: 'response',
      params: params
    } );
  }
}
