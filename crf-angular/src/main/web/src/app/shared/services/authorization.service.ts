import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {PageParameters, User} from "../interfaces";
import {Observable} from "rxjs";

@Injectable()

export class AuthorizationService {

    constructor(private http: HttpClient) {}

    login(user: User): Observable<any> {
        return this.http.post('http://localhost:8080/crf-rest/login', user, {observe: 'response' as 'body'})
    }

}
