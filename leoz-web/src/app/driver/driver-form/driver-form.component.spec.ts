import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverFormComponent } from './driver-form.component';
import { ReactiveFormsModule } from '@angular/forms';
import { DriverService } from '../driver.service';
import { ErrormsgService } from '../../error/errormsg.service';
import { HttpModule } from '@angular/http';

describe('DriverFormComponent', () => {
  let component: DriverFormComponent;
  let fixture: ComponentFixture<DriverFormComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DriverFormComponent ],
      imports: [
        ReactiveFormsModule,
        HttpModule
      ],
      providers: [
        ErrormsgService,
        DriverService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DriverFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
