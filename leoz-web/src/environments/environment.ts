// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

export const environment = {
  production: false,
  version: '20171130',
  apiUrl: 'https://leoz-t1.derkurier.de:13000/rs/api',          // testing system       for leoz.it-cobra.es
  // apiUrl: 'https://leoz-demo.derkurier.de:13000/rs/api',     // testing system       for leoz.it-cobra.es
  // apiUrl: 'https://leoz.derkurier.de:13000/rs/api',          // live system          for command.derkurier.de
  // apiUrl: 'http://localhost:13000/rs/api',                   // local system
  defLang: 'de',
  // use "leoz" for produktive environment.
  // "leo-old" will show the old menustructure for testingpurposes
  // "leo-old" may be used only on environments which are not reachable by unauthorized users
  defMenu: 'leo-old',
  devUser: 'dev@leoz',
  devPass: 'password',
  autologin: 'yes',
  pingURL: 'https://leoz-t1.derkurier.de:13000/rs/api/internal/v1/application/version'
};
