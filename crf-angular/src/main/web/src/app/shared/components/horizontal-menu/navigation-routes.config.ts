import {RouteInfo} from "../../interfaces";

export const HROUTES: RouteInfo[] = [

  {
    path: '/admin/home', title: 'Home', icon: 'ft-home', class: 'dropdown nav-item has-sub', isExternalLink: false,
    submenu: []
  },
  {
    path: '/admin/users', title: 'Users', icon: 'ft-users', class: 'dropdown nav-item has-sub', isExternalLink: false,
    submenu: []
  },
  {
    path: '', title: 'Customers', icon: 'ft-briefcase', class: 'dropdown nav-item has-sub', isExternalLink: false,
    submenu: []
  },
  {
    path: '', title: 'Deposits', icon: 'ft-dollar-sign', class: 'dropdown nav-item has-sub', isExternalLink: false,
    submenu: []
  },
  {
    path: '', title: 'Products', icon: 'ft-clipboard', class: 'dropdown nav-item has-sub', isExternalLink: false,
    submenu: []
  },
  {
    path: '', title: 'Payments', icon: 'ft-credit-card', class: 'dropdown nav-item has-sub', isExternalLink: false,
    submenu: []
  }
];
