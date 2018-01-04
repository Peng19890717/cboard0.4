cBoard.controller('newfunnelCtrl', function ($scope, $http,$compile,$state) {
    var sStep = {};
     $scope.difaultInit = function(count){
            for(var m=1; m<=count; m++) {
                $scope['checkModule'+m] = function(v,num){
                    $http.post("dashboardeventsec/getpagedatas.do",{pro_id:v,reload:"false"}).success(function (data) {
                        var colName = new Array();
                        for(var i in data.event_value_name){
                            colName.push({"id":i,"name":data.event_value_name[i]});
                        }
                            $scope['colNameList'+num] = colName;
                            $scope['n'+num] = "";
                    });
                };

         }
     }
    $scope.difaultInit(2);

    //增加步骤
    $scope.stepAddBtn = function(t){
        var i = $(".step-two-box").find("ul").length;
        if(i<20) {
            $scope.difaultInit(i + 1);
            i = i + 1;
            var xhtml = '';
            xhtml += '<ul class="clearfix"  id="step' + i + '">';
            xhtml += '<li class="fl"><label>　</label></li>';
            xhtml += '<li class="fl"><span class="step">第 ' + i + ' 步</span></li>';
            xhtml += '<li class="fl step-two"><div class="step-box step-two" id="step-box-' + i + '">';
            xhtml += '<select ng-change="checkModule' + i + '(v' + i + ',' + i + ')" ng-model="v' + i + '" ng-init="v' + i + '=\'\'">';
            xhtml += '<option value="" selected="selected">请选择</option>';
            xhtml += '<option value="CC_HomePage6" >6.0首页</option>';
            xhtml += '<option value="CC_Video_6.0">6.0影视中心</option>';
            xhtml += '</select>';
            xhtml += '<select id="screen' + i + '" name="" class="form-control" ng-change="screenCondition(n' + i + ',' + i + ')" ng-model="n' + i + '" ng-init="n' + i + '=\'\'">';
            xhtml += '<option value="" selected="selected">请选择</option>';
            xhtml += '<option value={{colname.id}} ng-repeat="colname in colNameList' + i + '">{{colname.name}}</option>';
            xhtml += '</select>';
            xhtml += '<select id="page' + i + '"  ng-show="pageTrue' + i + '==1"  ng-model="p' + i + '" ng-change="pageNameChose(p' + i + ','+i+')">';
            xhtml += '<option value="" selected="selected">请选择</option>';
            xhtml += '<option value="page_name">页面名称</option>';
            xhtml += '</select>';
            xhtml += '<select  ng-change="pageChose('+i+')" ng-show="pageName' + i + '==1" class="form-control pageNum" ng-options="page for page in dataPage' + i + '" ng-model="page' + i + '"></select></div>';
            if (t == 1) {
                xhtml += '<div  ng-show="pageName' + i + '==1" class="hint"><span class="add-step" ng-click="addStep($event,'+i+')"><img src="../../imgs/add.png"/></span>';
                xhtml += '<span class="del-step" ng-click="delStep($event)"><img src="../../imgs/remove.png"/></span></div>';
            }
            xhtml += '</li>';
            xhtml += '</ul>';
            var $html = $compile(xhtml)($scope);
            $(".step-two-box").append($html);
        }else{
            $scope.m_step = "1";
            $scope.stepText = "最多增加20步哦！"
        }
    }

    $scope.remove = function(arr, val) {
        for(var i=0; i<arr.length; i++) {
            if(arr[i] == val) {
                arr.splice(i, 1);
                break;
            }
        }
    }
    //减少步骤
    $scope.reduceBtn = function(){
        var i = $(".step-two-box").find("ul").length;
        if(i>2){
            $("#step"+i).remove();
            $scope.remove(bStep,bStep[i-1]);
        }else{
            $scope.m_step = "1";
            $scope.stepText = "最低保留两个步骤哦！"
        }
    }

    var npvUv = [2];//pv,uv
    var bStep = [];//步骤


    //pv,uv选择
    // $scope.clickCollect = function(event,val){
    //     var className = $(event.target).context.className;
    //         if(className == "jstree-icon jstree-checkbox jstree-clicked"){
    //             $(event.target).removeClass('jstree-clicked');
    //             $scope.remove(npvUv,val);
    //         }else{
    //             npvUv.push(val);
    //             $(event.target).addClass('jstree-clicked');
    //         }
    //         console.log(npvUv);
    //         if(npvUv.length<1){
    //             $scope.pvuv = "1";
    //         }else{
    //             $scope.pvuv = ""
    //         }
    //
    // }

    $scope.pageNameChose = function(page,num){
        if(page != ""){
            $scope["pageName"+num] = "1";
            sStep[num]={"vpro_module":$scope['v'+num],"vlog_name":$scope['n'+num],"vpage_name":$scope['page'+num]};
        }else{
            $scope["pageName"+num] = "";
            sStep[num]={"vpro_module":$scope['v'+num],"vlog_name":$scope['n'+num]};
        }
        var len = bStep.length;
        if(len<num){
            bStep.push(sStep[num])
        }else{
            bStep[num-1] = sStep[num]
        }
        console.log(bStep);

    }


    //筛选条件
    $scope.screenCondition = function(id,num){
        var pro_id = $scope["v"+num];
        console.log(pro_id);
        $http.post("dashboardeventsec/getpagedatas.do",{pro_id:pro_id,event_id:id}).success(function (data) {
            if(data.page_name){
                $scope['pageTrue'+num] = "1";
                $scope['dataPage'+num] = data.page_name;
                $scope['page'+num] = $scope['dataPage'+num][0];
                // sStep[num]={"vpro_module":$scope['v'+num],"vlog_name":$scope['n'+num],"vpage_name":$scope['page'+num]};
            }else{
                $scope['pageTrue'+num] = "0";
            }
            sStep[num]={"vpro_module":$scope['v'+num],"vlog_name":$scope['n'+num]};
            var len = bStep.length;
            if(len<num){
                bStep.push(sStep[num])
            }else{
                bStep[num-1] = sStep[num]
            }
            console.log(bStep);

        });
    }

    //页面名称改变
    $scope.pageChose = function(num){
        var len = bStep.length;
        sStep[num]={"vpro_module":$scope['v'+num],"vlog_name":$scope['n'+num],"vpage_name":$scope['page'+num]};
        if(len<num){
            bStep.push(sStep[num])
        }else{
            bStep[num-1] = sStep[num]
        }
        console.log(bStep);
    }


//保存
    $scope.saveBtn = function(){
        console.log(bStep);
        if(!$scope.funnelName){
            $("#funnelName").focus();
            $scope.funnel_Name = "1";
            return
        }
        if(bStep.length<2){
            $scope.m_step = "1";
            $scope.stepText = "请选择至少两个步骤"
            return
        }
       var requestParam = {
            vname:$("#funnelName").val(),
           vdata_source:$("#video").val(),
           //npv_uv:npvUv,
           npv_uv:[1],
            steps:bStep,
        }
       //  var requestParam = {
       //      "vname": "fd",
       //      "vdata_source": "全部",
       //      "npv_uv": [2,1],
       //      "steps": [
       //          {
       //              "vpro_module": "CC_HomePage6",
       //              "vlog_name": "page_view_event",
       //              "vpage_name": "主页"
       //          },
       //          {
       //              "vpro_module": "CC_Video_6.0",
       //              "vlog_name": "page_show_succeed",
       //              "vpage_name": "aliplay_pay_page"
       //          }
       //      ]
       //  };
        $http({
            method: "post",
            contentType: "application/json",
            dataType: "json",
            data:JSON.stringify(requestParam),
            headers: {'Content-Type': 'application/json'},
            url:"dashboardfiguresec/savefunneldata.do",
        }).success(function(data){
            console.log(data);
            if(data.msg == 1){
                $state.go('config.customFunnel');
            }
        });

    }

    $scope.addStep = function(event,i){
        var xhtml = '';
        var n = $("#step-box-" + i).find(".pageNum").length;
        n = n+1;
        $scope['page'+n] = $scope['dataPage'+i][0];
        xhtml += '<select  ng-change="pageChose('+i+')" ng-show="pageName' + i + '==1" class="form-control pageNum" ng-options="page for page in dataPage' + i + '" ng-model="page' + n + '"></select>';
        var $html = $compile(xhtml)($scope);
        $($(event.target)).parent().parent().prev().append($html);
    }

    $scope.delStep = function(event){
        var id = $(event.target).parent().parent().prev().attr("id");
        var num = $("#"+id).find("select").length;
        if(num > 4){
            $("#"+id+" select:last-child").remove();
        }
    }

});



