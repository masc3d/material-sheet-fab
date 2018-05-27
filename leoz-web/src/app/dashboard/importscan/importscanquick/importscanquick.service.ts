import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { distinctUntilChanged } from 'rxjs/operators'
import { TranslateService } from '../../../core/translate/translate.service';
import { MsgService } from '../../../shared/msg/msg.service';

@Injectable({
  providedIn: 'root',
})
export class ImportscanquickService {

  private packsLoadingSubject = new BehaviorSubject<boolean>( false );
  public packsLoading$ = this.packsLoadingSubject.asObservable().pipe( distinctUntilChanged() );

  constructor( protected http: HttpClient,
               protected translate: TranslateService,
               protected msgService: MsgService) {
  }
}
