import {Routes} from "@angular/router";
import {LoginComponent} from "./components/login/login.component";
import {AuthorizationPageComponent} from "./components/authorization-page/authorization-page.component";
import {ForgotPasswordComponent} from "./components/forgot-password/forgot-password.component";
import {RegistrationAdminComponent} from "./components/registration-admin/registration-admin.component";

export const AuthorizationRoutes: Routes = [
    {
        path: '',
        component: AuthorizationPageComponent,
        children: [
            {
            path: 'login',
            component: LoginComponent
            },
            {
            path: 'forgotPass',
            component: ForgotPasswordComponent
            },
            {
            path: 'registrationAdmin',
            component: RegistrationAdminComponent
            }
        ]
    },
]
