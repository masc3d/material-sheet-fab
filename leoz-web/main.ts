import { app, BrowserWindow, screen, ipcMain } from 'electron';
import * as fs from 'fs';
import * as PDFWindow from 'electron-pdf-window';

let win, serve, wc;
const args = process.argv.slice( 1 );
serve = args.some( val => val === '--serve' );

if (serve) {
  require( 'electron-reload' )( __dirname, {
    electron: require( `${__dirname}/../node_modules/electron` )
  } );
}

function createWindow() {

  const size = screen.getPrimaryDisplay().workAreaSize;

  // Create the browser window.
  win = new BrowserWindow( {
    x: 0,
    y: 0,
    width: size.width,
    height: size.height
  } );

  // and load the index.html of the app.
  win.loadURL( 'file://' + __dirname + '/index.html' );
  wc = win.webContents;

  // Open the DevTools.
  if (serve) {
    wc.openDevTools();
  }

  // Emitted when the window is closed.
  win.on( 'closed', () => {
    // pdfPreviewWindow.close();
    // pdfPreviewWindow = null;
    win = null;
  } );
}
// initialization and is ready to create browser windows.

// Some APIs can only be used after this event occurs.
app.on( 'ready', createWindow );

// Quit when all windows are closed.
app.on( 'window-all-closed', () => {
  // On OS X it is common for applications and their menu bar
  // to stay active until the user quits explicitly with Cmd + Q
  if (process.platform !== 'darwin') {
    app.quit();
  }
} );

app.on( 'activate', () => {
  // On OS X it's common to re-create a window in the app when the
  // dock icon is clicked and there are no other windows open.
  if (win === null) {
    createWindow();
  }
} );

ipcMain.on('printer-list', (e) => {
  const sender = e.sender;
  console.log('printerlist');
  sender.send('printer-list', wc.getPrinters());
});

ipcMain.on('preview-pdf', (e, baseURIstring) => {
  const base64Data = baseURIstring.replace(/^data:application\/pdf;base64,/, '');
  const tmpPdfPath = process.cwd() + '/tmp.pdf';
  fs.writeFile(tmpPdfPath, base64Data, 'base64', (err) => {
    if (err) {
      console.log('an error occured...');
    } else {
      console.log('file saved...');
      let pdfPreviewWindow = new PDFWindow({
        width: 800,
        height: 600
      });
      pdfPreviewWindow.loadURL(tmpPdfPath);
      pdfPreviewWindow.on( 'closed', () => {
        pdfPreviewWindow = null;
      } );
    }
  });
});
