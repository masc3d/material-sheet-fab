import { Injectable } from '@angular/core';
import * as jsPDF from 'jspdf';

@Injectable()
export class PrintingService {

  doc: jsPDF;

  printReports( doc: jsPDF, print: boolean ) {
    const popupWin = window.open( URL.createObjectURL( doc.output( 'blob' ) ), '_blank',
      'top=0,left=0,height=100%,width=auto,directories=no,titlebar=no,toolbar=no,location=no,'
      + 'status=no,menubar=no,scrollbars=no,resizable=no' );
    if (print) {
      popupWin.onload = function () {
        setTimeout( () => popupWin.print(), 0 );
        popupWin.onfocus = function () {
          setTimeout( () => popupWin.close(), 0 );
        }
      };
    }
  }
}
