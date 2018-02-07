/**
 * Created by yfyuan on 2016/8/12.
 */
'use strict';
cBoard.service('dataService', function ($http, $q, updateService) {
    //var datasetList;
    var getDatasetList = function () {
        var deferred = $q.defer();
        if (typeof(datasetList)!="undefined"&&datasetList!='') {
            deferred.resolve(angular.copy(datasetList));
            //console.log("datasetList="+datasetList);
        } else {
            $http.get("dashboard/getDatasetList.do?page=cboard/service/data/dataService.js").success(function (data) {
                deferred.resolve(data);
            });
        }
        return deferred.promise;
    };

    this.linkDataset = function (datasetId, chartConfig) {
        return linkDataset(datasetId, chartConfig);
    };
    var linkDataset = function (datasetId, chartConfig) {
        if (_.isUndefined(datasetId) || _.isUndefined(chartConfig)) {
            var deferred = $q.defer();
            deferred.resolve();
            return deferred.promise;
        } else {
            return getDatasetList().then(function (dsList) {
                var deferred = $q.defer();

                var dataset = _.find(dsList, function (e) {
                    return e.id == datasetId;
                });
                //link filter group
                _.each(chartConfig.filters, function (f) {
                    if (f.group) {
                        var group = _.find(dataset.data.filters, function (e) {
                            return e.id == f.id;
                        });
                        if (group) {
                            f.filters = group.filters;
                            f.group = group.group;
                        }
                    }
                });
                //link exp
                _.each(chartConfig.values, function (v) {
                    _.each(v.cols, function (c) {
                        if (c.type == 'exp') {
                            var exp = _.find(dataset.data.expressions, function (e) {
                                return c.id == e.id;
                            });
                            if (exp) {
                                c.exp = exp.exp;
                                c.alias = exp.alias;
                            }
                            // replace variable in exp
                            c.exp = replaceVariable(dataset.data.expressions, c);
                        }
                    });
                });
                function replaceVariable(expList, exp) {
                    var value = exp.exp;
                    var loopCnt = 0;
                    var context = {};
                    for (var i = 0; i < expList.length; i++) {
                        context[expList[i].alias] = expList[i].exp;
                    }
                    value = value.render2(context, function(v) {
                        return '(' + v + ')';
                    });
                    while (value.match(/\$\{.*?\}/g) != null) {
                        value = value.render2(context);
                        if (loopCnt++ > 10) {
                            throw 'Parser expresion [ ' + exp.exp + ' ] with error';
                        }
                    }
                    return value;
                }
                //link dimension
                var linkFunction = function (k) {
                    if (k.id) {
                        var _level;
                        var _dimension;
                        _.each(dataset.data.schema.dimension, function (e) {
                            if (e.type == 'level') {
                                _.each(e.columns, function (c) {
                                    if (c.id == k.id) {
                                        _dimension = c;
                                        _level = e;
                                    }
                                });
                            } else if (k.id == e.id) {
                                _dimension = e;
                            }
                        });
                        if (_dimension && _dimension.alias) {
                            k.alias = _dimension.alias;
                            if (_level) {
                                k.level = _level.alias;
                            }
                        }
                    }
                };
                _.each(chartConfig.keys, linkFunction);
                _.each(chartConfig.groups, linkFunction);
                deferred.resolve();
                return deferred.promise;
            });
        }
    };

    var getDimensionConfig = function (array) {
        var result = [];
        if (array) {
            _.each(array, function (e) {
                if (_.isUndefined(e.group)) {
                    result.push({columnName: e.col, filterType: e.type, values: e.values, id: e.id});
                } else {
                    _.each(e.filters, function (f) {
                        result.push({columnName: f.col, filterType: f.type, values: f.values});
                    });
                }
            });
        }
        return result;
    };

    this.getDimensionValues = function (datasource, query, datasetId, colmunName, chartConfig, callback) {
        chartConfig = angular.copy(chartConfig);
        linkDataset(datasetId, chartConfig).then(function () {
            var cfg = undefined;
            if (chartConfig) {
                cfg = {rows: [], columns: [], filters: []};
                cfg.rows = getDimensionConfig(chartConfig.keys);
                cfg.columns = getDimensionConfig(chartConfig.groups);
                cfg.filters = getDimensionConfig(chartConfig.filters);
            }

            $http.post("dashboard/getDimensionValues.do", {
                datasourceId: datasource,
                query: angular.toJson(query),
                datasetId: datasetId,
                colmunName: colmunName,
                cfg: angular.toJson(cfg),
            }).success(function (response) {
                callback(response);
            });
        });
    };

    this.getDataSeries = function (datasource, query, datasetId, chartConfig, callback, reload) {
        chartConfig = angular.copy(chartConfig);
        updateService.updateConfig(chartConfig);
        linkDataset(datasetId, chartConfig).then(function () {
            var dataSeries = getDataSeries(chartConfig);
            var cfg = {rows: [], columns: [], filters: []};
            cfg.rows = getDimensionConfig(chartConfig.keys);
            cfg.columns = getDimensionConfig(chartConfig.groups);
            cfg.filters = getDimensionConfig(chartConfig.filters);
            cfg.filters = cfg.filters.concat(getDimensionConfig(chartConfig.boardFilters));
            cfg.filters = cfg.filters.concat(getDimensionConfig(chartConfig.boardWidgetFilters));
            cfg.values = _.map(dataSeries, function (s) {
                return {column: s.name, aggType: s.aggregate};
            });
            if(cfg.filters.length>0){
                if(cfg.filters[0].columnName=="日期"){
                    if(cfg.rows.length>0)
                    cfg.rows[0].values=[];
                }
            }
            $http.post("dashboard/getAggregateData.do", {
                datasourceId: datasource,
                query: angular.toJson(query),
                datasetId: datasetId,
                cfg: angular.toJson(cfg),
                reload: reload
            }).success(function (data) {
                /**
                 * modify by wanghaihua
                 * @type {string}
                 */
                var result='';
                if(chartConfig.groups.length==0){
                    result=castRawData2Series2(data,chartConfig);
                }else
                    result = castRawData2Series(data, chartConfig);
                    result.chartConfig = chartConfig;
                    if (!_.isUndefined(datasetId)) {
                        getDrillConfig(datasetId, chartConfig).then(function (c) {
                        result.drill = {config: c};
                        callback(result);
                    });
                    } else {
                      callback(result);
                    };
            });
        });
    };

    this.getDrillPath = function (datasetId, id) {
        var deferred = $q.defer();
        getDatasetList().then(function (dsList) {
            var dataset = _.find(dsList, function (e) {
                return e.id == datasetId;
            });
            var path = [];
            var level;
            _.each(dataset.data.schema.dimension, function (_e) {
                if (_e.type == 'level') {
                    _.each(_e.columns, function (_c) {
                        if (_c.id == id) {
                            path = _e.columns;
                            level = _e;
                        }
                    });
                }
            });
            path = _.map(path, function (e) {
                return {
                    id: e.id,
                    alias: e.alias,
                    col: e.column,
                    level: level.alias,
                    type: '=',
                    values: [],
                    sort: 'asc'
                }
            });
            deferred.resolve(path);
        });
        return deferred.promise;
    };

    var getDrillConfig = function (datasetId, chartConfig) {
        var deferred = $q.defer();
        getDatasetList().then(function (dsList) {
                var drillConfig = {};
                var dataset = _.find(dsList, function (e) {
                    return e.id == datasetId;
                });
                if (!dataset.data.schema || dataset.data.schema.dimension.length == 0) {
                    deferred.resolve(drillConfig);
                    return deferred.promise;
                }
                var _f = function (array) {
                    _.each(array, function (c, i_array) {
                        var level;
                        var i_level;
                        _.find(dataset.data.schema.dimension, function (_e) {
                            if (_e.type == 'level') {
                                return _.find(_e.columns, function (_c, _i) {
                                    if (_c.id == c.id) {
                                        level = _e;
                                        i_level = _i;
                                        return true;
                                    }
                                });
                            }
                        });
                        if (!level) {
                            return;
                        }
                        var prevIsInLevel = false;
                        if (i_array > 0 && i_level > 0) {
                            prevIsInLevel = array[i_array - 1].id == level.columns[i_level - 1].id;
                        }
                        var prevDrilled = i_array > 0 && array[i_array - 1].values.length == 1 && array[i_array - 1].type == '=';
                        var nextIsInLevel = false;
                        if (i_array < array.length - 1 && i_level < level.columns.length - 1) {
                            nextIsInLevel = array[i_array + 1].id == level.columns[i_level + 1].id;
                        }
                        var isLastLevel = i_level == level.columns.length - 1;
                        var drillDownExistIdx = 0;
                        var drillDownExist = _.find(array, function (e, i) {
                            if (i_level < level.columns.length - 1 && level.columns[i_level + 1].id == e.id) {
                                drillDownExistIdx = i;
                                return true;
                            }
                        });
                        //if next level exist in array,the level must be the next of current
                        var drillDown = drillDownExist ? drillDownExistIdx == i_array + 1 : true;
                        var up = i_level > 0 && i_array > 0 && prevIsInLevel && (i_array == array.length - 1 || !nextIsInLevel) && prevDrilled;
                        var down = (nextIsInLevel || !isLastLevel) && drillDown && (!prevIsInLevel || (array[i_array - 1].type == '=' && array[i_array - 1].values.length == 1));
                        drillConfig[c.id] = {
                            up: up,
                            down: down
                        };
                    });
                };
                _f(chartConfig.keys);
                _f(chartConfig.groups);
                deferred.resolve(drillConfig);
            }
        );
        return deferred.promise;
    };

    this.viewQuery = function (params, callback) {
        params.config = angular.copy(params.config);
        updateService.updateConfig(params.config);
        linkDataset(params.datasetId, params.config).then(function () {
            var dataSeries = getDataSeries(params.config);
            var cfg = {rows: [], columns: [], filters: []};
            cfg.rows = getDimensionConfig(params.config.keys);
            cfg.columns = getDimensionConfig(params.config.groups);
            cfg.filters = getDimensionConfig(params.config.filters);
            cfg.filters = cfg.filters.concat(getDimensionConfig(params.config.boardFilters));
            cfg.values = _.map(dataSeries, function (s) {
                return {column: s.name, aggType: s.aggregate};
            });
            $http.post("dashboard/viewAggDataQuery.do", {
                datasourceId: params.datasource,
                query: angular.toJson(params.query),
                datasetId: params.datasetId,
                cfg: angular.toJson(cfg),
            }).success(function (response) {
                callback(response[0]);
            });
        });
    };

    this.getColumns = function (option) {
        $http.post("dashboard/getColumns.do", {
            datasourceId: option.datasource,
            query: option.query ? angular.toJson(option.query) : null,
            datasetId: option.datasetId,
            reload: option.reload
        }).success(function (response) {
            option.callback(response);
        });
    };

    var getDataSeries = function (chartConfig) {
        var result = [];
        _.each(chartConfig.values, function (v) {
            _.each(v.cols, function (c) {
                var series = configToDataSeries(c);
                _.each(series, function (s) {
                    if (!_.find(result, function (e) {
                            return JSON.stringify(e) == JSON.stringify(s);
                        })) {
                        result.push(s);
                    }
                });
            });
        });
        return result;
    };

    var configToDataSeries = function (config) {
        switch (config.type) {
            case 'exp':
                return getExpSeries(config.exp);
                break;
            default:
                return [{
                    name: config.col,
                    aggregate: config.aggregate_type
                }];
                break;
        }
    };

    var getExpSeries = function (exp) {
        return parserExp(exp).aggs;
    };

    var filter = function (cfg, iv) {
        switch (cfg.f_type) {
            case '=':
            case 'eq':
                for (var i = 0; i < cfg.f_values.length; i++) {
                    if (iv == cfg.f_values[i]) {
                        return true;
                    }
                }
                return cfg.f_values.length == 0;
            case '≠':
            case 'ne':
                for (var i = 0; i < cfg.f_values.length; i++) {
                    if (iv == cfg.f_values[i]) {
                        return false;
                    }
                }
                return true;
            case '>':
                var v = cfg.f_values[0];
                var params = toNumber(iv, v);
                if (!_.isUndefined(v) && params[0] <= params[1]) {
                    return false;
                }
                return true;
            case '<':
                var v = cfg.f_values[0];
                var params = toNumber(iv, v);
                if (!_.isUndefined(v) && params[0] >= params[1]) {
                    return false;
                }
                return true;
            case '≥':
                var v = cfg.f_values[0];
                var params = toNumber(iv, v);
                if (!_.isUndefined(v) && params[0] < params[1]) {
                    return false;
                }
                return true;
            case '≤':
                var v = cfg.f_values[0];
                var params = toNumber(iv, v);
                if (!_.isUndefined(v) && params[0] > params[1]) {
                    return false;
                }
                return true;
            case '(a,b]':
                var a = cfg.f_values[0];
                var b = cfg.f_values[1];
                var params = toNumber(iv, a, b);
                if (!_.isUndefined(a) && !_.isUndefined(b) && (params[0] <= params[1] || params[0] > params[2])) {
                    return false;
                }
                return true;
            case '[a,b)':
                var a = cfg.f_values[0];
                var b = cfg.f_values[1];
                var params = toNumber(iv, a, b);
                if (!_.isUndefined(a) && !_.isUndefined(b) && (params[0] < params[1] || params[0] >= params[2])) {
                    return false;
                }
                return true;
            case '(a,b)':
                var a = cfg.f_values[0];
                var b = cfg.f_values[1];
                var params = toNumber(iv, a, b);
                if (!_.isUndefined(a) && !_.isUndefined(b) && (params[0] <= params[1] || params[0] >= params[2])) {
                    return false;
                }
                return true;
            case '[a,b]':
                var a = cfg.f_values[0];
                var b = cfg.f_values[1];
                var params = toNumber(iv, a, b);
                if (!_.isUndefined(a) && !_.isUndefined(b) && (params[0] < params[1] || params[0] > params[2])) {
                    return false;
                }
                return true;
            default:
                return true;
        }
    };

    var toNumber = function () {
        var arr = _.isArray(arguments[0]) ? arguments[0] : arguments;
        var result = [];
        for (var i = 0; i < arr.length; i++) {
            var a = Number(arr[i]);
            if (isNaN(a)) {
                return arr;
            } else {
                result.push(a);
            }
        }
        return result;
    };
    this.toNumber = toNumber;

    /**
     * Cast the aggregated raw data into data series
     * @param rawData
     * @param chartConfig
     */
    var castRawData2Series = function (aggData, chartConfig) {
        var castedKeys = new Array();
        var castedGroups = new Array();
        var joinedKeys = {};
        var joinedGroups = {};
        var newData = {};

        var getIndex = function (columnList, col) {
            var result = new Array();
            if (col) {
                for (var j = 0; j < col.length; j++) {
                    var idx = _.find(columnList, function (e) {
                        return e.name == col[j];
                    });
                    result.push(idx.index);
                }
            }
            return result;
        };

        var keysIdx = getIndex(aggData.columnList, _.map(chartConfig.keys, function (e) {
            return e.col;
        }));
        var keysSort = _.map(chartConfig.keys, function (e) {
            return e.sort;
        });
        var groupsIdx = getIndex(aggData.columnList, _.map(chartConfig.groups, function (e) {
            return e.col;
        }));
        var groupsSort = _.map(chartConfig.groups, function (e) {
            return e.sort;
        });

        var valueSeries = _.filter(aggData.columnList, function (e) {
            return e.aggType;
        });
        for (var i = 0; i < aggData.data.length; i++) {
            //组合keys
            var newKey = getRowElements(aggData.data[i], keysIdx);
            var jk = newKey.join('-');
            if (_.isUndefined(joinedKeys[jk])) {
                castedKeys.push(newKey);
                joinedKeys[jk] = true;
            }
            //组合groups
            var group = getRowElements(aggData.data[i], groupsIdx);
            var newGroup = group.join('-');
            if (_.isUndefined(joinedGroups[newGroup])) {
                castedGroups.push(group);
                joinedGroups[newGroup] = true;
            }
            // pick the raw values into coordinate cell and then use aggregate function to do calculate
            _.each(valueSeries, function (dSeries) {
                if (_.isUndefined(newData[newGroup])) {
                    newData[newGroup] = {};
                }
                if (_.isUndefined(newData[newGroup][dSeries.name])) {
                    newData[newGroup][dSeries.name] = {};
                }
                if (_.isUndefined(newData[newGroup][dSeries.name][dSeries.aggType])) {
                    newData[newGroup][dSeries.name][dSeries.aggType] = {};
                }
                // if (_.isUndefined(newData[newGroup][dSeries.name][dSeries.aggType][jk])) {
                //     newData[newGroup][dSeries.name][dSeries.aggType][jk] = [];
                // }
                newData[newGroup][dSeries.name][dSeries.aggType][jk] = parseFloat(aggData.data[i][dSeries.index]);
            });
        }
        //sort dimension
        var getSort = function (sort) {
            return function (a, b) {
                var r = 0;
                var j = 0;
                for (; j < a.length; j++) {
                    if (!sort[j]) {
                        continue;
                    }
                    if (a[j] == b[j]) {
                        r = 0;
                        continue;
                    }
                    var params = toNumber(a[j], b[j]);
                    r = (params[0] > params[1]) ? 1 : -1;
                    if (sort[j] == 'desc') r = r * -1;
                    break;
                }
                return r;
            }
        };
        castedKeys.sort(getSort(keysSort));
        castedGroups.sort(getSort(groupsSort));
        //
        var castedAliasSeriesName = new Array();
        var aliasSeriesConfig = {};
        var aliasData = new Array();

        var valueSort = undefined;
        var valueSortArr = [];

        _.each(castedGroups, function (group) {
            _.each(chartConfig.values, function (value) {
                _.each(value.cols, function (series) {
                    if (_.isUndefined(valueSort) && series.sort) {
                        valueSort = series.sort;
                        castSeriesData(series, group.join('-'), castedKeys, newData, function (castedData, keyIdx) {
                            valueSortArr[keyIdx] = {v: castedData, i: keyIdx};
                        });
                    }
                });
            });
        });
        if (!_.isUndefined(valueSort)) {
            valueSortArr.sort(function (a, b) {
                if (a.v == b.v)return 0;
                var p = toNumber(a.v, b.v)
                if ((p[0] < p[1]) ^ valueSort == 'asc') {
                    return 1;
                }
                else {
                    return -1;
                }
            });
            var tk = angular.copy(castedKeys);
            _.each(valueSortArr, function (e, i) {
                castedKeys[i] = tk[e.i];
            });
        }

        _.each(castedGroups, function (group) {
            _.each(chartConfig.values, function (value, vIdx) {
                _.each(value.cols, function (series) {
                    var seriesName = series.alias ? series.alias : series.col;
                    var newSeriesName = seriesName;
                    if (group && group.length > 0) {
                        var a = [].concat(group);
                        a.push(seriesName);
                        newSeriesName = a.join('-');
                        castedAliasSeriesName.push(a);
                    } else {
                        castedAliasSeriesName.push([seriesName]);
                    }
                    //castedAliasSeriesName.push(newSeriesName);
                    aliasSeriesConfig[newSeriesName] = {
                        type: value.series_type,
                        valueAxisIndex: vIdx,
                        formatter: series.formatter
                    };
                    if(vIdx==1){
                        debugger;
                    }
                    castSeriesData(series, group.join('-'), castedKeys, newData, function (castedData, keyIdx) {
                        if (!aliasData[castedAliasSeriesName.length - 1]) {
                            aliasData[castedAliasSeriesName.length - 1] = new Array();
                        }
                        // Only format decimal
                        aliasData[castedAliasSeriesName.length - 1][keyIdx] = castedData;
                    });
                });
            });
        });
        for (var i = 0; i < castedKeys.length; i++) {
            var s = 0;
            var f = true;
            _.each(castedGroups, function (group) {
                _.each(chartConfig.values, function (value) {
                    _.each(value.cols, function (series) {
                        if (!f) {
                            return;
                        }
                        if (series.f_top && series.f_top <= i) {
                            f = false;
                        }
                        if (!filter(series, aliasData[s][i])) {
                            f = false;
                        }
                        if (f) {
                            aliasData[s][i] = dataFormat(aliasData[s][i]);
                        }
                        s++;
                    });
                });
            });
            if (!f) {
                castedKeys.splice(i, 1);
                _.each(aliasData, function (_series) {
                    _series.splice(i, 1);
                });
                i--;
            }
        }
        return {
            keys: castedKeys,
            series: castedAliasSeriesName,
            data: aliasData,
            seriesConfig: aliasSeriesConfig
        };
    };
    /**
     * 无group时多维数组排序
     * @param data
     * @param chartConfig
     */
    var castRawData2Series2=function (aggData,chartConfig) {
        var castedKeys=[];
        var castedAliasSeriesName=[];
        var aliasSeriesConfig={};
        var tempDataArray=[];//缓存当前数据
        var orderArr=[];
        var currentData = [];//排序后的数据
        /**
         * 获取 参数col在aggData.columnList中的下标
         * @param col
         */
        var getIndex=function (col) {
            var index=0;
            for(var i=0;i<aggData.columnList.length;i++){
                if(aggData.columnList[i].name==col){
                    index=i;
                    break;
                }
            }
            return index;
        }
        var isNumber=function (str) {
            var n = Number(str);
            var flag=false;
            if (!isNaN(n))
            {
                flag=true
            }
            return flag;
        }
        function arrfind(arr,ele) {
            var flag=false;
            for(var i=0;i<arr.length;i++){
                if(ele==arr[i].colName){
                    flag=true;
                    break;
                }
            }
            return flag;
        }
        function arrfind1(arr,ele) {
            var flag=false;
            for(var i=0;i<arr.length;i++){
                if(ele[0]==arr[i][0]){
                    flag=true;
                    break;
                }
            }
            return flag;
        }
        _.each(aggData.data,function (value,index) {
            var o={};//一条数据
            _.each(chartConfig.keys,function (v,i) {
                o[v.col]=isNumber(value[i])?parseInt(value[i]):value[i];
                if(isNumber(value[i])){
                    if(value[i].indexOf("E")>-1){
                        var seriesName_value=value[i].split("E");
                        o[v.col]=Math.floor(seriesName_value[0]*Math.pow(10,seriesName_value[1]));
                    }else{
                        o[v.col]=parseInt(value[i]);
                    }
                }else{
                    o[v.col]=value[i];
                }
                if(v.sort!=undefined&&!arrfind(orderArr,v.col))
                {
                    orderArr.push({"colName":v.col,"orderDir":v.sort});
                }
            });
            _.each(chartConfig.values, function (v, vIdx) {
                _.each(v.cols, function (series) {
                    var seriesName = series.col ? series.col : series.alias;
                    if(!arrfind1(castedAliasSeriesName,[seriesName]))
                    castedAliasSeriesName.push([seriesName]);
                    if(typeof(series.exp)!="undefined"){
                        var exp=getExpSeries(series.exp);
                        var oldSeriesExp=series.exp;
                        _.each(exp,function (n,j) {
                            series.exp=series.exp.replace(n.aggregate+"("+n.name+")",value[getIndex(n.name)])
                        })
                        o[seriesName]=Math.round(eval(series.exp)*10000)/10000;
                        series.exp=oldSeriesExp;
                    }else{
                        if(isNumber(value[getIndex(seriesName)])){
                            if(value[getIndex(seriesName)].indexOf("E")>-1){
                                var seriesName_value=value[getIndex(seriesName)].split("E");
                                o[seriesName]=Math.floor(seriesName_value[0]*Math.pow(10,seriesName_value[1]));
                            }else{
                                o[seriesName]=parseInt(value[getIndex(seriesName)]);
                            }
                        }else{
                            o[seriesName]=value[getIndex(seriesName)];
                        }
                        //o[seriesName]=isNumber(value[getIndex(seriesName)])?parseInt(value[getIndex(seriesName)]):value[getIndex(seriesName)];
                    }
                    if(typeof(series.sort)!="undefined"&&!arrfind(orderArr,seriesName))
                    {
                        orderArr.push({"colName":seriesName,"orderDir":series.sort});
                    }
                    aliasSeriesConfig[seriesName] = {
                        type: v.series_type,
                        valueAxisIndex: vIdx,
                        formatter: series.formatter
                    };
                });
            });
            tempDataArray.push(o);
        })
        var sortData = _generateByStr(orderArr);
        currentData = tempDataArray.concat();
        currentData.sort(eval(sortData));
        var aliasData=new Array(castedAliasSeriesName.length);
        for(var i=0;i<castedAliasSeriesName.length;i++){
            aliasData[i]=new Array();
        }
        _.each(currentData,function (value,index) {
            var arr=[];//key数据
            _.each(chartConfig.keys,function (v,i) {
                arr.push(value[v.col])
            });
            castedKeys.push(arr);
            for(var i=0;i<castedAliasSeriesName.length;i++){
                aliasData[i].push(value[castedAliasSeriesName[i][0]]);
            }
        })


        // 生成多列排序字符串方法
        function _generateByStr(tempDataArray) {
            var arr = tempDataArray.concat();
            if (arr == null || arr.length == 0) {
                return "";
            } else {
                if (arr.length > 1) {
                    var a = arr[0];
                    arr.shift();
                    return "_sortBy('" + a.colName + "','" + a.orderDir + "',"
                        + _generateByStr(arr) + ")";
                } else {
                    return "_sortBy('" + arr[0].colName + "','"
                        + arr[0].orderDir + "')";
                }
            }
        }
        // 排序主方法
        function _sortBy(name, dir, minor) {
            return function(o, p) {
                var a, b;
                if (o && p && typeof o === 'object' && typeof p === 'object') {
                    a = o[name];
                    b = p[name];
                    if (a === b) {
                        return typeof minor === 'function' ? minor(o, p) : 0;
                    }
                    if (typeof a === typeof b) {
                        if (dir == "asc") {
                            return a < b ? -1 : 1;
                        } else {
                            return a > b ? -1 : 1;
                        }
                    }
                    if (dir == "desc") {
                        return typeof a < typeof b ? -1 : 1;
                    } else {
                        return typeof a > typeof b ? -1 : 1;
                    }
                } else {
                    throw("error");
                }
            }
        }
        return{
            keys:castedKeys,
            series:castedAliasSeriesName,
            data:aliasData,
            seriesConfig:aliasSeriesConfig
        }

    }
    var castSeriesData = function (series, group, castedKeys, newData, iterator) {
        switch (series.type) {
            case 'exp':
               // debugger;
                var runExp = compileExp(series.exp);
                for (var i = 0; i < castedKeys.length; i++) {
                    iterator(runExp(newData[group], castedKeys[i].join('-')), i);
                }
                break;
            default:
                for (var i = 0; i < castedKeys.length; i++) {
                    iterator(newData[group][series.col][series.aggregate_type][castedKeys[i].join('-')], i)
                }
                break;
        }
    };

    function compileExp(exp) {
        var parseredExp = parserExp(exp);
        return function (groupData, key) {
            var _names = parseredExp.names;
            return eval(parseredExp.evalExp);
        };
    };

    var aggregate = function (data_array, fnc) {
        if (!data_array) {
            return data_array;
        }
        switch (fnc) {
            case 'sum':
                return aggregate_sum(data_array);
            case 'count':
                return aggregate_count(data_array);
            case 'avg':
                return aggregate_avg(data_array);
            case 'max':
                return _.max(data_array, function (f) {
                    return parseFloat(f);
                });
            case 'min':
                return _.min(data_array, function (f) {
                    return parseFloat(f);
                });
        }
    };

    var aggregate_sum = function (data_array) {
        var sum = 0;
        for (var i = 0; i < data_array.length; i++) {
            var f = parseFloat(data_array[i]);
            if (f) {
                sum += f;
            }
        }
        return sum;
    };

    var aggregate_count = function (data_array) {
        return data_array.length;
    };

    var aggregate_avg = function (data_array) {
        var sum = 0;
        var count = 0;
        for (var i = 0; i < data_array.length; i++) {
            var f = parseFloat(data_array[i]);
            if (f) {
                sum += f;
                count++;
            }
        }
        return count == 0 ? 0 : sum / count;
    };

    var getHeaderIndex = function (chartData, col) {
        var result = new Array();
        if (col) {
            for (var j = 0; j < col.length; j++) {
                var idx = _.indexOf(chartData[0], col[j]);
                result.push(idx);
            }
        }
        return result;
    };

    var getRowElements = function (row, elmIdxs) {
        var arr = new Array();
        for (var j = 0; j < elmIdxs.length; j++) {
            var elm = row[elmIdxs[j]];
            arr.push(elm);
        }
        return arr;
    };

    function parserExp(rawExp) {
        var evalExp = rawExp;
        var _temp = [];
        var aggs = [];
        evalExp = evalExp.trim().replace(/[\n|\r|\r\n]/g, '');

        _.each(evalExp.match(/".*?"/g), function (qutaText) {
            evalExp = evalExp.replace(qutaText, '_#' + _temp.length);
            _temp.push(qutaText);
        });

        var names = []; // expression text in aggreagtion function, could be a columnName or script
        _.each(evalExp.match(/(sum|avg|count|max|min|distinct)\("?.*?"?\)/g), function (aggUnit) {
            var aggregate = aggUnit.substring(0, aggUnit.indexOf('('));
            var name = aggUnit.substring(aggUnit.indexOf('(') + 1, aggUnit.indexOf(')'));
            if (name.match("_#")) {
                name = _temp[name.replace("_#", "")].replace(/\"/g, "");
            }
            evalExp = evalExp.replace(aggUnit, "groupData[_names[" + names.length + "]]['" + aggregate + "'][key]");
            names.push(name);
            aggs.push({
                name: name,
                aggregate: aggregate
            });
        });
        return {evalExp: evalExp, aggs: aggs, names: names};
    }
});
