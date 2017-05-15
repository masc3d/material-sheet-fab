import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Driver } from '../driver.model';
import { DriverService } from '../driver.service';

@Component({
  selector: 'app-driver-form',
  templateUrl: './driver-form.component.html',
  styles: [ `
    input.ng-invalid {
      border-left: 5px solid red;
    }

    input.ng-valid {
      border-left: 5px solid green;
    }
  ` ]
})
export class DriverFormComponent implements OnInit {

  activeDriver: Driver;
  driverForm: FormGroup;

  constructor(private fb: FormBuilder,
              private driverService: DriverService) {
  }

  ngOnInit() {
    const positionFormGroup = this.fb.group({
      lat: [ null, Validators.pattern(/^-?\d*(\.\d{0,4})?$/) ],
      lng: [ null, Validators.pattern(/^-?\d*(\.\d{0,4})?$/) ],
    });
    this.driverForm = this.fb.group({
      firstname: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(45) ] ],
      surname: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(45) ] ],
      password: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(255) ] ],
      email: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(100) ] ],
      phone: [ null, [ Validators.minLength(0), Validators.maxLength(45) ] ],
      alias: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(30) ] ],
      salt: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(45) ] ],
      role: [ null, [ Validators.required, Validators.minLength(3), Validators.maxLength(20) ] ],
      active: [ null, [ Validators.required, Validators.pattern('^[0-9]{1}$') ] ],
      drivernumber: [ null, [ Validators.pattern('^[0-9]{4}$') ] ],
      debitor_id: [ null, [ Validators.required, Validators.pattern('^[0-9]{4}$') ] ],
      tournumber: [ null, [ Validators.pattern('^[0-9]{4}$') ] ],
      position: positionFormGroup
    });

    this.driverService.activeDriver.subscribe((activeDriver: Driver) => {
      this.activeDriver = activeDriver;
      this.driverForm.patchValue({
        firstname: activeDriver.firstname,
        surname: activeDriver.surname,
        drivernumber: activeDriver.drivernumber,
        tournumber: activeDriver.tournumber,
        position: {
          lat: activeDriver.position ? activeDriver.position.lat : '',
          lng: activeDriver.position ? activeDriver.position.lng : ''
        }
      });
    });
  }

  onSubmit() {
    console.log(this.driverForm.value);
  }
}
