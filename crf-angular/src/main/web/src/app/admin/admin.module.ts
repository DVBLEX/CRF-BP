import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AdminPageComponent} from './components/admin-page/admin-page.component';
import {RouterModule} from "@angular/router";
import {AdminRoutes} from "./admin-routing";
import {SharedModule} from "../shared/shared.module";
import { UsersComponent } from './components/users/users.component';
import {NgbModule} from "@ng-bootstrap/ng-bootstrap";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {PipeModule} from "../shared/pipes/pipe.module";
import { HomeComponent } from './components/home/home.component';


@NgModule({
    declarations: [
        AdminPageComponent,
        UsersComponent,
        HomeComponent
    ],
    imports: [
        CommonModule,
        PipeModule,
        NgbModule,
        SharedModule,
        FormsModule,
        ReactiveFormsModule,
        RouterModule.forChild(AdminRoutes)
    ],
    providers: [
    ]
})
export class AdminModule {
}
