import { Injectable } from '@angular/core';
import { Subject } from 'rxjs/Subject';

@Injectable()
export class KeyUpEventService {

  private keyUpEventSubject = new Subject<KeyboardEvent>();
  public keyUpEvents = this.keyUpEventSubject.asObservable();

  nextEvent( ev: KeyboardEvent ) {
    // console.log(ev);
    this.keyUpEventSubject.next( ev );
  }
}
