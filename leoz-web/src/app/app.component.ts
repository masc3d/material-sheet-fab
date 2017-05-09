import { Component, OnInit } from '@angular/core';
import { AuthenticationService } from './auth/authentication.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {
  isLoggedIn: boolean;

  constructor(private authService: AuthenticationService){}

  ngOnInit() {
    this.authService.isLoggedIn.subscribe((isLoggedIn: boolean) => this.isLoggedIn = isLoggedIn);
  }
}
