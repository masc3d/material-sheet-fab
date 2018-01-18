import { Injectable } from '@angular/core';

import * as moment from 'moment';
import * as jsPDF from 'jspdf';

import { Report } from './report.model';
import { ReportPart } from './report-part.model';
import { ReportingService } from './reporting.service';
import { Deliverylist } from '../models/deliverylist.model';
import { DeliverylistItem } from '../models/deliverylist.item.model';

@Injectable()
export class StoplistReportingService extends ReportingService {

  startPageNo: number;
  doc: jsPDF;

  generateReports( listsToPrint: Deliverylist[] ): jsPDF {
    const reports: Report[] = [];
    this.doc = new jsPDF();
    this.startPageNo = 1;
    listsToPrint.forEach( ( listToPrint: Deliverylist ) => {
      const reportHeaderRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {
          doc.addImage( Report.logoImgData, Report.logoImgType, offsetX + 140, offsetY, 53, 19 );
          doc.setFontSize( 16 );
          doc.setFontType( 'bold' );
          doc.text( `${data[ 'report_loadinglist' ]} ${data[ 'loadlistNo' ]}`, offsetX, offsetY + 10 );
          doc.setFontSize( 10 );
          doc.text( `${data[ 'date' ]}: ${data[ 'dateFrom' ]} ${data[ 'to_auf' ]} ${data[ 'dateTo' ]}`, offsetX, offsetY + 15 );
          doc.setFontSize( 8 );
          doc.text( `${data[ 'from' ]}: ${data[ 'loadingAddress' ]}`, offsetX, offsetY + 19 );
          doc.text( `${data[ 'to_nach' ]}: ${data[ 'hubAddress' ]}`, offsetX, offsetY + 22 );

          doc.setDrawColor( 0 );
          doc.setFillColor( 245, 245, 245 );
          doc.rect( offsetX, offsetY + 25, 190, 14, 'FD' );

          doc.text( `${data[ 'total_CountShipments' ]}`, offsetX + 1, offsetY + 29 );
          doc.text( `${data[ 'shipmentCount' ]}`, offsetX + 70, offsetY + 29 );
          doc.text( `${data[ 'total_CountPackages' ]}`, offsetX + 1, offsetY + 33 );
          doc.text( `${data[ 'packageCount' ]}`, offsetX + 70, offsetY + 33 );
          doc.text( `${data[ 'total_Weight' ]}`, offsetX + 1, offsetY + 37 );
          doc.text( `${data[ 'totalWeight' ]} Kg`, offsetX + 70, offsetY + 37 );

          doc.setDrawColor( 0 );
          doc.setFillColor( 245, 245, 245 );
          doc.rect( offsetX, offsetY + 45, 190, 6, 'FD' );

          doc.setFontSize( 10 );
          doc.text( `${data[ 'totalPackagesToTransport' ]}:`, offsetX + 1, offsetY + 49 );
          return doc;
        },
        reportFooterRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {
          offsetY += 5;
          doc.setFontSize( 10 );
          doc.setFontType( 'bold' );
          doc.text( `${data[ 'nameOfDriver' ]}:`, offsetX, offsetY ); // 150
          doc.setLineWidth( 0.25 );
          doc.line( offsetX + 35, offsetY, offsetX + 95, offsetY );
          doc.text( `${data[ 'licensePlate' ]}:`, offsetX + 105, offsetY );
          doc.line( offsetX + 140, offsetY, offsetX + 190, offsetY );

          offsetY += 8;
          doc.text( `${data[ 'printing_Date_Short' ]}`, offsetX + 18, offsetY );

          offsetY += 2;
          doc.text( `${data[ 'loadingDate' ]}:`, offsetX, offsetY );
          doc.line( offsetX + 15, offsetY, offsetX + 45, offsetY );
          doc.text( `${data[ 'loadingTime' ]}:`, offsetX + 55, offsetY );
          doc.line( offsetX + 70, offsetY, offsetX + 95, offsetY );
          doc.text( `${data[ 'signature' ]}:`, offsetX + 105, offsetY );
          doc.line( offsetX + 130, offsetY, offsetX + 190, offsetY );

          offsetY += 5;
          doc.text( `${data[ 'llDriver' ]}`, offsetX + 105, offsetY );
          return doc;
        },
        pageFooterRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {
          doc.setFontSize( 10 );
          doc.setFontType( 'bold' );
          doc.text( `${data[ 'printingDate' ]}: ${data[ 'printing_Date_Long' ]}`, offsetX, 285 );
          doc.text( `${data[ 'page' ]}: ${currPageNo}`, offsetX + 175, 285 );
          return doc;
        },
        report = new Report( 'stoplist-report', 10, 10,
          null,
          null,
          this.buildPageContent( listToPrint ),
          new ReportPart( 23, pageFooterRenderFunction, {
            printingDate: this.translate.instant( 'printingDate' ),
            printing_Date_Long: moment().format( this.dateFormatLong ),
            page: this.translate.instant( 'page' ),
          } ),
          new ReportPart( 28, reportFooterRenderFunction, {
            nameOfDriver: this.translate.instant( 'nameOfDriver' ),
            licensePlate: this.translate.instant( 'licensePlate' ),
            loadingDate: this.translate.instant( 'loadingDate' ),
            printing_Date_Short: moment().format( this.dateFormatShort ),
            loadingTime: this.translate.instant( 'loadingTime' ),
            signature: this.translate.instant( 'signature' ),
            llDriver: this.translate.instant( 'llDriver' ),
          } ) );
      reports.push( report );
    } );

    reports.forEach( ( report: Report ) => this.addReport( this.doc, report ) );

    return this.doc;
  }

  private addReport( doc: jsPDF, report: Report ) {
    this.doc = report.generate( doc, this.startPageNo );
    this.startPageNo += report.totalPages;
  }

  private buildPageContent( deliverylist: Deliverylist ): ReportPart[] {
    const deliveryListRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {
      doc.setFontSize( 8 );
      doc.setFontType( 'bold' );
      doc.text( `deliverylist.id: ${deliverylist.id}`, offsetX, offsetY );

      doc.setFontType( 'normal' );
      offsetY += 4;
      deliverylist.orders.forEach( ( dli: DeliverylistItem ) => {
        doc.text( `dli.parcels.length: ${dli.parcels.length}`, offsetX, offsetY );
      } );
      return doc;
    };
    return [ new ReportPart( null, deliveryListRenderFunction, deliverylist ) ];
  }

}
