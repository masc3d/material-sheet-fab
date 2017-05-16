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
    const positionFormGroup = this.fb.group({
      lat: [ null, Validators.pattern(/^-?\d*(\.\d{0,4})?$/) ],
      lng: [ null, Validators.pattern(/^-?\d*(\.\d{0,4})?$/) ],
    });
    this.userForm = this.fb.group({
      firstname: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(45) ] ],
      surname: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(45) ] ],
      password: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(255) ] ],
      email: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(100) ] ],
      phone: [ null, [ Validators.minLength(0), Validators.maxLength(45) ] ],
      alias: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(30) ] ],
      salt: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(45) ] ],
      role: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(20) ] ],
      active: [ null, [ Validators.required, Validators.pattern('^[0-9]{1}$') ] ],
      usernumber: [ null, [ Validators.pattern('^[0-9]{4}$') ] ],
      debitor_id: [ null, [ Validators.required, Validators.pattern('^[0-9]{4}$') ] ],
      tournumber: [ null, [ Validators.pattern('^[0-9]{4}$') ] ],
      position: positionFormGroup
    });

    this.userService.activeUser.subscribe((activeUser: User) => {
      this.activeUser = activeUser;
      this.userForm.patchValue({
        firstname: activeUser.firstname,
        surname: activeUser.surname,
        usernumber: activeUser.usernumber,
        tournumber: activeUser.tournumber,
        position: {
          lat: activeUser.position ? activeUser.position.lat : '',
          lng: activeUser.position ? activeUser.position.lng : ''
        }
      });
    });
  }

  onSubmit() {
    console.log(this.userForm.value);
  }
}
