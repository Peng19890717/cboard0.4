/**
 * Created by hanxinliu on 2017/04/26.
 */


cBoard.controller('eventCtrl', function ($scope, $http, dataService, $uibModal, ModalUtils, $filter, chartService, $timeout) {


    getRouteChart("funnelChart");
    //时间插件
    $scope.date = {
        startDate: moment().subtract(1, "days"),
        endDate: moment().subtract(1, "days"),
    };
    $scope.date2 = {
        startDate: moment().subtract(1, "days"),
        endDate: moment()
    };

    $scope.opts = {
        locale: {
            applyClass: 'btn-green',
            applyLabel: "确定",
            fromLabel: "From",
            toLabel: "To",
            cancelLabel: '取消',
            customRangeLabel: '自定义日期范围',
            daysOfWeek: ["日","一","二","三","四","五","六"],
            firstDay: 1,
            monthNames: ["一月","二月","三月","四月","五月","六月","7月","八月","九月","十月","十一月","十二月"]
        },
        ranges: {
            '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
            '今天': [moment().subtract(0, 'days'), moment().subtract(0, 'days')],
            '过去7天': [moment().subtract(7, 'days'), moment().subtract(1, 'days')],
            // '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
            '过去30天': [moment().subtract(30, 'days'), moment().subtract(1, 'days')],
            // '过去60天': [moment().subtract(60, 'days'), moment().subtract(1, 'days')]
        }
    };

    $scope.setStartDate = function () {
        $scope.date.startDate = moment().subtract(4, "days");
    };

    $scope.setRange = function () {
        $scope.date = {
            startDate: moment().subtract(5, "days"),
            endDate: moment()
        };
    };


});