cBoard.controller('routeCtrl', function ($scope, $http, dataService, $uibModal, ModalUtils, $filter, chartService, $timeout) {
//初始化
    var datapvTime = [];
    var datapvValue = [];
    var datauvValue = [];

    var dimValue = [];
    var otherDimValue = [];
    var otherDim;
    var aa;
    var bb;
    sessionStorage.setItem("event_id","'detail_recommand_clicked'");//默认详情页-推荐位影片点击
    $scope.event_name = "详情页-推荐位影片点击";
    $scope.defaultInit = function(type,query,obj,timetype,startTime,endTime,dimtype){
        var requestParam = {
            // pkg_source: "",视频源$("#pkg_sources").val()
            pro_id:$scope.item3,//产品模块
            event_id: sessionStorage.getItem("event_id"),//事件名称
            condition_dim:$("#screen").val(),//筛选条件
            condition_relation:$("#data").val(),//筛选条件关系
            condition_value:$("#data_list").val() || $("#login_list").val() || $scope.page_name,//筛选条件值
            dim:$scope.item,//细分属性
            dim_value:obj,//细分属性维度
            time:timetype,//时间间隔
            start_time:startTime,//开始时间
            end_time:endTime,//结束时间
            init:type,//查询类型
            button_query:query,
            reload:"true",

        }
        $http.post("dashboardeventsec/query.do",requestParam).success(function (data) {
            //  console.log(data);
            if(data.msg == 1) {
                if(type == "true"){
                    var sources = new Array();
                    for (var i in data.data_sources) {
                        sources.push({"id": i, "name": data.data_sources[i]});
                    }
                    $scope.sourcesList = sources;//初始化绑定产品模块
                    $scope.item3 = data.data_sources_default;//默认产品模块
                    sessionStorage.setItem("data_sources_default", data.data_sources_default);

                    var dataDim = new Array();//细分属性
                    for (var i in data.dim) {
                        dataDim.push({"id": i, "name": data.dim[i]});
                    }
                    $scope.dataDim = dataDim;
                    $scope.item = data.dim_default;//初始化选中
                    $scope.dimValues = data.dim_values;
                    sessionStorage.setItem("dimValues",data.dim_values);
                    otherDimValue =data.dim_values;
                    dimValue =data.dim_values;
                    otherDim = data.dim_values.join(",");
                    sessionStorage.setItem("choseValues","");
                    $scope.tacitly = true;
                    aa = sessionStorage.getItem("dimValues").split(",");
                    console.log("初始化传参",otherDim);
                    var initList = new Array();//初始化事件名称
                    for(var i in data.event_value_name){
                        initList.push({"id":i,"name":data.event_value_name[i]})
                    }
                    $scope.initEventList = initList;


                    var colName = new Array();//初始化筛选条件
                    for(var i in data.condition_col_name){
                        colName.push({"id":i,"name":data.condition_col_name[i]});
                    }
                    $scope.colNameList = colName;
                    $scope.colValList = data.condition_col_value;
                }else{
                    $scope.tacitly = false;
                    datapvValue.length = 0;
                    datapvTime.length = 0;
                    datauvValue.length = 0;


                    if(dimtype == "2"){//查询更新
                        $scope.seaValues = data.dim_values;
                        sessionStorage.setItem("choseValues",data.dim_values);//是否有查询的值
                        bb = sessionStorage.getItem("choseValues").split(",");
                        otherDim = bb.join(",");
                    }else if(dimtype == "1" || dimtype == "0"){
                        otherDim = obj;
                        if(sessionStorage.getItem("choseValues")){
                            $scope.tacitly = false;
                            $scope.seaValues = sessionStorage.getItem("choseValues").split(",");
                        }else{
                            $scope.tacitly = true;
                            $scope.seaValues = sessionStorage.getItem("dimValues").split(",");
                        }
                    }
                    console.log("其他点击传参",otherDim);

                }



                if(data.data_pv){
                    for (var i = 1; i < data.data_pv.length; i++) {
                        datapvValue.push(data.data_pv[i][1]);
                        datapvTime.push(data.data_pv[i][0]);//时间轴
                    }
                }

                if(data.data_uv){
                    for (var k = 1; k < data.data_uv.length; k++) {
                        datauvValue.push(data.data_uv[k][1]);//uv值
                    }
                }

                if(data.data_pv_uv_sum){
                    var dataSum = data.data_pv_uv_sum.slice(1)
                    $scope.dataSum = dataSum;
                }else{
                    $scope.dataSum = [];
                }
                if(data.data_pv_uv_time){
                    var dataTime = data.data_pv_uv_time.slice(1)
                    $scope.dataTime = dataTime;
                }else{
                    $scope.dataTime = [];
                }
                $scope.getLineChart();

            }else{
                alert(data.errorMessage);
            }
        });
    }
    $scope.defaultInit("true","false");
    $scope.initTime = false;//分析详情初始化没有时间字段


//报表

    $scope.getLineChart = function(){
        console.log(datapvValue);
        var theme = {
            //
            color: [
                '#1790cf','#1bb2d8','#99d2dd','#88b0bb',
                '#1c7099','#038cc4','#75abd0','#afd6dd'
            ],
        };
        var myChart = echarts.init(document.getElementById("chartContent"),theme);

        if(datapvTime.length > 6){
            var intetype = 'auto';
            var num = 0;
        }else{
            var intetype = 0;
            var num = 0;
        }
        option = {
            title : {
                text: '',
                subtext: ''
            },
            tooltip : {
                trigger: 'axis'
            },
            legend: {
                data:['PV','UV']
            },
            toolbox: {
                show : true,
                feature : {
                    mark : {show: true},
                    dataView : {show: true, readOnly: false},
                    magicType : {show: true, type: ['line', 'bar']},
                    restore : {show: true},
                    saveAsImage : {show: true}
                }
            },
            grid: {
                left: '2%',
                right: '6%',
                bottom: '5%',
                containLabel: true
            },
            calculable : true,
            xAxis : [
                {
                    type : 'category',
                    boundaryGap : false,
                    data : datapvTime,
                    axisLabel:{ interval:intetype,
                        rotate:num,
                        margin:8,
                    }
                }
            ],
            yAxis : [
                {
                    type : 'value',
                }
            ],
            series : [
                {
                    name:'PV',
                    type:'line',
                    data:datapvValue,
                    markLine : {
                        data : [
                            {type : 'average', name: '平均值'}
                        ]
                    }
                },
                {
                    name:'UV',
                    type:'line',
                    data:datauvValue,
                    markLine : {
                        data : [
                            {type : 'average', name : '平均值'}
                        ]
                    }
                }
            ]
        };
        // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
    }


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

    //查询还原为昨天的时间
    sessionStorage.setItem("sTime",$scope.formatDate($scope.date.startDate._d));
    sessionStorage.setItem("eTime",$scope.formatDate($scope.date.endDate._d));

    //Watch for date changes
    $scope.count = 0;
    $scope.$watch('date', function(newDate) {
        var startTime = $scope.formatDate(newDate.startDate._d);
        var endTime = $scope.formatDate(newDate.endDate._d);
        sessionStorage.setItem("startTime",startTime);
        sessionStorage.setItem("endTime",endTime);
        $scope.count++;
        if ($scope.count > 1){
            //监听次数
            $scope.defaultInit("false","false",otherDim,$scope.timetype,startTime,endTime,"0");
            $scope.initTime = true;
        }else if($scope.count = 1){
            $scope.dftTime = newDate;
        }


    }, false);


//细分属性模糊搜索
    $(function(){
        $("input[type='text']","#search_condition").keyup(function(){
            var obj=this;
            $("li","#search_condition").hide();
            $("li","#search_condition").filter(function(index){
                return $(this).text().indexOf($(obj).val())>=0;
            }).show();
        });
    })

    $(function(){
        $("input[type='text']","#search_conditiona_sea").keyup(function(){
            var obj=this;
            $("li","#search_condition_sea").hide();
            $("li","#search_condition_sea").filter(function(index){
                return $(this).text().indexOf($(obj).val())>=0;
            }).show();
        });
    })

//产品模块选择
    $scope.checkModule = function(x){
        $scope.initEvent(x);//事件名称
        sessionStorage.setItem("initId",x);
    }
    $scope.initEvent = function(x) {
        $http.post("dashboardeventsec/getlinkagedatas.do",{pro_id:x,reload:"false"}).success(function (data) {
            var initList = new Array();
            for(var i in data.event_value_name){
                initList.push({"id":i,"name":data.event_value_name[i]})
            }
            $scope.initEventList = initList;
        });
    }
    // $scope.initEvent(sessionStorage.getItem("data_sources_default"));
//模糊搜索
    $(function(){
        $("input[type='text']","#search_event").focus(function(){
            $(".event-list").show();
        });
        $(".page-input").focus(function(){
            $(".page-list").show();
        });

        $("input[type='text']","#search_event").keyup(function(){
            var obj=this;
            $("li","#search_event").hide();
            $("li","#search_event").filter(function(index){
                return $(this).text().indexOf($(obj).val())>=0;
            }).show();
        });

        $("input[type='text']","#page_event").keyup(function(){
            var obj=this;
            $("li","#page_event").hide();
            $("li","#page_event").filter(function(index){
                return $(this).text().indexOf($(obj).val())>=0;
            }).show();
        });
    })



    //事件名称下拉框选择
    $scope.checkEvent = function(val,name){
        sessionStorage.setItem("event_id",val);
        console.log(val);
        $(".event-menu").hide();
        $scope.event_name = name;
        $http.post("dashboardeventsec/getlinkagedatas.do",{pro_id:sessionStorage.getItem("initId"),event_id:val,reload:"false"}).success(function (data) {
            var colName = new Array();
            for(var i in data.condition_col_name){
                colName.push({"id":i,"name":data.condition_col_name[i]});
            }
            $scope.colNameList = colName;
            $scope.colValList = data.condition_col_value;

            $scope.dataTrue = "0";
            $scope.n = "";
        });
    }
    //筛选条件值下拉框
    $scope.checkPage = function(id,name){
        $(".event-menu").hide();
        $scope.page_name = name;
    }
    $(document).bind("click",function(e){
        var target  = $(e.target);    //e.target获取触发事件的元素****隐藏下拉框
        if(target.closest("#search_event,#page_event").length == 0){
            $(".event-menu").hide();
        };
        e.stopPropagation();
    })


//筛选条件
    $scope.screenCondition = function(id){
        for(var n in $scope.colValList){
            if(id == n){
                console.log(n);
                $scope.thrId = n;
                $scope.thrList = $scope.colValList[n];
            }
            if(id == "model" || id == "brand" || id == "pkg_name"){
                $scope.dataTrue ="1";//等于，不等于，包含，不包含，为空，不为空

            }else if(id == "uid"){
                $scope.dataTrue = "2";//登录
            }else if(id == "page_name" || id == "event_id" || id == "event_name" || id == "event_pid" || id == "event_version" || id == "forum_name" || id == "forum_version" || id == "mainpage_type" || id == "mainpage_version"){
                $scope.dataTrue = "3";//页面名称
                $("#page_event").show();
            }else if(id == "os_vercode" || id == "pkg_vername"){
                $scope.dataTrue = "4";//小于，小于等于
            }
        }
    }


//*checkbox点击*/
//     Array.prototype.remove = function(val) {
//         var index = this.indexOf(val);
//         if (index > -1) {
//             this.splice(index, 1);
//         }
//     };

    $scope.remove = function(arr, val) {
        for(var i=0; i<arr.length; i++) {
            if(arr[i] == val) {
                arr.splice(i, 1);
                break;
            }
        }
    }
    // console.log("初始化的值",aa);
    // console.log("查询过的值",bb);
    $scope.clickCollect = function(event,val,type){
        var className = $(event.target).context.className;
        if(type == "T"){
            if(className == "jstree-icon jstree-checkbox jstree-clicked"){
                //aa.remove(val);
                $scope.remove(aa,val);
                $(event.target).removeClass('jstree-clicked');
            }else{
                aa.push(val);
                $(event.target).addClass('jstree-clicked');
            }
            $scope.obj = aa.join(",");
        }else if(type == "C"){
            if(className == "jstree-icon jstree-checkbox jstree-clicked"){
                //bb.remove(val);
                $scope.remove(bb,val);
                $(event.target).removeClass('jstree-clicked');
            }else{
                bb.push(val);
                $(event.target).addClass('jstree-clicked');
            }
            $scope.obj = bb.join(",");
        }
        $scope.defaultInit("false","false",$scope.obj,$scope.timetype,sessionStorage.getItem("startTime"),sessionStorage.getItem("endTime"),"1");
        $scope.initTime = true;
    }


//点击时间间隔
    $scope.changeTime = function(timetype){
        $scope.defaultInit("false","false",otherDim,timetype,sessionStorage.getItem("startTime"),sessionStorage.getItem("endTime"),"0");
        $scope.timetype = timetype;
        $scope.initTime = true;
    }

    //查询
    $scope.querySub = function(){
        $scope.defaultInit("false","true",otherDim,"时",sessionStorage.getItem("sTime"),sessionStorage.getItem("eTime"),"2");
        $scope.timetype = "时";
        $scope.date = $scope.dftTime;
        // console.log("查询返回默认时间",$scope.dftTime);
        $scope.initTime = false;
    }
    //导出excel
    $scope.exportExcel = function(){
        $(".table-striped").table2excel({
            exclude: ".noExl",
            name: "趋势表",
            filename: "myFileName",
            fileext: ".xls",
            exclude_img: true,
            exclude_links: true,
            exclude_inputs: true
        });
    }


});
