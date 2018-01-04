/**
 * Created by wanghaihua on 2017/6/2
 */


cBoard.controller('customFunnelCtrl', function ($scope, $http, $q) {

    $scope.funnelChart=function (legend,data) {
        var max=_.max(data,function (num) {
            return num.value
        }).value;
        var min=_.min(data,function (num) {
            return num.value
        }).value;
        var myChart = echarts.init(document.getElementById("funnelChart"),"theme-fin1");
        option = {
            // title: {
            //
            //     text: '漏斗图',
            //     subtext: '纯属虚构'
            // },
            tooltip: {
                trigger: 'item',
                formatter: "{a} <br/>{b} : {c}"
            },
            toolbox: {
                feature: {
                    dataView: {readOnly: false},
                    restore: {},
                    saveAsImage: {}
                }
            },
            legend: {
                data: legend
            },
            calculable: true,
            series: [
                {
                    name:'漏斗图',
                    type:'funnel',
                    left: '10%',
                    top: 60,
                    //x2: 80,
                    bottom: 60,
                    width: '80%',
                    // height: {totalHeight} - y - y2,
                    min: 0,
                    max: parseInt(max),
                    minSize: '0%',
                    maxSize: '100%',
                    sort: 'descending',
                    gap: 2,
                    label: {
                        normal: {
                            show: true,
                            position: 'inside'
                        },
                        emphasis: {
                            textStyle: {
                                fontSize: 20
                            }
                        }
                    },
                    labelLine: {
                        normal: {
                            length: 10,
                            lineStyle: {
                                width: 1,
                                type: 'solid'
                            }
                        }
                    },
                    itemStyle: {
                        normal: {
                            borderColor: '#fff',
                            borderWidth: 1
                        }
                    },
                    data: data
                    //     [
                    //     {value: 60, name: '访问'},
                    //     {value: 40, name: '咨询'},
                    //     {value: 20, name: '订单'},
                    //     {value: 80, name: '点击'},
                    //     {value: 100, name: '展现'}
                    // ]
                }
            ]
        };
        myChart.setOption(option);

    }
    //getRouteChart("funnelChart");
    //时间插件
    $scope.date = {
        startDate: moment().subtract(7, "days"),
        endDate: moment().subtract(0, "days"),
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
    var getVideoName=function(englighName){
        switch(englighName){
            case "yinhe":
                return "爱奇艺"
                break;
            case "tencent":
                return "腾讯"
                break;
        }

    }
    var getVideoEnglishName=function(data,index){
        if(data[index].value==null)
            return "未知";
        var videoName=data[index].value.split(",");
        var str=[];
        for(var i in videoName){
            str.push(getVideoName(videoName[i]));
        }
        return str.join(",");

    }
    var funnelNameList=function () {
        // var deferred = $q.defer()
        $http.post("dashboardfiguresec/getFunnelAnalysisData.do").success(function (funnelNameListJson) {
            var getJSON=function (data) {
                var mapData=[];
                for(var i in data){
                    var o={};
                    o.key=i;
                    o.value=data[i];
                    mapData.push(o);
                }
                return mapData.reverse();
            }
            $scope.funnelNameList=getJSON(funnelNameListJson.mapData);
            if($scope.funnelNameList.length>0)
                $scope.funnelNameList_value=$scope.funnelNameList[0].key+",0";
            $scope.mapVideo=getJSON(funnelNameListJson.mapVideo);
            $scope.videName=getVideoEnglishName($scope.mapVideo,0);
            $scope.initFunnel();
        });

        // $http({
        //     method: 'post',
        //     url: 'dashboardfiguresec/getFunnelAnalysisData.do'
        // }).then(function (response) {
        //    debugger
        // }, function(response) {
        //   debugger
        // });

    }
    $scope.funnelNameChange=function (index) {
        $scope.videName=getVideoEnglishName($scope.mapVideo,$scope.funnelNameList_value.split(",")[1]);
    }
    funnelNameList();
    var initfun=function () {

    }
    $scope.formatTen = function(num) {
        return num > 9 ? (num + "") : ("0" + num);
    }

    $scope.formatDate = function(date) {
        var year = date.getFullYear();
        var month = date.getMonth() + 1;
        var day = date.getDate();
        var hour = date.getHours();
        var minute = date.getMinutes();
        var second = date.getSeconds();
        return year + "-" + $scope.formatTen(month) + "-" + $scope.formatTen(day);
    }

    //接口
    $scope.initFunnel=function () {
        var requestParam={};
        requestParam.pk_funnel_path=$scope.funnelNameList_value.split(",")[0];
        requestParam.start_time=$scope.formatDate((new Date($scope.date.startDate._d)));
        requestParam.end_time=$scope.formatDate((new Date($scope.date.endDate._d)));
        $http.post("dashboardfiguresec/getfunneldatas.do",requestParam).success(function (data) {
            if(parseInt(data.msg)==1){
                if(data.datas.length==0){
                    alert("没有数据！");
                    return;
                }
                var legend=[],seriesData=[];
                _.each(data.datas,function (v,key) {
                    legend.push(v.event_name);
                    var o={};
                    o.value=v.val;
                    o.name=v.event_name;
                    seriesData.push(o);
                })
                $scope.funnelChart(legend,seriesData);

            }else{
                alert(data.errorMsg);
            }
        })
    }

    $scope.getFunnelChart=function () {
        $scope.initFunnel();
    }
});