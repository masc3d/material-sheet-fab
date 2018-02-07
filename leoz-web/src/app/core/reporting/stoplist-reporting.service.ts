import { Injectable } from '@angular/core';

import * as moment from 'moment';
import * as jsPDF from 'jspdf';

import { Report } from './report.model';
import { ReportPart } from './report-part.model';
import { ReportingService } from './reporting.service';
import { Deliverylist } from '../models/deliverylist.model';
import { DeliverylistItem } from '../models/deliverylist.item.model';
import { Tour } from '../models/tour.model';
import { roundDecimals } from '../math/roundDecimals';
import { TourListItem } from '../models/tour-list-item.model';
import { Package } from '../models/package.model';

@Injectable()
export class StoplistReportingService extends ReportingService {

  startPageNo: number;
  doc: jsPDF;

  generateReports( listsToPrint: Tour[] ): jsPDF {
    const reports: Report[] = [];
    this.doc = new jsPDF();
    this.startPageNo = 1;
    listsToPrint.forEach( ( listToPrint: Tour ) => {
      const reportHeaderRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {
          doc.addImage( Report.logoImgData, Report.logoImgType, offsetX + 140, offsetY, 53, 19 );
          doc.setFontSize( 16 );
          doc.setFontType( 'bold' );
          doc.text( `${data[ 'report_tourplan' ]}`, offsetX, offsetY + 10 );
          doc.text( `${data[ 'tourlist_Date_Short' ]}`, offsetX + 70, offsetY + 10 );
          doc.setFontSize( 12 );
          doc.text( `${data[ 'tourID' ]} (${data[ 'tourIDValue' ]})`, offsetX, offsetY + 15 );

          doc.text( `${data[ 'deliverylistID' ]} ${data[ 'curentUserMail' ]}`, offsetX + 70, offsetY + 15 );
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
        reportFooterRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {
          offsetY += 80;
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
          new ReportPart( 57, reportHeaderRenderFunction,
            {
              report_tourplan: this.translate.instant( 'report_tourplan' ),
              tourlist_Date_Short: moment().format( this.dateFormatShort ),
              tourID: this.translate.instant( 'tourID' ),
              deliverylistID: listToPrint.deliverylistId,
              tourIDValue: listToPrint.id,

              curentUserMail: JSON.parse( localStorage.getItem( 'currentUser' ) ).user.email,
              status: this.translate.instant( 'status' ),
              optimization: listToPrint.optimized != null
                ? this.translate.instant( 'optimized' )
                : this.translate.instant( 'not_optimized' ),

              total_CountShipments: this.translate.instant( 'total_CountShipments' ),
              shipmentCount: listToPrint.totalShipments,
              total_CountPackages: this.translate.instant( 'total_CountPackages' ),
              packageCount: listToPrint.totalPackages,
              total_Weight: this.translate.instant( 'total_Weight' ),
              totalWeight: listToPrint.totalWeight,
            } ),
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

  private buildPageContent( tour: Tour ): ReportPart[] {
    const stopListRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {

      doc.setFontSize( 8 );
      doc.setFontType( 'bold' );
      // doc.text( `tour.id: ${tour.id}`, offsetX, offsetY );
      doc.setFontType( 'normal' );

      tour.orders.forEach( ( dli: DeliverylistItem ) => {
        offsetY += 10;
        doc.text( `${dli.deliveryAddress.line1}`, offsetX, offsetY );
        doc.text( `${dli.deliveryAddress.street} ${dli.deliveryAddress.streetNo}`, offsetX + 40, offsetY );
        doc.text( `${dli.deliveryAddress.zipCode} ${dli.deliveryAddress.city}`, offsetX + 95, offsetY );
        doc.text( `Pkst: ${dli.parcels.length}`, offsetX + 40, offsetY + 4 );
        doc.text( `Termin: ${moment(dli.deliveryAppointment.dateStart).format( 'HH:mm' )}`, offsetX + 95, offsetY + 4 );
        doc.text( `bis ${moment(dli.deliveryAppointment.dateEnd).format( 'HH:mm' )}`, offsetX + 115, offsetY + 4 );
      } );
      return doc;
    };
    return [ new ReportPart( null, stopListRenderFunction, tour ) ];
  }

}
