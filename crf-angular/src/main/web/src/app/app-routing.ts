import {Routes} from "@angular/router";

export const appRoutes: Routes = [
    {
        path: '',
        redirectTo: 'authorization/login',
        pathMatch: 'full'
    },
    {
        path: 'authorization',
        loadChildren: () => import('./shared/Authorization/Authorization.module').then((m) => m.AuthorizationModule)
    },
    {
        path: 'admin',
        loadChildren: () => import('./admin/admin.module').then((m) => m.AdminModule)
    }

]
