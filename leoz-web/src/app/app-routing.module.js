import { HomeComponent } from './home/home.component';
import { DriverComponent } from './driver/driver.component';
import { TourComponent } from './tour/tour.component';
export var routes = [
    { path: '', data: ['Home'], component: HomeComponent },
    { path: 'driver', data: ['Drivers'], component: DriverComponent },
    { path: 'tour', data: ['Tour'], component: TourComponent },
    { path: '**', redirectTo: '/' }
];
//# sourceMappingURL=app.routing.js.map