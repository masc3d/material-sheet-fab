import { Injectable } from '@angular/core';
import * as jsPDF from 'jspdf';
import { MsgService } from '../../shared/msg/msg.service';

@Injectable({
  providedIn: 'root',
})
export class PrintingService {

  constructor(protected msgService: MsgService) {
  }

  printReports( doc: jsPDF, filename: string, saving: boolean ) {
    if (saving) {
      doc.save( `${filename}.pdf` );
    } else {
      const popupWindow = window.open( URL.createObjectURL( doc.output( 'blob' ) ), '_blank',
      'top=0,left=0,height=auto,width=auto,directories=no,titlebar=no,toolbar=no,location=no,' +
        'status=no,menubar=no,scrollbars=no,resizable=no' );
      setTimeout( () => {
        if (!popupWindow || !popupWindow.innerHeight || popupWindow.innerHeight === 0) {
          this.msgService.error( 'Popup geblocket....' );
        }
      }, 30 );
    }
  }
}
