import { Injectable } from '@angular/core';

import * as moment from 'moment';
import * as jsPDF from 'jspdf';
import * as QRCode from 'qrcode';

import { Report } from './report.model';
import { ReportPart } from './report-part.model';
import { ReportingService } from './reporting.service';
import { Stop, Tour } from '../models/tour.model';
import { roundDecimalsAsString } from '../math/roundDecimals';

@Injectable({
  providedIn: 'root',
})
export class StoplistReportingService extends ReportingService {

  startPageNo: number;
  doc: jsPDF;

  async generateReports( tours: Tour[] ): Promise<jsPDF> {

    const reportHeaderRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {
        doc.addImage( Report.logoImgData, Report.logoImgType, offsetX + 140, offsetY, 53, 19 );
        doc.setFontSize( 16 );
        doc.setFontType( 'bold' );

        doc.text( `${data[ 'report_tourplan' ]}`, offsetX, offsetY + 6 );
        doc.text( `${data[ 'tourlist_Date_Short' ]}`, offsetX + 70, offsetY + 6 );
        doc.setFontSize( 12 );
        doc.text( `${data[ 'tourID' ]} (${data[ 'tourIDValue' ]})`, offsetX, offsetY + 15 );

        if (data.barcodeImgData) {
          doc.addImage( data.barcodeImgData, data.barcodeImgType, offsetX + 40, offsetY );
        }

        doc.text( `${data[ 'deliverylistID' ] ? data[ 'deliverylistID' ] : ''} ${data[ 'curentUserMail' ]}`, offsetX + 70, offsetY + 15 );
        doc.text( `${data[ 'status' ]}`, offsetX, offsetY + 20 );
        doc.text( `${data[ 'optimization' ]}`, offsetX + 70, offsetY + 20 );

        doc.setFontSize( 10 );
        doc.setDrawColor( 0 );
        doc.setFillColor( 245, 245, 245 );
        doc.rect( offsetX, offsetY + 25, 190, 14, 'FD' );

        doc.text( `${data[ 'total_CountShipments' ]}`, offsetX + 1, offsetY + 29 );
        doc.text( `${data[ 'shipmentCount' ]}`, offsetX + 70, offsetY + 29 );
        doc.text( `${data[ 'total_CountPackages' ]}`, offsetX + 1, offsetY + 33 );
        doc.text( `${data[ 'packageCount' ]}`, offsetX + 70, offsetY + 33 );
        doc.text( `${data[ 'total_Weight' ]}`, offsetX + 1, offsetY + 37 );
        doc.text( `${data[ 'totalWeight' ]} Kg`, offsetX + 70, offsetY + 37 );

        return doc;
      },
      pageFooterRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {
        doc.setFontSize( 10 );
        doc.setFontType( 'bold' );
        doc.text( `${data[ 'printingDate' ]}: ${data[ 'printing_Date_Long' ]}`, offsetX, 285 );
        doc.text( `${data[ 'page' ]}: ${currPageNo}`, offsetX + 175, 285 );
        return doc;
      };

    const reports: Report[] = [];
    this.doc = new jsPDF();
    this.startPageNo = 1;
    await tours.forEach( async ( tour: Tour ) => {
      const barcodeImgData = await QRCode.toDataURL( `DK;TR;${tour.id};${tour.uid.toUpperCase()}`, { width: 90 } );
      const report = new Report( 'stoplist-report', 10, 10,
        new ReportPart( 35, reportHeaderRenderFunction,
          {
            barcodeImgData: barcodeImgData,
            barcodeImgType: 'PNG',
            report_tourplan: this.translate.instant( 'report_tourplan' ),
            tourlist_Date_Short: moment().format( this.dateFormatShort ),
            tourID: this.translate.instant( 'tourID' ),
            deliverylistID: tour.customId,
            tourIDValue: tour.id,

            curentUserMail: JSON.parse( localStorage.getItem( 'currentUser' ) ).user.email,
            status: this.translate.instant( 'status' ),
            optimization: tour.optimized != null
              ? this.translate.instant( 'optimized' )
              : this.translate.instant( 'not_optimized' ),

            total_CountShipments: this.translate.instant( 'total_CountShipments' ),
            shipmentCount: tour.totalShipments,
            total_CountPackages: this.translate.instant( 'total_CountPackages' ),
            packageCount: tour.totalPackages,
            total_Weight: this.translate.instant( 'total_Weight' ),
            totalWeight: tour.totalWeight,
          } ),
        null,
        this.buildPageContent( tour ),
        new ReportPart( 23, pageFooterRenderFunction, {
          printingDate: this.translate.instant( 'printingDate' ),
          printing_Date_Long: moment().format( this.dateFormatLong ),
          page: this.translate.instant( 'page' ),
        } ),
        null );
      reports.push( report );
    } );

    reports.forEach( ( report: Report ) => this.addReport( this.doc, report ) );

    return this.doc;
  }

  private addReport( doc: jsPDF, report: Report ) {
    this.doc = report.generate( doc, this.startPageNo );
    this.startPageNo += report.totalPages;
  }

  private buildPageContent( tour: Tour ): ReportPart[] {
    return tour.stops.map( stop => this.createStopPart( stop ) );
  }

  private createStopPart( stop: Stop ): ReportPart {
    const stopListRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {

      doc.setFontSize( 8 );
      doc.setFontType( 'bold' );
      doc.setFontType( 'normal' );

      let etaFrom = '',
        etaTo = '';
      if (data.route) {
        etaFrom = moment( data.route.eta.from ).format( 'HH:mm' );
        etaTo = moment( data.route.eta.to ).format( 'HH:mm' );
      }
      offsetY += 9;
      const weight = roundDecimalsAsString(  data.weight, 10, true );
      const amount = data.parcelNumbers ? data.parcelNumbers.length : '';
      doc.text( `${data.address.line1}`, offsetX, offsetY );
      doc.text( `${data.address.street} ${data.address.streetNo}`, offsetX + 70, offsetY );
      doc.text( `${data.address.zipCode} ${data.address.city}`, offsetX + 125, offsetY );
      doc.text( `Anzahl: ${amount}`, offsetX + 70, offsetY + 4 );
      doc.text( `Gewicht: ${weight} kg`, offsetX + 85, offsetY + 4 );
      doc.text( `ETA: ${etaFrom}`, offsetX, offsetY + 4 );
      doc.text( `bis ${etaTo}`, offsetX + 15, offsetY + 4 );
      doc.text( `Termin: ${moment( data.appointmentStart ).format( 'HH:mm' )}`, offsetX + 125, offsetY + 4 );
      doc.text( `bis ${moment( data.appointmentEnd ).format( 'HH:mm' )}`, offsetX + 145, offsetY + 4 );
      return doc;
    };
    return new ReportPart( 11.5, stopListRenderFunction, stop );
  }

}
