import { ReportPart } from './report-part.model';
import * as jsPDF from 'jspdf';

export class Report extends ReportPart {

  static logoImgData = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAN8AAABPCAYAAABrnxBvAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAAyRpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuMy1jMDExIDY2LjE0NTY2MSwgMjAxMi8wMi8wNi0xNDo1NjoyNyAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczp4bXBNTT0iaHR0cDovL25zLmFkb2JlLmNvbS94YXAvMS4wL21tLyIgeG1sbnM6c3RSZWY9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9zVHlwZS9SZXNvdXJjZVJlZiMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENTNiAoTWFjaW50b3NoKSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpBRDhBNjM1NzE0NkYxMUUyOUQ0OUE4Qzg0QTE2Njg5RCIgeG1wTU06RG9jdW1lbnRJRD0ieG1wLmRpZDpBRDhBNjM1ODE0NkYxMUUyOUQ0OUE4Qzg0QTE2Njg5RCI+IDx4bXBNTTpEZXJpdmVkRnJvbSBzdFJlZjppbnN0YW5jZUlEPSJ4bXAuaWlkOkFEOEE2MzU1MTQ2RjExRTI5RDQ5QThDODRBMTY2ODlEIiBzdFJlZjpkb2N1bWVudElEPSJ4bXAuZGlkOkFEOEE2MzU2MTQ2RjExRTI5RDQ5QThDODRBMTY2ODlEIi8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+1yxnwgAALRVJREFUeNrsXQeY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';
  static logoImgType = 'PNG';

  name: string;
  reportHeader?: ReportPart;
  pageHeader?: ReportPart;
  pageContent?: ReportPart[];
  pageFooter?: ReportPart;
  reportFooter?: ReportPart;
  totalPages: number;

  constructor( name: string, reportHeader?: ReportPart, pageHeader?: ReportPart,
               pageContent?: ReportPart[], pageFooter?: ReportPart, reportFooter?: ReportPart ) {
    super();
    this.name = name;
    this.reportHeader = reportHeader;
    this.pageHeader = pageHeader;
    this.pageContent = pageContent;
    this.pageFooter = pageFooter;
    this.reportFooter = reportFooter;
    this.totalPages = 1;
  }

  splitIntoPages( pageHeight: number ): ReportPage[] {
    let remainingHeight = pageHeight,
      contentParts: ReportPart[] = [];
    const pages: ReportPage[] = [];

    if (this.pageHeader) {
      remainingHeight -= this.pageHeader.height;
    }
    if (this.pageFooter) {
      remainingHeight -= this.pageFooter.height;
    }
    const resetRemainingHeight = remainingHeight;

    if (this.reportHeader && pages.length === 0) {
      remainingHeight -= this.reportHeader.height;
    }

    if (this.pageContent && this.pageContent.length > 0) {
      this.pageContent.forEach( ( content: ReportPart ) => {
        if (remainingHeight - content.height < 0) {
          pages.push( new ReportPage( contentParts ) );
          remainingHeight = resetRemainingHeight;
          contentParts = [];
        }
        remainingHeight -= content.height;
        contentParts.push( content );
      } );
      pages.push( new ReportPage( contentParts ) );
      if (this.reportFooter && remainingHeight - this.reportFooter.height < 0) {
        pages.push( new ReportPage() );
      }
    }
    return pages;
  }

  generate( doc: jsPDF, startPageNo: number = 1 ): jsPDF {
    let currPageNo = startPageNo;
    let offsetY = 10;
    const pages: ReportPage[] = this.splitIntoPages( doc.internal.pageSize.height );
    this.totalPages = pages.length;
    if (startPageNo > 1) {
      doc.addPage();
    }
    if (this.reportHeader && currPageNo === startPageNo) {
      doc = this.reportHeader.render( doc, offsetY, currPageNo );
      offsetY += this.reportHeader.height;
    }
    pages.forEach( ( reportPage: ReportPage ) => {
      if (currPageNo > startPageNo) {
        doc.addPage();
        offsetY = 10;
      }
      if (this.pageHeader) {
        doc = this.pageHeader.render( doc, offsetY, currPageNo );
        offsetY += this.pageHeader.height;
      }
      if (reportPage.contentParts.length > 0) {
        reportPage.contentParts.forEach( ( content: ReportPart ) => {
          doc = content.render( doc, offsetY, currPageNo );
          offsetY += content.height;
        } );
      }
      if (this.pageFooter) {
        doc = this.pageFooter.render( doc, offsetY, currPageNo );
      }
      currPageNo += 1;
    } );
    if (this.reportFooter) {
      doc = this.reportFooter.render( doc, offsetY, currPageNo );
    }
    return doc;
  }
}

class ReportPage {
  contentParts: ReportPart[];

  constructor( contentParts: ReportPart[] = [] ) {
    this.contentParts = contentParts;
  }
}
