import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TourDriverListComponent } from './tour-driver-list.component';
import { HttpModule } from '@angular/http';
import { TourService } from '../tour.service';
import { SharedModule } from '../../../shared/shared.module';
import { DriverService } from '../driver.service';
import { CoreModule } from '../../../core/core.module';
import { RouterTestingModule } from '@angular/router/testing';
import { ButtonModule, DataTableModule, DropdownModule } from 'primeng/primeng';

describe('TourDriverListComponent', () => {
  let component: TourDriverListComponent;
  let fixture: ComponentFixture<TourDriverListComponent>;
  let driverService: DriverService;
  let spy: jasmine.Spy;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TourDriverListComponent ],
      imports: [
        RouterTestingModule,
        HttpModule,
        SharedModule,
        DataTableModule,
        DropdownModule,
        ButtonModule,
        CoreModule.forRoot(),
      ],
      providers: [
        DriverService,
        TourService
      ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TourDriverListComponent);
    component = fixture.componentInstance;
    driverService = fixture.debugElement.injector.get(DriverService);
  });

  it('should create', () => {
    spy = spyOn(driverService, 'getDrivers');
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });
});
