import {Component, OnInit} from '@angular/core';
import {map} from "rxjs/operators";
import {Observable} from "rxjs";
import {GridService} from "../../../shared/services/grid.service";
import {NgbModal} from "@ng-bootstrap/ng-bootstrap";
import {UserInfo} from "../../../shared/interfaces";

@Component({
  selector: 'app-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.scss',]
})
export class UsersComponent implements OnInit{

  getUserList$: Observable<any>;
  users$: Observable<UserInfo>;

  pageParam = {
    page: 1,
    size: 10
  }

  constructor(
      private gridService: GridService,
      private modalService: NgbModal
  ) {

  }

  ngOnInit() {
    this.getUsers();
  }

  paginationChange() {
    this.getUsers();
  }

  selectChange() {
    this.getUsers();
  }

  getUsers() {
    this.getUserList$ = this.gridService.getList(
        {
          page: this.pageParam.page - 1,
          size: +this.pageParam.size
        },
        '/admins/list'
        );
    this.users$ = this.getUserList$.pipe(map( (response) => response.dataList))
  }

  getCollectionSize() {
    return this.gridService.getCollectionSize()
  }

  getIsLoading() {
    return this.gridService.getIsLoading();
  }

  openModal(modal) {
    this.modalService.open(modal, { centered: true });
  }

}
