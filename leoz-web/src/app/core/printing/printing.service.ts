import { Injectable } from '@angular/core';
import * as jsPDF from 'jspdf';

@Injectable()
export class PrintingService {

  printReports( doc: jsPDF, filename: string, saving: boolean ) {
    if (saving) {
      doc.save( `${filename}.pdf` );
    } else {
      window.open( URL.createObjectURL( doc.output( 'blob' ) ), '_blank',
      'top=0,left=0,height=100%,width=auto,directories=no,titlebar=no,toolbar=no,location=no,'
      + 'status=no,menubar=no,scrollbars=no,resizable=no' );
    }
  }
}
