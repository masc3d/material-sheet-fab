import { Injectable } from '@angular/core';
import { Response, ResponseOptions } from '@angular/http';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class ErrormsgService {

  private latestErrorSubject = new BehaviorSubject<Response>(new Response(new ResponseOptions({status: 200})));
  public latestError = this.latestErrorSubject.asObservable().distinctUntilChanged();

  public changeError(errorResponse: Response) {
    this.latestErrorSubject.next(errorResponse);
  }
}
