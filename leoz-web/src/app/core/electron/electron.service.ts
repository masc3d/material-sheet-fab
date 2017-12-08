import { Injectable } from '@angular/core';

import { ipcRenderer, PrinterInfo } from 'electron';
import * as fs from 'fs';
import { Subject } from 'rxjs/Subject';
import { BehaviorSubject } from 'rxjs/BehaviorSubject';

@Injectable()
export class ElectronService {

  public ipc: typeof ipcRenderer;
  public fs: typeof fs;

  private printersSubject = new BehaviorSubject<PrinterInfo[]>([]);
  public printers$ = this.printersSubject.asObservable().distinctUntilChanged();

  constructor() {
    if (this.isElectron()) {
      this.ipc = window.require( 'electron' ).ipcRenderer;
      this.fs = window.require( 'fs' );

      this.ipc.on( 'printer-list', ( e, printers: PrinterInfo[] ) => {
        this.printersSubject.next( printers );
      } );
    }
  }

  public isElectron() {
    return window && window.process && window.process.type;
  };

  public getPrinters() {
    this.ipc.send( 'printer-list' );
  }

  previewPDF( baseURIstring: string ) {
    this.ipc.send( 'preview-pdf', baseURIstring );
  }
}
