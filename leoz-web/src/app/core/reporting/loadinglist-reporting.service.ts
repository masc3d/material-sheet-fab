import { Injectable } from '@angular/core';

import * as moment from 'moment';
import * as jsPDF from 'jspdf';

import { Exportlist } from '../../dashboard/export/exportlist.model';
import { LoadinglistReportHeader } from '../../dashboard/export/loadinglistscan/loadinglist-report-header.model';
import { Package } from '../models/package.model';
import { Report } from './report.model';
import { ReportPart } from './report-part.model';
import { ReportingService } from './reporting.service';

@Injectable({
  providedIn: 'root',
})
export class LoadinglistReportingService extends ReportingService {

  startPageNo: number;
  doc: jsPDF;

  generateReports( listsToPrint: Exportlist[], llReportHeader: LoadinglistReportHeader ): jsPDF {
    const reports: Report[] = [];
    this.doc = new jsPDF();
    this.startPageNo = 1;
    listsToPrint.forEach( ( listToPrint: Exportlist ) => {
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
        report = new Report( 'loadinglist-report', 10, 10,
          new ReportPart( 57, reportHeaderRenderFunction,
            {
              report_loadinglist: this.translate.instant( 'report_loadinglist' ),
              date: this.translate.instant( 'date' ),
              dateFrom: llReportHeader.dateFrom.format( this.dateFormatShort ),
              to_auf: this.translate.instant( 'to_auf' ),
              dateTo: llReportHeader.dateTo.format( this.dateFormatShort ),
              loadlistNo: llReportHeader.loadlistNo,
              from: this.translate.instant( 'from' ),
              loadingAddress: llReportHeader.loadingAddress,
              to_nach: this.translate.instant( 'to_nach' ),
              hubAddress: llReportHeader.hubAddress,
              total_CountShipments: this.translate.instant( 'total_CountShipments' ),
              shipmentCount: llReportHeader.shipmentCount,
              total_CountPackages: this.translate.instant( 'total_CountPackages' ),
              packageCount: llReportHeader.packageCount,
              total_Weight: this.translate.instant( 'total_Weight' ),
              totalWeight: llReportHeader.totalWeight,
              totalPackagesToTransport: this.translate.instant( 'totalPackagesToTransport' ),
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

  private buildPageContent( loadinglist: Exportlist ): ReportPart[] {
    const pageContents: ReportPart[] = [];
    const groupedPackages = {};
    for (const p of loadinglist.packages) {
      this.groupBy( groupedPackages, p, 'devliveryStation' )
    }

    for (const deliveryStation in groupedPackages) {
      if (groupedPackages.hasOwnProperty( deliveryStation )) {
        const packages = groupedPackages[ deliveryStation ];
        const stationPart = this.createStationPart( deliveryStation, packages );
        pageContents.push( stationPart );
      }
    }
    return pageContents;
  }

  private groupBy( groupedPackages: Object,
                   p: Package,
                   fieldName: string ): void {
    const tmp = groupedPackages[ p[ fieldName ] ] ? groupedPackages[ p[ fieldName ] ] : [];
    tmp.push( p );
    groupedPackages[ p[ fieldName ] ] = tmp;
  }

  private createStationPart( deliveryStation: string, packages: Package[] ): ReportPart {
    const calcHeight = function ( packCount: number ) {
        return 8 + (4 * Math.ceil( packCount / 5 ));
      },
      stationPartRenderFunction = function ( doc: jsPDF, offsetX: number, offsetY: number, currPageNo: number, data: any ) {
        const station: string = data.station,
          packs: Package[] = data.packs;
        let packageCounter = 0;

        doc.setFontSize( 8 );
        doc.setFontType( 'bold' );
        doc.text( `St: ${station}`, offsetX, offsetY );

        doc.setFontType( 'normal' );
        offsetY += 4;
        packs.forEach( ( p: Package ) => {
          const offsetXFactor = packageCounter % 5,
            innerOffsetX = 37;
          doc.text( `${p.parcelNo}`, offsetX + (innerOffsetX * offsetXFactor), offsetY );
          doc.text( `Kg:`, offsetX + 20 + (innerOffsetX * offsetXFactor), offsetY );
          doc.text( `${p.realWeight}`, offsetX + 25 + (innerOffsetX * offsetXFactor), offsetY );
          packageCounter += 1;
          if (packageCounter % 5 === 0) {
            offsetY += 4;
          }
        } );
        return doc;
      },
      stationPartData = { station: deliveryStation, packs: packages };
    return new ReportPart( calcHeight( packages.length ), stationPartRenderFunction, stationPartData );
  }
}
