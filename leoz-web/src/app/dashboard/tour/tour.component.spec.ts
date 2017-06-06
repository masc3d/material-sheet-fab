import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import {
  DataTableModule,
  DropdownModule,
  ButtonModule } from 'primeng/primeng';

import { TourComponent } from './tour.component';
import { TourDriverListComponent } from './tour-driver-list/tour-driver-list.component';
import { TourMapComponent } from './tour-map/tour-map.component';
import { HttpModule } from '@angular/http';
import { YagaModule } from '@yaga/leaflet-ng2';
import { TourService } from './tour.service';
import { SharedModule } from '../../shared/shared.module';
import { CoreModule } from '../../core/core.module';
import { DriverService } from './driver.service';
import { RouterTestingModule } from '@angular/router/testing';

describe('TourComponent', () => {
  let component: TourComponent;
  let fixture: ComponentFixture<TourComponent>;
  let driverService: DriverService;
  let spy: jasmine.Spy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        TourComponent,
        TourDriverListComponent,
        TourMapComponent
      ],
      imports: [
        RouterTestingModule,
        HttpModule,
        YagaModule,
        SharedModule,
        DataTableModule,
        DropdownModule,
        ButtonModule,
        CoreModule.forRoot()
      ],
      providers: [
        DriverService,
        TourService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TourComponent);
    component = fixture.componentInstance;
    driverService = fixture.debugElement.injector.get(DriverService);
  });

  it('should create', () => {
    spy = spyOn(driverService, 'getDrivers');
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });
});
