import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute, Router} from "@angular/router";
import {NgxSpinnerService} from "ngx-spinner";
import {AuthorizationService} from "../../../services/authorization.service";
import {User} from "../../../interfaces";
import {HttpResponse} from "@angular/common/http";
import {CookieService} from "ngx-cookie-service";
import {TokenStorageService} from "../../../services/token-storage.service";

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {

  loginFormSubmitted = false;
  isLoginFailed = false;

  loginForm = new FormGroup({
    username: new FormControl('andrii.fedorchuk+admin@telclic.net', [Validators.required]),
    password: new FormControl('CrowdFund2021!', [Validators.required]),
    rememberMe: new FormControl(true)
  });


  constructor(private router: Router,
              private spinner: NgxSpinnerService,
              private route: ActivatedRoute,
              private tokenStorageService: TokenStorageService,
              private authAdminService: AuthorizationService) {
  }

  get lf() {
    return this.loginForm.controls;
  }

  ngOnInit() {
  }

    // On submit button click
  onSubmit() {

    let token = '';

    const user: User = {
      username: this.loginForm.value.username,
      password: this.loginForm.value.password
    }

    console.log(user);

    this.authAdminService.login(user)
        .subscribe(
            (response: HttpResponse<any>) => {

              token = response.headers.get('Authorization');
              this.tokenStorageService.saveToken(token);
              this.router.navigate(['/admin']);

            },
            error => {
              console.log('Error: ', error)
            });
  }

}
