import { Pipe, PipeTransform } from '@angular/core';
import { Driver } from './driver.model';
import { RoleGuard } from '../../core/auth/role.guard';
import { PermissionCheck } from '../../core/auth/permission-check';

@Pipe( {
  name: 'driverfilter',
  pure: false
} )
export class DriverFilterPipe implements PipeTransform {

  constructor(private roleGuard: RoleGuard) {}

  transform( drivers: Driver[], args: any[] ) {
    const filterName = args[0] ? args[0] : 'driverfilter';
    if (!drivers) {
      return;
    }
    if (this.roleGuard.userRole === Driver.RoleEnum.DRIVER) {
      const currUser = JSON.parse( localStorage.getItem( 'currentUser' ) );
      return drivers.filter( ( driver: Driver ) => driver.email === currUser.user.email );
      // return drivers.filter( ( driver: Driver ) => driver.email === 'driver@deku.org' );
    } else {
      let filtered = filterName === 'driverfilter'
        ? drivers.filter( ( driver: Driver ) => driver.role === Driver.RoleEnum.DRIVER )
        : drivers.filter( ( driver: Driver ) => PermissionCheck.isAllowedRole( this.roleGuard.userRole, driver.role ) );
      filtered = filtered.filter( ( driver: Driver ) => driver.active );
      return filtered;
    }
  }
}
