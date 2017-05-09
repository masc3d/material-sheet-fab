import { Component, OnInit } from '@angular/core';
import { Response } from '@angular/http';
import { ErrormsgService } from '../error/errormsg.service';

@Component({
  selector: 'app-footer',
  templateUrl: './app-footer.component.html',
  providers: [ErrormsgService]
})
export class AppFooterComponent implements OnInit {
  latestError: Response;

  constructor(private errormsgService: ErrormsgService){}

  ngOnInit() {
    this.errormsgService.latestError.subscribe((latestErrorResponse) => this.latestError = latestErrorResponse);
    console.log(this.latestError);
  }
}
