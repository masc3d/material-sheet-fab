import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Http, Response, ResponseOptions} from '@angular/http';
import { RequestMethod, RequestOptions, Headers } from '@angular/http';

export class User {
  constructor(public username: string,
    public password: string, public depotId: number) { }
}

const users = [
  new User('user1', 'user1', 1),
  new User('user2', 'user2', 2)
];

@Injectable()
export class AuthenticationService {

  private isLoggedInSubject = new BehaviorSubject<boolean>(false);
  public isLoggedIn = this.isLoggedInSubject.asObservable().distinctUntilChanged();

  constructor(private router: Router, private http: Http){
    if (localStorage.getItem('currentUser') !== null && localStorage.getItem('currentUser').length > 0) {
      this.isLoggedInSubject.next(true);
    }
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
