import { HomeComponent } from './home/home.component';
import {UserComponent} from './user/user.component';
import {TourComponent} from './tour/tour.component';
import { LoginComponent } from './login/login.component';
import { AuthenticationGuard } from './auth/authentication.guard';

export const routes = [
  { path: '', data: ['Home'], component: HomeComponent, canActivate: [AuthenticationGuard] },
  { path: 'login', name: ['Login'], component: LoginComponent },
  { path: 'logout', data: ['Logout'], component: LoginComponent },
  { path: 'user', data: ['Users'], component: UserComponent, canActivate: [AuthenticationGuard] },
  { path: 'tour', data: ['Tour'], component: TourComponent, canActivate: [AuthenticationGuard] },
  { path: '**', redirectTo: '' }
];
