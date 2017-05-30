import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Response } from '@angular/http';
import { User } from '../user.model';
import { UserService } from '../user.service';
import { RoleGuard } from '../../../core/auth/role.guard';
import { Subscription } from 'rxjs/Subscription';

@Component( {
  selector: 'app-user-form',
  templateUrl: './user-form.component.html'
} )
export class UserFormComponent implements OnInit, OnDestroy {

  activeUser: User;
  userForm: FormGroup;
  private subscription: Subscription;
  private loading = false;
  private errMsg = '';

  constructor( private fb: FormBuilder,
               private userService: UserService,
               private roleGuard: RoleGuard ) {
  }

  ngOnInit() {
    this.userForm = this.fb.group( {
      firstName: [ null, [ Validators.required, Validators.maxLength( 45 ) ] ],
      lastName: [ null, [ Validators.required, Validators.maxLength( 45 ) ] ],
      password: [ null, [ Validators.required, Validators.minLength( 5 ), Validators.maxLength( 25 ) ] ],
      email: [ null, [ Validators.required, Validators.email ] ],
      phone: [ null, [ Validators.maxLength( 45 ) ] ],
      alias: [ null, [ Validators.required, Validators.maxLength( 30 ) ] ],
      role: [ null, [ Validators.required ] ],
      active: [ null, [ Validators.required ] ]
    } );

    this.userService.activeUser.subscribe( ( activeUser: User ) => {
      this.activeUser = activeUser;
      this.userForm.patchValue( {
        firstName: activeUser.firstName,
        lastName: activeUser.lastName,
        password: '',
        email: activeUser.email,
        phone: activeUser.phone,
        alias: activeUser.alias,
        role: activeUser.role,
        active: activeUser.active
      } );
    } );
  }

  ngOnDestroy() {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  onSubmit() {
    console.log( this.userForm.value );
    this.loading = true;
    this.subscription = this.userService.insert( this.userForm.value )
      .subscribe(
        ( resp: Response ) => {
          if (resp.status === 204) {
            this.loading = false;
            this.errMsg = 'User insert successful';
            this.clearFields();
          } else {
            this.handleError( resp );
          }
        },
        ( error: Response ) => {
          this.handleError( error );
        } );
  }

  handleError( resp: Response ) {
    this.loading = false;
    console.log( resp );
    this.errMsg = resp.json().title;
  }

  clearFields() {
    this.userForm.patchValue( {
      firstName: '',
      lastName: '',
      password: '',
      email: '',
      phone: '',
      alias: '',
      role: '',
      active: true
    } );
    this.userForm.get( 'email' ).markAsUntouched();
    return false;
  }
}
