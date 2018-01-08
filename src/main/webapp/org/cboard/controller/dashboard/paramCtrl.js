/**
 * Created by yfyuan on 2017/3/28.
 */
'user strict';
cBoard.controller('paramCtrl', function ($scope, $uibModal, $http) {

    var evalValue = function (value) {
        if (isNaN(Number(value))) {
            var now = function (i, key) {
                if (i == undefined) {
                    return +moment();
                }
                return +moment().add(i, key);
            };
            var interval = function (i, key) {
                if (i == undefined) {
                    i == 1
                }
                var y = 0;
                switch (key) {
                    case 'h':
                        y = 60 * 60 * 1000;
                        break;
                    case 'd':
                        y = 24 * 60 * 60 * 1000;
                        break;
                    case 'm':
                        y = 60 * 1000;
                        break;
                    case 's':
                        y = 1000;
                        break;
                }
                return i * y;
            };
            return eval(value);
        } else {
            return value;
        }
    };

    var formatter = function (d, f) {
        if (_.isUndefined(f)) {
            return d;
        } else {
            return moment(d).format(f);
        }
    };

    $scope.init = function () {
        $scope.param = $scope.$parent.param;
        $scope.param.selects = [];
        $scope.param.type = '=';
        $scope.param.values = [];
        if ($scope.param.paramType == 'slider') {
            var cfg = $scope.param.cfg;
            var _max = evalValue(_.result(cfg, 'max', null));
            var _min = evalValue(_.result(cfg, 'min', null));
            var apply = _.debounce($scope.$parent.applyParamFilter, 800);
            $scope.slider = {
                minValue: _max - Number(evalValue(_.result(cfg, 'range', 0))),
                maxValue: _max,
                options: {
                    floor: _min,
                    ceil: _max,
                    draggableRange: true,
                    enforceStep: false,
                    maxRange: Number(evalValue(_.result(cfg, 'maxRange', null))),
                    step: evalValue(_.result(cfg, 'step', 1 * 60 * 1000)),
                    translate: function (value) {
                        return formatter(value, cfg.formatter);
                    },
                    onChange: function (sliderId, modelValue, highValue, pointerType) {
                        $scope.param.type = '[a,b]';
                        $scope.param.values = [formatter(modelValue, cfg.value_fmt), formatter(highValue, cfg.value_fmt)];
                        apply();
                    }
                }
            };
            $scope.param.type = '[a,b]';
            $scope.param.values = [formatter($scope.slider.minValue, cfg.value_fmt), formatter($scope.slider.maxValue, cfg.value_fmt)];
            $scope.param.refresh = function () {
                if ($scope.slider.maxValue == $scope.slider.options.ceil) {
                    var _range = $scope.slider.maxValue - $scope.slider.minValue;
                    var cfg = $scope.param.cfg;
                    var max = evalValue(_.result(cfg, 'max', null));
                    var min = evalValue(_.result(cfg, 'min', null));
                    $scope.slider.maxValue = max;
                    $scope.slider.minValue = max - _range;
                    $scope.slider.options.floor = min;
                    $scope.slider.options.ceil = max;
                    $scope.param.type = '[a,b]';
                    $scope.param.values = [formatter($scope.slider.minValue, cfg.value_fmt), formatter($scope.slider.maxValue, cfg.value_fmt)];
                }
            }
        } else {
            _.each($scope.param.col, function (c) {
                var p;
                if (_.isUndefined(c.datasetId)) {
                    _.each($scope.board.layout.rows, function (row) {
                        _.each(row.widgets, function (widget) {
                            if (widget.widget.id == c.widgetId) {
                                p = {
                                    datasourceId: widget.widget.data.datasource,
                                    query: angular.toJson(widget.widget.data.query),
                                    datasetId: null
                                };
                            }
                        });
                    });
                } else {
                    p = {datasourceId: null, query: null, datasetId: c.datasetId};
                }
                $http.post("dashboard/getDimensionValues.do", {
                    datasourceId: p.datasourceId,
                    query: p.query,
                    datasetId: p.datasetId,
                    colmunName: c.column
                }).success(function (response) {
                    _.each(response, function (s) {
                        if (_.indexOf($scope.param.selects, s) < 0) {
                            $scope.param.selects.push(s);
                        }
                    });

                });
            });
        }
        $scope.$emit('paramInitFinish', $scope.param);
    };

    $scope.editParamDate = function () {
        $uibModal.open({
            templateUrl: 'org/cboard/view/dashboard/modal/paramDate.html',
            //windowTemplateUrl: 'org/cboard/view/util/modal/window.html',
            backdrop: false,
            size: 'lg',
            resolve: {
                param: function () {
                    if ($scope.param) {
                        return angular.copy($scope.param);
                    } else {
                        return {type: '='}
                    }
                },
                filter: function () {
                    return false;
                },
                getSelects: function () {
                    return function (byFilter, column, callback) {
                        callback($scope.param.selects);
                    };
                },
                ok: function () {
                    return function (param) {
                        $scope.param.values = param.values;
                        $scope.param.type = param.type;
                        $scope.applyParamFilter();
                    }
                }
            },
            controller: 'paramSelector'
        });
    };
    $scope.editParam = function () {
        $uibModal.open({
            templateUrl: 'org/cboard/view/dashboard/modal/param.html',
            windowTemplateUrl: 'org/cboard/view/util/modal/window.html',
            backdrop: false,
            size: 'lg',
            resolve: {
                param: function () {
                    if ($scope.param) {
                        return angular.copy($scope.param);
                    } else {
                        return {type: '='}
                    }
                },
                filter: function () {
                    return false;
                },
                getSelects: function () {
                    return function (byFilter, column, callback) {
                        callback($scope.param.selects);
                    };
                },
                ok: function () {
                    return function (param) {
                       // debugger;
                        $scope.param.values = param.values;
                        $scope.param.type = param.type;
                        $scope.applyParamFilter();
                    }
                }
            },
            controller: 'paramSelector'
        });
    };



//时间控件

//getRouteChart("funnelChart");
//时间插件
$scope.date={startDate: null, endDate: null};
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
    },
    eventHandlers:{
        'apply.daterangepicker':function (ev,picker) {
            var timeArr=$("input.date-picker[flag='"+$scope.param.$$hashKey+"']").eq(0).val().split(" - ");
            if($scope.param.selects[0].indexOf("-")>=0)
            $scope.param.values =timeArr;
            else{
                if(timeArr.length==2){
                    $scope.param.values=[timeArr[0].replace(/-/g,""),timeArr[1].replace(/-/g,"")]
                }
            }
            $scope.param.type ='[a,b]';
            $scope.applyParamFilter();
        },
        'show.daterangepicker':function () {
            $scope.maxDate=moment($scope.param.selects[$scope.param.selects.length-1]).add('days',1).format('YYYY-MM-DD');
        }
    }
};
});