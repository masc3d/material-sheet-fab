import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class KeyUpEventService {

  private keyUpEventSubject = new Subject<KeyboardEvent>();
  public keyUpEvents$ = this.keyUpEventSubject.asObservable();

  nextEvent( ev: KeyboardEvent ) {
    // console.log(ev);
    this.keyUpEventSubject.next( ev );
  }

  public onKeyUp( key: string, takeUntilSubject: Subject<void>, callback: Function, $this: any, args?: any[] ) {
    this.keyUpEvents$
      .pipe(
        filter( ( ev: KeyboardEvent ) => ev.key === key ),
        takeUntil( takeUntilSubject )
      )
      .subscribe( () => args ? callback.apply( $this, args ) : callback.apply( $this ) );
  }
}
