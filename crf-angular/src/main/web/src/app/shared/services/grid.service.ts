import { Injectable } from '@angular/core';
import {PageParameters, ResponseData} from "../interfaces";
import {Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {tap} from "rxjs/operators";

@Injectable()

export class GridService {

  private collectionSize: number;
  private isLoading = false;

  constructor(private http: HttpClient) { }

  getList(pageParameters: PageParameters, url: string): Observable<ResponseData> {
    this.isLoading = true ;
    return this.http.get<ResponseData>(`http://localhost:8080/crf-rest${url}?page=${pageParameters.page}&size=${pageParameters.size}`)
        .pipe(
            tap((response) => this.collectionSize = response.page.totalElements),
            tap(() => this.isLoading = false)
        )
  }

  getCollectionSize(): number {
    return this.collectionSize ;
  }

  getIsLoading(): boolean {
    return this.isLoading ;
  }
}
