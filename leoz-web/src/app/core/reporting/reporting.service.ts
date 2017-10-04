import { Injectable } from '@angular/core';
import * as jsPDF from 'jspdf';

import { TranslateService } from '../translate/translate.service';

@Injectable()
export abstract class ReportingService {

  dateFormatLong: string;
  dateFormatShort: string;

  constructor( public translate: TranslateService ) {
    this.translate.onLangChanged
      .subscribe( ( lang: string ) => {
        this.dateFormatShort = this.translate.setDateformat( 'internalShort' );
        this.dateFormatLong = this.translate.setDateformat( 'internalLong' );
      } );
    this.dateFormatShort = this.translate.setDateformat( 'internalShort' );
    this.dateFormatLong = this.translate.setDateformat( 'internalLong' );
  }

  abstract generateReports(...any): jsPDF;
}
