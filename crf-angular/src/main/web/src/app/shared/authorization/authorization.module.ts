import {NgModule} from '@angular/core';
import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {CommonModule} from '@angular/common';
import {RouterModule} from "@angular/router";
import {NgxSpinnerModule} from "ngx-spinner";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";

import {LoginComponent} from './components/login/login.component';
import {AuthorizationRoutes} from "./authorization-routing";
import {AuthorizationPageComponent} from './components/authorization-page/authorization-page.component';
import {ForgotPasswordComponent} from './components/forgot-password/forgot-password.component';
import { RegistrationAdminComponent } from './components/registration-admin/registration-admin.component';
import {SharedModule} from "../shared.module";


@NgModule({
    declarations: [
        LoginComponent,
        AuthorizationPageComponent,
        ForgotPasswordComponent,
        RegistrationAdminComponent
    ],
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        NgbModule,
        NgxSpinnerModule,
        SharedModule,
        RouterModule.forChild(AuthorizationRoutes)
    ],
    providers: []
})
export class AuthorizationModule {
}
