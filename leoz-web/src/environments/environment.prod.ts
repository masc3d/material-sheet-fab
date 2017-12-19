// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: true,
  version: '20171220', // on change, always adapt assets/version.json
  // --build-optimizer true
  // testing system      apiUrl for leoz.it-cobra.es
  // apiUrl: 'https://leoz-t1.derkurier.de:13000/rs/api',
  // local system        apiUrl
  // apiUrl: 'http://localhost:13000/rs/api',
  // testing system      apiUrl for command-demo.derkurier.de ==> (leoz-demo.derkurier.de:13000)
  apiUrl: 'https://leoz-demo.derkurier.de:13000/rs/api',
  // live system         apiUrl for command.derkurier.de      ==> (leoz.derkurier.de:13000)
  //  apiUrl: 'https://leoz.derkurier.de:13000/rs/api',
  defMenu: 'leoz',
  // use "leoz" for produktive environment.
  // "leo-old" will show the old menustructure for testingpurposes
  // "leo-old" may be used only on environments which are not reachable by unauthorized users
  defLang: 'de',
  devUser: '',
  devPass: '',
  autologin: 'no',

  // pingURL: 'https://leoz-t1.derkurier.de:13000/rs/api/internal/v1/application/version'   // for leoz.it-cobra.es
  pingURL: 'https://leoz-demo.derkurier.de:13000/rs/api/internal/v1/application/version' // for command-demo.derkurier.de
  // pingURL: 'https://leoz.derkurier.de:13000/rs/api/internal/v1/application/version'      // for command.derkurier.de
};
