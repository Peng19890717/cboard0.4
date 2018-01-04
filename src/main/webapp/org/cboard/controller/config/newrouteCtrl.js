/**
 * Created by yfyuan on 2017/04/25.
 */
cBoard.controller('newrouteCtrl', function ($scope, $http, dataService, $uibModal, ModalUtils, $filter, chartService, $timeout) {

    // $scope.addStep = function(t){
    //     console.log(2222);
    //     var xhtml = '';
    //     xhtml += '<select class="" onchange="">';
    //     xhtml += '<option selected="" value="请选择">请选择</option>';
    //     xhtml += '<option value="设备品牌">设备品牌</option>';
    //     xhtml += '<option value="机型">机型</option>';
    //     xhtml += '<option value="操作系统">操作系统</option>';
    //     xhtml += '<option value="系统版本号">系统版本号</option>';
    //     xhtml += '<option value="独立应用名称">独立应用名称</option>';
    //     xhtml += '<option value="独立应用版本">独立应用版本</option>';
    //     xhtml += '<option value="登录状态">登录状态</option>';
    //     xhtml += '</select>';
    //     $(t).parent().prev().append(xhtml);
    //     console.log( $(t).parent().prev().attr("id"))
    // }
    //
    // $scope.delStep = function(t){
    //     var id = $(t).parent().prev().attr("id");
    //     var num = $("#"+id).find("select").length;
    //     if(num > 2){
    //         $("#"+id+" select:last-child").remove();
    //     }
    // }
    //
    // $scope.stepAddBtn = function () {
    //     var i = $(".step-two-box").find("ul").length;
    //     i = i + 1;
    //     var xhtml = '';
    //     xhtml += '<ul class="clearfix">';
    //     xhtml += '<li class="fl"><label>　</label></li>';
    //     xhtml += '<li class="fl"><span class="step">第 '+i+' 步</span></li>';
    //     xhtml += '<li class="fl" style="width:782px"><div class="step-box step-two" id="step-box'+i+'">';
    //     xhtml += '<select onchange>';
    //     xhtml += '<option value="影视中心启动">影视中心启动</option>';
    //     xhtml += '<option value="影视中心退出">影视中心退出</option>';
    //     xhtml += '<option value="点击搜索图标">点击搜索图标</option>';
    //     xhtml += '<option value="进入搜索页面">进入搜索页面</option>';
    //     xhtml += '<option value="输入关键词">输入关键词</option>';
    //     xhtml += '<option value="点击搜索结果">点击搜索结果</option>';
    //     xhtml += '<option value="点击热词影片">点击热词影片</option>';
    //     xhtml += '<option value="浏览首页">浏览首页</option>';
    //     xhtml += '<option value="点击专题">点击专题</option>';
    //     xhtml += '<option value="点击影片">点击影片</option>';
    //     xhtml += '</select>';
    //     xhtml += '<select onchange>';
    //     xhtml += '<option selected="" value="请选择">请选择</option>';
    //     xhtml += '<option value="设备品牌">设备品牌</option>';
    //     xhtml += '<option value="机型">机型</option>';
    //     xhtml += '<option value="操作系统">操作系统</option>';
    //     xhtml += '<option value="系统版本号">系统版本号</option>';
    //     xhtml += '<option value="独立应用名称">独立应用名称</option>';
    //     xhtml += '<option value="独立应用版本">独立应用版本</option>';
    //     xhtml += '<option value="登录状态">登录状态</option>';
    //     xhtml += '</select>';
    //     xhtml += '</div><div class="hint">';
    //     xhtml += '<span class="add-step" ng-click="addStep(this)"><img src="../../imgs/u578.png"/></span>';
    //     xhtml += '<span class="del-step" ng-click="delStep(this)">减</span>';
    //     xhtml += '</div></li>';
    //     xhtml += '</ul>';
    //     $(".step-two-box").append(xhtml);
    //
    // }
});



