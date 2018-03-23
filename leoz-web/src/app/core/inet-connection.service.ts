import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';
import { Observable } from 'rxjs/Observable';
import { distinctUntilChanged } from 'rxjs/operators';
import 'rxjs/add/observable/fromEvent';

import { environment } from '../../environments/environment';

@Injectable()
export class InetConnectionService {

  private runPeriodicPing = false;
  private isPinging = false;

  private isOnlineSubject = new BehaviorSubject( false );
  public isOnline$ = this.isOnlineSubject.asObservable().pipe(distinctUntilChanged());

  constructor( private http: HttpClient ) {
    Observable.fromEvent( window, 'online' ).subscribe( ( evt: Event ) => {
      this.runPeriodicPing = true;
      this.startPeriodicPing();
    } );
    Observable.fromEvent( window, 'offline' ).subscribe( ( evt: Event ) => {
      this.isOnlineSubject.next( false );
      this.runPeriodicPing = false;
    } );
    this.runPeriodicPing = true;
    this.startPeriodicPing();
  }

  public isOffline() {
    this.runPeriodicPing = true;
    this.isOnlineSubject.next( false );
    this.startPeriodicPing();
  }

  public startPeriodicPing() {
    if (!this.isPinging) {
      this.isPinging = true;
      this.ping(this);
    }
  }

  private ping(ics: InetConnectionService) {
    if (ics.runPeriodicPing) {
      ics.isPinging = true;
      this.http.get( environment.pingURL, { observe: 'response' } )
        .subscribe( ( resp: HttpResponse<any> ) => {
            if (resp.status === 200) {
              ics.runPeriodicPing = false;
              ics.isPinging = false;
              ics.isOnlineSubject.next( true );
            } else {
              ics.repeatPing( ics );
            }
          },
          _ => {
          ics.repeatPing( ics );
          } );
    } else {
      ics.isPinging = false;
    }
  }

  private repeatPing( ics: InetConnectionService ) {
    setTimeout( function () {
      ics.ping( ics );
    }, 5000 );
  }
}
