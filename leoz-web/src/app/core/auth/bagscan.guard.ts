import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Station } from './station.model';
import { AuthenticationService } from './authentication.service';

@Injectable()
export class BagscanGuard implements CanActivate {

  public activeStation: Station;

  constructor(private auth: AuthenticationService) {
    auth.activeStation$.subscribe( ( activeStation: Station ) => this.activeStation = activeStation );
  }

  canActivate( route: ActivatedRouteSnapshot, state: RouterStateSnapshot ) {
    return (this.activeStation && this.activeStation.exportValuablesAllowed);
  }

}
