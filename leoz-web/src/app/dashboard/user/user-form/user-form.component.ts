import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { animate, state, style, transition, trigger } from '@angular/animations';
import { Observable } from 'rxjs/Observable';
import { takeUntil } from 'rxjs/operators';

import { Message } from 'primeng/components/common/api';
import { SelectItem } from 'primeng/api';

import { User } from '../user.model';
import { UserService } from '../user.service';
import { RoleGuard } from '../../../core/auth/role.guard';
import { MsgService } from '../../../shared/msg/msg.service';
import { TranslateService } from 'app/core/translate/translate.service';
import { AbstractTranslateComponent } from 'app/core/translate/abstract-translate.component';
import { PermissionCheck } from '../../../core/auth/permission-check';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { InetConnectionService } from '../../../core/inet-connection.service';

@Component( {
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
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
  msgs$: Observable<Message[]>;

  dateFormatPrimeng: string;

  stateOptions: SelectItem[];

  locale: any;
  assignableStations: SelectItem[];
  activeUser: User;
  userForm: FormGroup;
  private loading = false;

  roleOptions: SelectItem[];

  constructor( private fb: FormBuilder,
               private userService: UserService,
               protected msgService: MsgService,
               private roleGuard: RoleGuard,
               protected cd: ChangeDetectorRef,
               protected translate: TranslateService,
               private ics: InetConnectionService ) {
    super( translate, cd, msgService, () => {
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
    this.assignableStations = this.createAssignableStations();

    this.userForm = this.fb.group( {
      emailOrigin: [ null ],
      firstName: [ null, [ Validators.required, Validators.maxLength( 45 ) ] ],
      lastName: [ null, [ Validators.required, Validators.maxLength( 45 ) ] ],
      password: [ null, [ Validators.required, Validators.minLength( 6 ), Validators.maxLength( 25 ) ] ],
      email: [ null, [ Validators.required, Validators.email ] ],
      phone: [ null, [ Validators.required ] ],
      alias: [ null, [ Validators.required, Validators.maxLength( 30 ) ] ],
      phoneMobile: [ null, [ Validators.required ] ],
      role: [ null, [ Validators.required ] ],
      active: [ null, [ Validators.required ] ],
      expiresOn: [ null, [ Validators.required ] ],
      allowedStations: [ null, [ Validators.required ] ]
    } );

    this.userService.activeUser$
      .pipe(
        takeUntil( this.ngUnsubscribe )
      )
      .subscribe( ( activeUser: User ) => {
        this.activeUser = activeUser;
        const passwordControl = this.userForm.get( 'password' );
        const validators = <ValidatorFn[]> [];
        validators.push( Validators.minLength( 6 ) );
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
          phoneMobile: activeUser.phoneMobile,
          role: activeUser.role,
          active: activeUser.active,
          expiresOn: activeUser.expiresOn ? new Date( activeUser.expiresOn ) : activeUser.expiresOn,
          allowedStations: activeUser.allowedStations
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

  private createAssignableStations(): any {
    const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
    return currUser.user.allowedStations.map( stationNo => {
      return { label: stationNo, value: stationNo };
    } );
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
        ( resp: HttpResponse<any> ) => {
          if (resp.status === 204) {
            this.loading = false;
            this.msgService.success( this.translate.instant( 'UserInsertSuccessful' ) );
            this.clearActiveUser();
            this.userService.getUsers();
          } else {
            this.loading = false;
            this.msgService.handleResponse( resp );
          }
        },
        ( error: HttpErrorResponse ) => {
          this.ics.isOffline();
          this.loading = false;
          this.msgService.handleResponse( error );
        } );
  }

  protected updateUser( userData: any, originEmail: string ) {
    this.userService.update( userData, originEmail )
      .subscribe(
        ( resp: HttpResponse<any> ) => {
          if (resp.status === 204) {
            this.loading = false;
            this.msgService.success( this.translate.instant( 'UserUpdateSuccessful' ) );
            this.clearActiveUser();
            this.userService.getUsers();
          } else {
            this.loading = false;
            this.msgService.handleResponse( resp );
          }
        },
        ( error: HttpErrorResponse ) => {
          this.ics.isOffline();
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
