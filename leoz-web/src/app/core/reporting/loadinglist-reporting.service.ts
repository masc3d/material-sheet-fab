import { Injectable } from '@angular/core';

import * as moment from 'moment';
import * as jsPDF from 'jspdf';

import { Loadinglist } from '../../dashboard/stationloading/loadinglistscan/loadinglist.model';
import { LoadinglistReportHeader } from '../../dashboard/stationloading/loadinglistscan/loadinglist-report-header.model';
import { Package } from '../../dashboard/stationloading/loadinglistscan/package.model';
import { Report } from './report.model';
import { ReportPart } from './report-part.model';
import { ReportingService } from './reporting.service';

@Injectable()
export class LoadinglistReportingService extends ReportingService {

  startPageNo: number;
  doc: jsPDF;

  generateReports( listsToPrint: Loadinglist[], llReportHeader: LoadinglistReportHeader ): jsPDF {
    const reports: Report[] = [];
    this.doc = new jsPDF();
    this.startPageNo = 1;
    listsToPrint.forEach( ( listToPrint: Loadinglist ) => {
      const reportHeaderRenderFunction = function ( doc: jsPDF, offsetY: number, currPageNo: number, data: any ) {
          doc.addImage( Report.logoImgData, Report.logoImgType, 140, 4, 53, 19 );
          doc.setFontSize( 16 );
          doc.setFontType( 'bold' );
          doc.text( `${data[ 'report_loadinglist' ]} ${data[ 'loadlistNo' ]}`, 5, 10 );
          doc.setFontSize( 10 );
          doc.text( `${data[ 'date' ]}: ${data[ 'dateFrom' ]} ${data[ 'to_auf' ]} ${data[ 'dateTo' ]}`, 5, 15 );
          doc.setFontSize( 8 );
          doc.text( `${data[ 'from' ]}: ${data[ 'loadingAddress' ]}`, 5, 19 );
          doc.text( `${data[ 'to_nach' ]}: ${data[ 'hubAddress' ]}`, 5, 22 );

          doc.setDrawColor( 0 );
          doc.setFillColor( 245, 245, 245 );
          doc.rect( 5, 25, 190, 14, 'FD' );

          doc.text( `${data[ 'total_CountShipments' ]}`, 6, 29 );
          doc.text( `${data[ 'shipmentCount' ]}`, 75, 29 );
          doc.text( `${data[ 'total_CountPackages' ]}`, 6, 33 );
          doc.text( `${data[ 'packageCount' ]}`, 75, 33 );
          doc.text( `${data[ 'total_Weight' ]}`, 6, 37 );
          doc.text( `${data[ 'totalWeight' ]} Kg`, 75, 37 );

          doc.setDrawColor( 0 );
          doc.setFillColor( 245, 245, 245 );
          doc.rect( 5, 45, 190, 6, 'FD' );

          doc.setFontSize( 10 );
          doc.text( `${data[ 'totalPackagesToTransport' ]}:`, 6, 49 );
          return doc;
        },
        reportFooterRenderFunction = function ( doc: jsPDF, offsetY: number, currPageNo: number, data: any ) {
          offsetY += 5;
          doc.setFontSize( 10 );
          doc.setFontType( 'bold' );
          doc.text( `${data[ 'nameOfDriver' ]}:`, 5, offsetY ); // 150
          doc.setLineWidth( 0.25 );
          doc.line( 40, offsetY, 100, offsetY );
          doc.text( `${data[ 'licensePlate' ]}:`, 110, offsetY );
          doc.line( 145, offsetY, 195, offsetY );

          offsetY += 8;
          doc.text( `${data[ 'printing_Date_Short' ]}`, 23, offsetY );

          offsetY += 2;
          doc.text( `${data[ 'loadingDate' ]}:`, 5, offsetY );
          doc.line( 20, offsetY, 50, offsetY );
          doc.text( `${data[ 'loadingTime' ]}:`, 60, offsetY );
          doc.line( 75, offsetY, 100, offsetY );
          doc.text( `${data[ 'signature' ]}:`, 110, offsetY );
          doc.line( 135, offsetY, 195, offsetY );

          offsetY += 5;
          doc.text( `${data[ 'llDriver' ]}`, 110, offsetY );
          return doc;
        },
        pageFooterRenderFunction = function ( doc: jsPDF, offsetY: number, currPageNo: number, data: any ) {
          doc.setFontSize( 10 );
          doc.setFontType( 'bold' );
          doc.text( `${data[ 'printingDate' ]}: ${data[ 'printing_Date_Long' ]}`, 5, 285 );
          doc.text( `${data[ 'page' ]}: ${currPageNo}`, 190, 285 );
          return doc;
        },
        report = new Report( 'loadinglist-report',
          new ReportPart( 47, reportHeaderRenderFunction,
            {
              report_loadinglist: this.translate.instant( 'report_loadinglist' ),
              date: this.translate.instant( 'date' ),
              dateFrom: moment( llReportHeader.dateFrom ).format( this.dateFormatShort ),
              to_auf: this.translate.instant( 'to_auf' ),
              dateTo: moment( llReportHeader.dateTo ).format( this.dateFormatShort ),
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

  private buildPageContent( loadinglist: Loadinglist ): ReportPart[] {
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
      stationPartRenderFunction = function ( doc: jsPDF, offsetY: number, currPageNo: number, data: any ) {
        const station: string = data.station,
          packs: Package[] = data.packs;
        let packageCounter = 0;

        doc.setFontSize( 8 );
        doc.setFontType( 'bold' );
        doc.text( `St: ${station}`, 5, offsetY );

        doc.setFontType( 'normal' );
        offsetY += 4;
        packs.forEach( ( p: Package ) => {
          const offsetXFactor = packageCounter % 5,
            offsetX = 37;
          doc.text( `${p.packageId}`, 5 + (offsetX * offsetXFactor), offsetY );
          doc.text( `Kg:`, 25 + (offsetX * offsetXFactor), offsetY );
          doc.text( `${p.weight}`, 30 + (offsetX * offsetXFactor), offsetY );
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
