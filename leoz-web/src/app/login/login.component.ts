import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Response } from '@angular/http';
import { AuthenticationService } from '../core/auth/authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styles: [ `
    input.ng-invalid, input.invalid {
      border-left: 5px solid red !important;
    }

    input.ng-valid {
      border-left: 5px solid green;
    }
  ` ]
})
export class LoginComponent implements OnInit {

  loading = false;
  errMsg = '';

  loginForm: FormGroup;

  constructor(private fb: FormBuilder,
              private router: Router,
              private authenticationService: AuthenticationService) { }

  ngOnInit() {
    // reset login status
    this.authenticationService.logout();

    this.loginForm = this.fb.group({
      username: [ null, [ Validators.required ] ],
      password: [ null, [ Validators.required ] ],
    });
  }

  login() {
    this.loading = true;
    this.authenticationService.login(this.loginForm.value.username, this.loginForm.value.password)
      .subscribe(
        (resp: Response) => {
          if (resp.status === 200) {
            this.loading = false;
            this.router.navigate([ 'dashboard/home' ]);
          } else {
            this.handleError(resp);
          }
        },
        (error: Response) => {
          this.handleError(error);
        });
  }

  handleError(resp: Response) {
    this.loading = false;
    console.log(resp);
    this.errMsg = resp.json().title;
  }

  logout() {
    this.authenticationService.logout();
  }

  resetErrMsg() {
    this.errMsg = '';
  }
}
