import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthenticationService } from '../core/auth/authentication.service';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/takeUntil';

import { Message } from 'primeng/primeng';

import { MsgService } from '../shared/msg/msg.service';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { environment } from '../../environments/environment';
import { AbstractTranslateComponent } from '../core/translate/abstract-translate.component';
import { TranslateService } from '../core/translate/translate.service';

@Component( {
  selector: 'app-login',
  templateUrl: './login.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class LoginComponent extends AbstractTranslateComponent implements OnInit {

  loading = false;
  private autoLogin = `${environment.autologin}`;
  private devUser = `${environment.devUser}`;
  private devPass = `${environment.devPass}`;

  msgs$: Observable<Message[]>;

  loginForm: FormGroup;

  constructor( private fb: FormBuilder,
               private router: Router,
               private authenticationService: AuthenticationService,
               protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();

    // reset login status
    this.authenticationService.logout();

    this.loginForm = this.fb.group( {
      username: [ null, [ Validators.required ] ],
      password: [ null, [ Validators.required ] ],
    } );

    if (this.autoLogin === 'yes') {
      this.loginForm.patchValue( {
        username: this.devUser,
        password: this.devPass
      } );
    } else {
      this.loginForm.patchValue( {
        username: null,
        password: null
      } );
    }
  }

  login() {
    this.loading = true;
    this.authenticationService.login( this.loginForm.value.username, this.loginForm.value.password )
      .subscribe(
        ( resp: HttpResponse<any> ) => {
          if (resp.status === 200) {
            this.loading = false;
            this.router.navigate( [ 'dashboard/home' ] );
          } else {
            this.loading = false;
            console.log( 'handleResponse( resp )', resp );
            this.msgService.handleResponse( resp );
          }
        },
        ( error: HttpErrorResponse ) => {
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
