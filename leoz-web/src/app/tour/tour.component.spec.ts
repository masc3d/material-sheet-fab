import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TourComponent } from './tour.component';
import { TourDriverListComponent } from './tour-driver-list/tour-driver-list.component';
import { TourMapComponent } from './tour-map/tour-map.component';
import { HttpModule } from '@angular/http';
import { YagaModule } from '@yaga/leaflet-ng2';
import { ErrormsgService } from '../error/errormsg.service';
import { DriverService } from '../driver/driver.service';
import { TourService } from './tour.service';

describe('TourComponent', () => {
  let component: TourComponent;
  let fixture: ComponentFixture<TourComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TourComponent,
        TourDriverListComponent,
        TourMapComponent
      ],
      imports: [
        HttpModule,
        YagaModule
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
    fixture = TestBed.createComponent(TourComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
