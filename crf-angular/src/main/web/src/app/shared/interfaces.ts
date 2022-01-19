export interface User {
    username: string
    password: string
}

export interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
    badge?: string;
    badgeClass?: string;
    isExternalLink: boolean;
    submenu: RouteInfo[];
}

export interface PageParameters {
    page: number,
    size: number
}

export interface UserInfo {
    dateCreatedString: string;
    email: string;
    firstName: string;
    lastName: string;
    username: string;
}

export interface Page {
    totalElements: number;
    totalPages: number;
}

export interface ResponseData {
    dataList: any[];
    page: Page;
    responseCode: number;
    responseDate: string;
    responseText: string;
}
