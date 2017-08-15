import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Response } from '@angular/http';
import { AuthenticationService } from '../core/auth/authentication.service';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/takeUntil';

import { Message } from 'primeng/primeng';

import { MsgService } from '../shared/msg/msg.service';

@Component( {
  selector: 'app-login',
  templateUrl: './login.component.html'
} )
export class LoginComponent implements OnInit {

  loading = false;

  errMsgs: Observable<Message[]>;
  loginForm: FormGroup;

  constructor( private fb: FormBuilder,
               private router: Router,
               private authenticationService: AuthenticationService,
               private msgService: MsgService ) {
  }

  ngOnInit() {
    this.msgService.clear();
    this.errMsgs = this.msgService.msgs;

    // reset login status
    this.authenticationService.logout();

    this.loginForm = this.fb.group( {
      username: [ null, [ Validators.required ] ],
      password: [ null, [ Validators.required ] ],
    } );
  }

  login() {
    this.loading = true;
    this.authenticationService.login( this.loginForm.value.username, this.loginForm.value.password )
      .subscribe(
        ( resp: Response ) => {
          if (resp.status === 200) {
            this.loading = false;
            this.router.navigate( [ 'dashboard/home' ] );
          } else {
            this.loading = false;
            this.msgService.handleResponse( resp );
          }
        },
        ( error: Response ) => {
          this.loading = false;
          this.msgService.handleResponse( error );
        } );
  }

  logout() {
    this.authenticationService.logout();
  }

  resetErrMsg() {
    this.msgService.clear();
  }
}
