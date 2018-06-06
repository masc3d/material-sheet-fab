# Get up and running

## Installation

### NodeJS

##### OSX

* Install [homebrew](https://brew.sh)

```
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```

* Install NodeJS

```
brew install nodejs
```

##### Windows

* Install [NodeJS](https://nodejs.org/en/download/current/) 

### Angular CLI

```
npm install -g @angular/cli@latest
```

### Project dependencies

* Change directory to web project `leoz-web`

```
npm install
```

## Configuration

For customizing REST host among others

`src/environments/*.ts`

## Build and run web application

For localhost only

```
ng serve
```

For public access

```
ng serve --host <host> --port <port>
```

# Default README

This project was generated with [Angular CLI](https://github.com/angular/angular-cli) version 1.0.1.

## Development server

Run `ng serve` for a dev server. Navigate to `http://localhost:4200/`. The app will automatically reload if you change any of the source files.

## Code scaffolding

Run `ng generate component component-name` to generate a new component. You can also use `ng generate directive|pipe|service|class|module`.

## Build

Run `ng build` to build the project. The build artifacts will be stored in the `dist/` directory. Use the `-prod` flag for a production build.

## Running unit tests

Run `ng test` to execute the unit tests via [Karma](https://karma-runner.github.io).

## Running end-to-end tests

Run `ng e2e` to execute the end-to-end tests via [Protractor](http://www.protractortest.org/).
Before running the tests make sure you are serving the app via `ng serve`.

## Further help

To get more help on the Angular CLI use `ng help` or go check out the [Angular CLI README](https://github.com/angular/angular-cli/blob/master/README.md).

## used:
- npm install --save ngx-bootstrap // git clone https://github.com/valor-software/ngx-bootstrap.git
  => only external dependency moment.js (npm moment) https://github.com/moment/moment/
- npm install --save @yaga/leaflet-ng2 // git clone https://github.com/yagajs/leaflet-ng2.git

for tests:
- npm install --save-dev karma-phantomjs-launcher

export CHROME_BIN=/usr/bin/chromium-browser
