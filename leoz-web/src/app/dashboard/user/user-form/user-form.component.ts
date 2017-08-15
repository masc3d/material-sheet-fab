import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ValidatorFn } from '@angular/forms';
import { Response } from '@angular/http';
import {
  trigger,
  state,
  style,
  animate,
  transition
} from '@angular/animations';
import 'rxjs/add/operator/takeUntil';

import { SelectItem } from 'primeng/primeng';

import { User } from '../user.model';
import { UserService } from '../user.service';
import { RoleGuard } from '../../../core/auth/role.guard';
import { Msg } from '../../../shared/msg/msg.model';
import { MsgService } from '../../../shared/msg/msg.service';
import { TranslateService } from 'app/core/translate/translate.service';
import { AbstractTranslateComponent } from 'app/core/translate/abstract-translate.component';
import { PermissionCheck } from '../../../core/auth/permission-check';

@Component( {
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  animations: [
    trigger( 'myself', [
      state( 'inactive', style( {
        opacity: 0,
        // transform: 'translateY(-100%)'
      } ) ),
      state( 'active', style( {
        opacity: 1,
        // transform: 'translateY(0)'
      } ) ),
      transition( 'inactive <=> active',
        animate( '0.5s ease-in-out' ) )
    ] ),
  ]
} )
export class UserFormComponent extends AbstractTranslateComponent implements OnInit {

  dateFormatPrimeng: string;

  locale: any;
  roleOptions: SelectItem[];
  stateOptions: SelectItem[];
  activeUser: User;

  userForm: FormGroup;
  private loading = false;
  public errMsg: Msg;

  constructor( private fb: FormBuilder,
               private userService: UserService,
               private msgService: MsgService,
               private roleGuard: RoleGuard,
               protected translate: TranslateService ) {
    super( translate, () => {
      this.roleOptions = this.createRoleOptions();
      this.stateOptions = this.createStateOptions();
      const expiresOnControl = this.userForm.get( 'expiresOn' );
      setTimeout( () => expiresOnControl.setValue( expiresOnControl.value ), 20 );
    } );
  }

  ngOnInit() {
    super.ngOnInit();

    this.roleOptions = this.createRoleOptions();
    this.stateOptions = this.createStateOptions();

    this.msgService.msg
      .takeUntil( this.ngUnsubscribe ).subscribe( ( msg: Msg ) => this.errMsg = msg );
    this.msgService.clear();
    this.userForm = this.fb.group( {
      emailOrigin: [ null ],
      firstName: [ null, [ Validators.required, Validators.maxLength( 45 ) ] ],
      lastName: [ null, [ Validators.required, Validators.maxLength( 45 ) ] ],
      password: [ null, [ Validators.required, Validators.maxLength( 25 ) ] ],
      email: [ null, [ Validators.required, Validators.email ] ],
      phone: [ null, [ Validators.required ] ],
      alias: [ null, [ Validators.required, Validators.maxLength( 30 ) ] ],
      role: [ null, [ Validators.required ] ],
      active: [ null, [ Validators.required ] ],
      expiresOn: [ null ]
    } );

    this.userService.activeUser
      .takeUntil( this.ngUnsubscribe )
      .subscribe( ( activeUser: User ) => {
        this.activeUser = activeUser;
        const passwordControl = this.userForm.get( 'password' );
        const validators = <ValidatorFn[]> [];
        validators.push( Validators.maxLength( 25 ) );
        if (this.isEditMode( activeUser )) {
          this.msgService.clear();
        } else {
          validators.push( Validators.required );
        }
        passwordControl.clearValidators();
        passwordControl.setValidators( validators );
        this.userForm.patchValue( {
          emailOrigin: activeUser.email,
          firstName: activeUser.firstName,
          lastName: activeUser.lastName,
          password: '',
          email: activeUser.email,
          phone: activeUser.phone,
          alias: activeUser.alias,
          role: activeUser.role,
          active: activeUser.active,
          expiresOn: activeUser.expiresOn ? new Date( activeUser.expiresOn ) : activeUser.expiresOn
        } );
      } );
  }

  private createStateOptions(): SelectItem[] {
    const stateOptions = [];
    stateOptions.push( { label: this.translate.instant( 'yes' ), value: 1 } );
    stateOptions.push( { label: this.translate.instant( 'no' ), value: 0 } );
    return stateOptions;
  }

  private createRoleOptions(): SelectItem[] {
    const roleOptions = [];
    if (this.roleGuard.isPoweruser()) {
      roleOptions.push( { label: this.translate.instant( 'User' ), value: 'USER' } );
      roleOptions.push( { label: this.translate.instant( 'Driver' ), value: 'DRIVER' } );
      roleOptions.push( { label: this.translate.instant( 'Customer' ), value: 'CUSTOMER' } );
    }
    if (this.roleGuard.isUser()) {
      roleOptions.push( { label: this.translate.instant( 'Driver' ), value: 'DRIVER' } );
      roleOptions.push( { label: this.translate.instant( 'Customer' ), value: 'CUSTOMER' } );
    }
    return roleOptions;
  }

  private isEditMode( activeUser: User ) {
    return activeUser.email && activeUser.email.length > 0;
  }

  myself(): boolean {
    return PermissionCheck.myself( this.activeUser );
  }

  onSubmit() {
    this.loading = true;
    const removeFields = [ 'emailOrigin' ];
    if (!this.userForm.value.emailOrigin || this.userForm.value.emailOrigin.length === 0) {
      this.createUser( this.cloneAndRemove( this.userForm.value, removeFields ) );
    } else {
      if (!this.userForm.value.password || this.userForm.value.password.length === 0) {
        removeFields.push( 'password' );
      }
      this.updateUser( this.cloneAndRemove( this.userForm.value, removeFields ),
        this.userForm.value.emailOrigin );
    }
  }

  private cloneAndRemove( value: any, removeFields: string[] ): any {
    const copy = { ...value };
    for (const removeField of removeFields) {
      delete copy[ removeField ];
    }
    return copy;
  }

  protected createUser( userData: any ) {
    this.userService.insert( userData )
      .subscribe(
        ( resp: Response ) => {
          if (resp.status === 204) {
            this.loading = false;
            this.msgService.success( this.translate.instant( 'UserInsertSuccessful' ) )
            this.clearActiveUser();
            this.userService.getUsers();
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

  protected updateUser( userData: any, originEmail: string ) {
    this.userService.update( userData, originEmail )
      .subscribe(
        ( resp: Response ) => {
          if (resp.status === 204) {
            this.loading = false;
            this.msgService.success( this.translate.instant( 'UserUpdateSuccessful' ) )
            this.clearActiveUser();
            this.userService.getUsers();
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

  clearFields() {
    this.msgService.clear();
    this.clearActiveUser();
    return false;
  }

  clearActiveUser() {
    this.userForm.get( 'email' ).markAsUntouched();
    this.userService.changeActiveUser( <User>{} );
  }
}
