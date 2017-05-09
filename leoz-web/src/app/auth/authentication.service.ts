import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs/Observable';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Response, ResponseOptions } from '@angular/http';

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

  constructor(private router: Router){}

  logout() {
    localStorage.removeItem("currentUser");
    this.isLoggedInSubject.next(false);
    this.router.navigate(['login']);
  }

  login(username: string, password: string): Observable<any> {
    // TODO just checking against constant users array
    /* should be done something like:

      return this.http.post('/api/authenticate', JSON.stringify({ username: username, password: password }))
      .map((response: Response) => {
        // login successful if there's a jwt token in the response
        let user = response.json();
        if (user && user.token) {
          // store user details and jwt token in local storage to keep user logged in between page refreshes
          localStorage.setItem('currentUser', JSON.stringify(user));
        }
      });*/
    const authenticatedUser:User = users.find(u => u.username === username);
    if (authenticatedUser && authenticatedUser.password === password){
      localStorage.setItem("currentUser", JSON.stringify(authenticatedUser));
      this.isLoggedInSubject.next(true);
      return Observable.of(new Response(new ResponseOptions({
        status: 200,
        body: {
          username: authenticatedUser.username,
          token: 'fake-jwt-token'
        }
      })));
    }
    this.isLoggedInSubject.next(false);
    return Observable.of(new Response(new ResponseOptions({
      status: 301,
      body: 'Username or password is incorrect'
    })));
  }

}
