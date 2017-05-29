import { Pipe, PipeTransform } from '@angular/core';
import { Driver } from './driver.model';

@Pipe( {
  name: 'driverfilter',
  pure: false
} )
export class DriverFilterPipe implements PipeTransform {

  transform( items: Driver[], args: any[] ) {
    if (!items) {
      return;
    }
    return items.filter( item => item.role === Driver.RoleEnum.DRIVER );
  }
}
