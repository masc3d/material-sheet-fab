import { Injectable } from '@angular/core';
import * as jsPDF from 'jspdf';
import ioBarcode from 'io-barcode';

import { Report } from './report.model';
import { ReportPart } from './report-part.model';

import { ReportingService } from './reporting.service';
import { checkdigitInt25 } from '../math/checkdigitInt25';

@Injectable()
export class BagscanReportingService extends ReportingService {

  generateReports(): jsPDF {
    // generate barcode hardcoded
    let barcodeImgData;

    // If using UMD bundle via a <script> tag, ioBarcode is exposed as a global

    let belegNr = '345678901';
    belegNr = ('000000000000' + belegNr).substr( -11 );
    const checkSum = checkdigitInt25( belegNr );
    const barcode = belegNr + checkSum;
    console.log( barcode );
    console.log( belegNr + '(' + checkSum + ')' );

    const canvas = ioBarcode.ITF( barcode, {
      width: 1.9,
      height: 50,
      displayValue: false
    } );

    barcodeImgData = canvas.toDataURL( 'image/png' );

    const reportHeaderRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {
      // offsetX = 10, offsetY = 10
      doc.setDrawColor( 0 );
      doc.setFillColor( 245, 245, 245 );
      doc.rect( offsetX - 10, offsetY - 10, 99, 128, 'FD' );
      doc.rect( offsetX, offsetY - 8, 79, 125, 'FD' );
      doc.addImage( data.barcodeImgData, data.barcodeImgType, offsetX + 8, offsetY - 5 );
      doc.setFontSize( 7 );
      doc.text( belegNr + '(' + checkSum + ')', offsetX + 30, offsetY + 11 );
      doc.addImage( Report.logoImgData, Report.logoImgType, offsetX, offsetY + 90, 53, 19 );


      // console.log('offset', offsetX, offsetY);
      /*doc.setFontSize( 16 );
      doc.setFontType( 'bold' );
      doc.text( `${data[ 'report_loadinglist' ]} ${data[ 'loadlistNo' ]}`, offsetX, offsetY + 10 );
      doc.setDrawColor( 0 );
      doc.setFillColor( 245, 245, 245 );
      doc.rect( offsetX, offsetY + 25, 190, 14, 'FD' );
      doc.setLineWidth( 0.25 );
      doc.line( offsetX + 35, offsetY, offsetX + 95, offsetY );*/
      return doc;
    };

    const report = new Report( 'bagscan-report', 10, 10,
      new ReportPart( 57, reportHeaderRenderFunction, { barcodeImgData: barcodeImgData, barcodeImgType: 'PNG' } ) );
    return report.generate( new jsPDF( {
      orientation: 'p',
      unit: 'mm',
      format: [ 128, 99 ]
    } ), 1 );
  }

}
