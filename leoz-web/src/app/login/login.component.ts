import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Response } from '@angular/http';
import { AuthenticationService } from '../core/auth/authentication.service';
import { Subject } from 'rxjs/Subject';
import 'rxjs/add/operator/takeUntil';

import { Msg } from '../shared/msg/msg.model';
import { MsgService } from '../shared/msg/msg.service';

@Component( {
  selector: 'app-login',
  templateUrl: './login.component.html'
} )
export class LoginComponent implements OnInit, OnDestroy {

  private ngUnsubscribe: Subject<void> = new Subject<void>();

  loading = false;

  errMsg: Msg;
  loginForm: FormGroup;

  constructor( private fb: FormBuilder,
               private router: Router,
               private authenticationService: AuthenticationService,
               private msgService: MsgService ) {
  }

  ngOnInit() {
    this.msgService.clear();
    this.msgService.msg
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( msg: Msg ) => this.errMsg = msg );

    // reset login status
    this.authenticationService.logout();

    this.loginForm = this.fb.group( {
      username: [ null, [ Validators.required ] ],
      password: [ null, [ Validators.required ] ],
    } );
  }

  ngOnDestroy() {
    this.ngUnsubscribe.next();
    this.ngUnsubscribe.complete();
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
