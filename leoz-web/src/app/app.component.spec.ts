import { TestBed, async } from '@angular/core/testing';
import { RouterModule } from '@angular/router';

import { AppModule } from './app.module';
import { AppComponent } from './app.component';
import { AppFooterComponent } from './app-footer/app-footer.component';
import { MainMenuComponent } from './menu/main-menu/main-menu.component';
import { TopMenuComponent } from './menu/top-menu/top-menu.component';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { BaseRequestOptions, HttpModule } from '@angular/http';
import { YagaModule } from '@yaga/leaflet-ng2';
import { ErrormsgService } from './error/errormsg.service';
import { TourService } from './tour/tour.service';
import { DriverService } from './driver/driver.service';
import { routes } from './app.routing';
import { HomeComponent } from './home/home.component';
import { DriverComponent } from './driver/driver.component';
import { TourComponent } from './tour/tour.component';
import { DriverFormComponent } from './driver/driver-form/driver-form.component';
import { DriverListComponent } from './driver/driver-list/driver-list.component';
import { TourMapComponent } from './tour/tour-map/tour-map.component';
import { TourDriverListComponent } from './tour/tour-driver-list/tour-driver-list.component';
import { APP_BASE_HREF } from '@angular/common';

describe('AppComponent', () => {
  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [
        AppComponent,
        AppFooterComponent,
        MainMenuComponent,
        HomeComponent,
        TopMenuComponent,
        DriverComponent,
        TourComponent,
        DriverFormComponent,
        DriverListComponent,
        TourMapComponent,
        TourDriverListComponent
      ],
      imports: [
        FormsModule,
        ReactiveFormsModule,
        HttpModule,
        RouterModule.forRoot(routes, {useHash: false}),
        YagaModule
      ],
      providers: [
        ErrormsgService,
        DriverService,
        TourService,
        BaseRequestOptions,
        {provide: APP_BASE_HREF, useValue : '/' }
      ]
    }).compileComponents();
  }));

  it('should create the app', async(() => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.debugElement.componentInstance;
    expect(app).toBeTruthy();
  }));

});
