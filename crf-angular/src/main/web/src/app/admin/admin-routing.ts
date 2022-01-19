import {Routes} from "@angular/router";
import {AdminPageComponent} from "./components/admin-page/admin-page.component";
import {UsersComponent} from "./components/users/users.component";
import {HomeComponent} from "./components/home/home.component";

export const AdminRoutes: Routes = [
    {
        path: '',
        component: AdminPageComponent,
        children: [
            { path: 'users', component: UsersComponent },
            { path: 'home', component: HomeComponent }
        ]
    },
]
