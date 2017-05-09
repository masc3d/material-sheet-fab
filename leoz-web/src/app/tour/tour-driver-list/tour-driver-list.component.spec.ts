import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TourDriverListComponent } from './tour-driver-list.component';
import { ErrormsgService } from '../../error/errormsg.service';
import { DriverService } from '../../driver/driver.service';
import { HttpModule } from '@angular/http';
import { TourService } from '../tour.service';

describe('TourDriverListComponent', () => {
  let component: TourDriverListComponent;
  let fixture: ComponentFixture<TourDriverListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TourDriverListComponent ],
      imports: [
        HttpModule
      ],
      providers: [
        ErrormsgService,
        DriverService,
        TourService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TourDriverListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
