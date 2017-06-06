import { Pipe, PipeTransform } from '@angular/core';
import { Driver } from './driver.model';
import { RoleGuard } from '../../core/auth/role.guard';

@Pipe( {
  name: 'driverfilter',
  pure: false
} )
export class DriverFilterPipe implements PipeTransform {

  constructor(private roleGuard: RoleGuard) {}

  transform( drivers: Driver[], args: any[] ) {
    if (!drivers) {
      return;
    }
    if (this.roleGuard.userRole === Driver.RoleEnum.Driver) {
      // return drivers.filter( ( driver: Driver ) => driver.email === currUserEmail );
      return drivers.filter( ( driver: Driver ) => driver.email === 'driver@deku.org' );
    } else {
      return drivers.filter( ( driver: Driver ) => driver.role === Driver.RoleEnum.Driver );
    }
  }
}
