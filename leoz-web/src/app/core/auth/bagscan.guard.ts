import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Station } from './station.model';

@Injectable()
export class BagscanGuard implements CanActivate {

  public activeStation: Station;

  constructor() {
    if (localStorage.getItem( 'activeStation' )) {
      this.activeStation = JSON.parse( localStorage.getItem( 'activeStation' ) );
    }
  }

  canActivate( route: ActivatedRouteSnapshot, state: RouterStateSnapshot ) {
    return (this.activeStation && this.activeStation.exportValuablesAllowed);
  }

}
