import * as jsPDF from 'jspdf';

export class ReportPart {

  height?: number;
  renderer?: ReportPartRendererFn;
  renderData?: any;

  constructor(height?: number, renderer?: ReportPartRendererFn, renderData?: any) {
    this.height = height || null;
    this.renderer = renderer || null;
    this.renderData = renderData || null;
  }

  render(doc: jsPDF, offsetY: number, currPageNo: number): jsPDF {
    return this.renderer && this.renderData ? this.renderer(doc, offsetY, currPageNo, this.renderData) : doc;
  }
}

type ReportPartRendererFn = (doc: jsPDF, offsetY: number, currPageNo: number, data: any) => jsPDF;
