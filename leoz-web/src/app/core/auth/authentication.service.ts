import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Http, Response } from '@angular/http';
import { RequestOptions, Headers } from '@angular/http';

@Injectable()
export class AuthenticationService {

  private isLoggedInSubject = new BehaviorSubject<boolean>(false);

  constructor(private router: Router, private http: Http){
    this.isLoggedInSubject.next(localStorage.getItem('currentUser') !== null && localStorage.getItem('currentUser').length > 0);
  }

  logout() {
    localStorage.removeItem('currentUser');
    this.isLoggedInSubject.next(false);
    this.router.navigate(['login']);
  }

  login(username: string, password: string): Observable<any> {

    const headers = new Headers();
    headers.append('Content-Type', 'application/json');

    const options = new RequestOptions({ headers: headers });

    return this.http.patch('http://localhost:13000/rs/api/internal/v1/authorize/web',
        JSON.stringify({
            'email': `${username}`,
            'password': `${password}`
        } ), options)
      .map((response: Response) => {
          if (response.status === 200) {
            // example data {"key":"DArZpI5njClOAHjueuspWmw1dQdGcj","debitorNo":"5"}
            localStorage.setItem('currentUser', JSON.stringify(response.json()));
            this.isLoggedInSubject.next(true);
          } else {
            this.isLoggedInSubject.next(false);
          }
          return response;
      });
  }
}
