import { Pipe, PipeTransform } from '@angular/core';
import { User } from './user.model';
import { RoleGuard } from '../../core/auth/role.guard';
import { PermissionCheck } from '../../core/auth/permission-check';

@Pipe( {
  name: 'userfilter',
  pure: false
} )
export class UserFilterPipe implements PipeTransform {

  constructor( private roleGuard: RoleGuard ) {
  }

  transform( users: User[], args: any[] ) {
    if (!users) {
      return;
    }
    return users.filter( (user: User) => PermissionCheck.isAllowedRole( this.roleGuard.userRole, user.role ) );
  }

}
