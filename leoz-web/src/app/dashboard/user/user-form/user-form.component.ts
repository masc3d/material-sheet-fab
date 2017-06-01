import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Response } from '@angular/http';
import { User } from '../user.model';
import { UserService } from '../user.service';
import { RoleGuard } from '../../../core/auth/role.guard';
import { Subscription } from 'rxjs/Subscription';
import { Msg } from '../../../shared/msg/msg.model';
import { MsgService } from '../../../shared/msg/msg.service';

@Component( {
  selector: 'app-user-form',
  templateUrl: './user-form.component.html'
} )
export class UserFormComponent implements OnInit, OnDestroy {

  activeUser: User;
  userForm: FormGroup;
  private subscriptionCRUD: Subscription;
  private subscriptionActiveUser: Subscription;
  private loading = false;
  private errMsg: Msg;

  constructor( private fb: FormBuilder,
               private userService: UserService,
               private msgService: MsgService,
               private roleGuard: RoleGuard ) {
  }

  ngOnInit() {
    this.errMsg = this.msgService.clear();
    this.userForm = this.fb.group( {
      emailOrigin: [ null ],
      firstName: [ null, [ Validators.required, Validators.maxLength( 45 ) ] ],
      lastName: [ null, [ Validators.required, Validators.maxLength( 45 ) ] ],
      password: [ null ],
      email: [ null, [ Validators.required, Validators.email ] ],
      phone: [ null, [ Validators.required ] ],
      alias: [ null, [ Validators.required, Validators.maxLength( 30 ) ] ],
      role: [ null, [ Validators.required ] ],
      active: [ null, [ Validators.required ] ]
    } );

    this.subscriptionActiveUser = this.userService.activeUser.subscribe( ( activeUser: User ) => {
      this.activeUser = activeUser;
      const passwordControl = this.userForm.get( 'password' );
      passwordControl.clearValidators();
      passwordControl.setValidators( [ Validators.maxLength( 25 ) ] );
      if (this.isEditMode( activeUser )) {
        this.errMsg = this.msgService.clear();
      } else {
        passwordControl.setValidators( [ Validators.required ] );
      }
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
      } );
    } );
  }

  private isEditMode( activeUser: User ) {
    return activeUser.email && activeUser.email.length > 0;
  }

  ngOnDestroy() {
    if (this.subscriptionCRUD) {
      this.subscriptionCRUD.unsubscribe();
    }
    if (this.subscriptionActiveUser) {
      this.subscriptionActiveUser.unsubscribe();
    }
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
    this.subscriptionCRUD = this.userService.insert( userData )
      .subscribe(
        ( resp: Response ) => {
          if (resp.status === 204) {
            this.loading = false;
            this.errMsg = this.msgService.success( 'User insert successful' );
            this.clearActiveUser();
            this.userService.getUsers();
          } else {
            this.loading = false;
            this.errMsg = this.msgService.handleResponse( resp );
          }
        },
        ( error: Response ) => {
          this.loading = false;
          this.errMsg = this.msgService.handleResponse( error );
        } );
  }

  protected updateUser( userData: any, originEmail: string ) {
    this.subscriptionCRUD = this.userService.update( userData, originEmail )
      .subscribe(
        ( resp: Response ) => {
          if (resp.status === 204) {
            this.loading = false;
            this.errMsg = this.msgService.success( 'User update successful' );
            this.clearActiveUser();
            this.userService.getUsers();
          } else {
            this.loading = false;
            this.errMsg = this.msgService.handleResponse( resp );
          }
        },
        ( error: Response ) => {
          this.loading = false;
          this.errMsg = this.msgService.handleResponse( error );
        } );
  }

  clearFields() {
    this.errMsg = this.msgService.clear();
    this.clearActiveUser();
    return false;
  }

  clearActiveUser() {
    this.userForm.get( 'email' ).markAsUntouched();
    this.userService.changeActiveUser( <User>{} );
  }
}
