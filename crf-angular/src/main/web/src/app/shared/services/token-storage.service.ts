import { Injectable } from '@angular/core';
import {CookieService} from "ngx-cookie-service";

@Injectable()

export class TokenStorageService {


  constructor(private cookieService: CookieService) {
  }

  tokenKey = 'crf-auth-token';

  logout(): void {
    this.cookieService.delete(this.tokenKey);
  }

  saveToken(token: string): void {

    if (this.cookieService.check(this.tokenKey)) {
      this.cookieService.delete(this.tokenKey);
    }

    this.cookieService.set(this.tokenKey, token)

  }

  getToken(): string {
    return this.cookieService.get(this.tokenKey);
  }

}
