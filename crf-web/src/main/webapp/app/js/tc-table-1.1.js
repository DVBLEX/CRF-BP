/*
 * Telclic Table for AngularJS
 * Version 1.1
 * JS
 */
function TCTable(getDataFunc) {
    this.currentPage = 1;
    this.lastPage = 1;
    this.pageRecordCount = 20;
    this.data = new Array();
    this.count = 0;
    this.getData = getDataFunc;
    this.showingData = 0;
    this.pageCount = 0;
    this.selectionLocked = false;
}

TCTable.prototype.SOURCE_NEXT_PAGE = 2;
TCTable.prototype.SOURCE_PREVIOUS_PAGE = 3;
TCTable.prototype.SOURCE_LAST_PAGE = 4;
TCTable.prototype.SOURCE_FIRST_PAGE = 5;
TCTable.prototype.SELECTED_ROW_CLASS = "tcTRowSelected";
TCTable.prototype.REF_BUTTON_FIRST = '.tc-table-pager #tcTablePagerButtonFirst';
TCTable.prototype.REF_BUTTON_PREVIOUS = '.tc-table-pager #tcTablePagerButtonPrevious';
TCTable.prototype.REF_BUTTON_NEXT = '.tc-table-pager #tcTablePagerButtonNext';
TCTable.prototype.REF_BUTTON_LAST = '.tc-table-pager #tcTablePagerButtonLast';
TCTable.prototype.REF_INPUT_CURRENTPAGE = '.tc-table-pager #tcTablePagerCurrentPage';

TCTable.prototype.reloadTable = function() {
    this.currentPage = 1;
    this.gotoCurrentPage();
};

TCTable.prototype.getCount = function() {
    return this.count;
};

TCTable.prototype.setCount = function(c) {
    this.count = c;
};

TCTable.prototype.getLastPage = function() {
    return this.lastPage;
};

TCTable.prototype.setLastPage = function(lp) {
    this.lastPage = lp;
};

TCTable.prototype.gotoCurrentPage = function() {
    this.getData(this.currentPage, this.pageRecordCount);
};

TCTable.prototype.updateControls = function() {
    $(this.REF_BUTTON_FIRST).attr("disabled", this.count <= 0 || this.currentPage <= 1);
    $(this.REF_BUTTON_PREVIOUS).attr("disabled", this.count <= 0 || this.currentPage <= 1);
    $(this.REF_BUTTON_NEXT).attr("disabled", this.count <= 0 || this.currentPage >= this.pageCount);
    $(this.REF_BUTTON_LAST).attr("disabled", this.count <= 0 || this.currentPage >= this.pageCount);
    $(this.REF_INPUT_CURRENTPAGE).attr("disabled", this.count <= 0);
};

TCTable.prototype.selectRow = function(event) {
    if (!this.selectionLocked) {
        if ($(event.currentTarget).hasClass(this.SELECTED_ROW_CLASS)) {
            $(event.currentTarget).removeClass(this.SELECTED_ROW_CLASS);
        } else {
            var t = this;
            $(".tc-table ." + this.SELECTED_ROW_CLASS).each(function() {
                $(this).removeClass(t.SELECTED_ROW_CLASS);
            });
            $(event.currentTarget).addClass(this.SELECTED_ROW_CLASS);
        }
    }
}

TCTable.prototype.getCurrentPage = function() {
    return this.currentPage;
};

TCTable.prototype.setCurrentPage = function(cp) {
    this.currentPage = cp;
};

TCTable.prototype.getPageRecordCount = function() {
    return this.pageRecordCount;
};

TCTable.prototype.setPageRecordCount = function(prc) {
    this.pageRecordCount = prc;
};

TCTable.prototype.getTableData = function() {
    return this.data;
};

TCTable.prototype.setData = function(response) {
    
    /*
     * Process pagination information
     */
    var page = response.page;
    this.setCount(page.totalElements);
    this.setLastPage(page.totalPages);
    this.pageCount = page.totalPages;
    
    /*
     * Process the data from the response
     */
    this.data = response.dataList;
    this.showingData = this.data.length;
    this.unlockSelection();
    this.updateControls();
};

TCTable.prototype.removeAllData = function() {
    this.setData({
        page: {
            totalElements : 0,
            totalPages : 0
        },
        dataList : []
    });
};

TCTable.prototype.getPageCount = function() {
    return this.pageCount;
};

TCTable.prototype.isSelectionLocked = function() {
    return this.selectionLocked;
}

TCTable.prototype.setSelectionLocked = function(isLocked) {
    this.selectionLocked = isLocked;
}

TCTable.prototype.lockSelection = function() {
    this.selectionLocked = true;
}

TCTable.prototype.unlockSelection = function() {
    this.selectionLocked = false;
}

crfApp.directive("tcTablePagination", function() {
    return {
        restrict : 'E',
        templateUrl : 'app/views/tcTablePager.html',
        replace : true,
        require : 'ngModel',
        require : '^?form',
        link : function($scope, element, attrs) {
            $scope.tcTableChangePage = function(sourceId) {

                var currentPage = $scope.tcTable.getCurrentPage();
                if (currentPage != undefined && currentPage !== "") {

                    currentPage += "";
                    if (currentPage.match(/^\d+$/)) {

                        if (sourceId === $scope.tcTable.SOURCE_NEXT_PAGE) {
                            currentPage++;
                        } else if (sourceId === $scope.tcTable.SOURCE_PREVIOUS_PAGE) {
                            currentPage--;
                        } else if (sourceId === $scope.tcTable.SOURCE_LAST_PAGE) {
                            currentPage = $scope.tcTable.getPageCount();
                        } else if (sourceId === $scope.tcTable.SOURCE_FIRST_PAGE) {
                            currentPage = 1;
                        }

                        if (currentPage > $scope.tcTable.getPageCount()) {
                            currentPage = $scope.tcTable.getPageCount();
                        } else if (currentPage < 1) {
                            currentPage = 1;
                        }

                    } else {
                        currentPage = 1;
                    }

                } else {
                    currentPage = 1;
                }

                $scope.tcTable.setCurrentPage(currentPage);
                $scope.tcTable.gotoCurrentPage();
            }
        }
    }
});

crfApp.directive('convertToNumber', function() {
    return {
        require : 'ngModel',
        link : function(scope, element, attrs, ngModel) {
            ngModel.$parsers.push(function(val) {
                return val != null ? parseInt(val, 10) : null;
            });
            ngModel.$formatters.push(function(val) {
                return val != null ? '' + val : null;
            });
        }
    };
});
