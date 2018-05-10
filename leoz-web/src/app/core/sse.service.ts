import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const EventSource: any = window[ 'EventSource' ];

@Injectable({
  providedIn: 'root',
})
export class SseService {

  constructor() {
  }

  observeMessages<T>(sseUrl: string): Observable<T> {
    return new Observable<T>(obs => {
      console.log( `initializing SSE ${sseUrl}....` );
      const es = new EventSource(sseUrl);
      es.addEventListener('message', (evt) => {
        obs.next(<T> JSON.parse(evt.data));
      });
      return () => {
        console.log( `....closing SSE ${sseUrl}` );
        es.close();
      };
    });
  }
}
