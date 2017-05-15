import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DriverComponent } from './driver.component';
import { HttpModule } from '@angular/http';
import { ErrormsgService } from '../error/errormsg.service';
import { DriverService } from './driver.service';
import { DriverListComponent } from './driver-list/driver-list.component';
import { ReactiveFormsModule } from '@angular/forms';

describe('DriverComponent', () => {
  let component: DriverComponent;
  let fixture: ComponentFixture<DriverComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        DriverComponent,
        DriverListComponent
      ],
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
    fixture = TestBed.createComponent(DriverComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
