import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';

import { Message } from 'primeng/api';
import { AbstractTranslateComponent } from '../../../core/translate/abstract-translate.component';
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';
import { ChangepasswordService } from './changepassword.service';


@Component( {
  selector: 'app-changepassword',
  templateUrl: './changepassword.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush
} )
export class ChangepasswordComponent extends AbstractTranslateComponent implements OnInit {

  loading = false;
  pwdMismatch = false;

  msgs$: Observable<Message[]>;
  sticky$: Observable<boolean>;

  pwdForm: FormGroup;

  constructor( private fb: FormBuilder,
               protected translate: TranslateService,
               protected cd: ChangeDetectorRef,
               protected msgService: MsgService,
               protected changePwdService: ChangepasswordService ) {
    super( translate, cd, msgService );
  }

  ngOnInit() {
    super.ngOnInit();

    this.msgs$ = this.msgService.msgs$;
    this.sticky$ = this.msgService.sticky$;
    this.pwdForm = this.fb.group( {
      oldPwd: [ null, [ Validators.required, Validators.minLength( 6 ), Validators.maxLength( 25 ) ] ],
      newPwd: [ null, [ Validators.required, Validators.minLength( 6 ), Validators.maxLength( 25 ) ] ],
      newPwdRepeat: [ null, [ Validators.required, Validators.minLength( 6 ), Validators.maxLength( 25 ) ] ]
    } );
  }

  changePwd() {
    if (this.pwdForm.get( 'newPwd' ).value !== this.pwdForm.get( 'newPwdRepeat' ).value) {
      this.pwdMismatch = true;
    } else {
      this.pwdMismatch = false;
      this.loading = true;
      const userId = JSON.parse( localStorage.getItem( 'currentUser' ) ).user.id;
      this.changePwdService.changePwd( userId,
        this.pwdForm.get( 'oldPwd' ).value,
        this.pwdForm.get( 'newPwd' ).value )
        .subscribe( ( _ ) => {
            this.msgService.success( 'pwdChangeSuccess', false );
            this.clearInputFields();
          },
          ( _ ) => {
            this.msgService.error( 'pwdChangeFailure', false );
          } )
    }
    this.cd.detectChanges();
  }

  private clearInputFields() {
    this.pwdForm.patchValue( {
      oldPwd: '',
      newPwd: '',
      newPwdRepeat: ''
    } );
  }
}
