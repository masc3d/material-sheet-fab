import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { User } from '../user.model';
import { UserService } from '../user.service';

@Component({
  selector: 'app-user-form',
  templateUrl: './user-form.component.html',
  styles: [ `
    input.ng-invalid {
      border-left: 5px solid red;
    }

    input.ng-valid {
      border-left: 5px solid green;
    }
  ` ]
})
export class UserFormComponent implements OnInit {

  activeUser: User;
  userForm: FormGroup;

  constructor(private fb: FormBuilder,
              private userService: UserService) {
  }

  ngOnInit() {
    this.userForm = this.fb.group({
      firstName: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(45) ] ],
      lastName: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(45) ] ],
      password: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(25) ] ],
      email: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(100) ] ],
      phone: [ null, [ Validators.minLength(0), Validators.maxLength(45) ] ],
      alias: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(30) ] ],
      role: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(20) ] ],
      active: [ null, [ Validators.required ] ]
      /*active: [ null, [ Validators.required, Validators.pattern('^[0-9]{1}$') ] ]*/
    });

    this.userService.activeUser.subscribe((activeUser: User) => {
      this.activeUser = activeUser;
      this.userForm.patchValue({
        firstName: activeUser.firstName,
        lastName: activeUser.lastName,
        /*password: activeUser.password,*/
        password: '',
        email: activeUser.email,
        phone: activeUser.phone,
        alias: activeUser.alias,
        role: activeUser.role,
        active: activeUser.active
      });
    });
  }

  onSubmit() {
    console.log(this.userForm.value);
  }
}
