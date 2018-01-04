function stepAddBtn(t){
console.log(111,t);
    var i = $(".step-two-box").find("ul").length;
        i = i + 1;
    var xhtml = '';
    xhtml += '<ul class="clearfix">';
    xhtml += '<li class="fl"><label>　</label></li>';
    xhtml += '<li class="fl"><span class="step">第 '+i+' 步</span></li>';
    xhtml += '<li class="fl" style="width:782px"><div class="step-box step-two" id="step-box'+i+'">';
    xhtml += '<select onchange>';
    xhtml += '<option value="\'CC_HomePage6\'" >6.0首页</option>';
    xhtml += '<option value="\'CC_Video_6.0\'">6.0影视中心</option>';
    xhtml += '</select>';
    xhtml += '<select onchange>';
    xhtml += '<option selected="" value="请选择">请选择</option>';
    xhtml += '<option value="设备品牌">设备品牌</option>';
    xhtml += '<option value="机型">机型</option>';
    xhtml += '<option value="操作系统">操作系统</option>';
    xhtml += '<option value="系统版本号">系统版本号</option>';
    xhtml += '<option value="独立应用名称">独立应用名称</option>';
    xhtml += '<option value="独立应用版本">独立应用版本</option>';
    xhtml += '<option value="登录状态">登录状态</option>';
    xhtml += '</select>';
    xhtml += '</div><div class="hint">';
    if(t == 1){
        xhtml += '<span class="add-step" onClick="addStep(this)"><img src="../../imgs/add.png"/></span>';
        xhtml += '<span class="del-step" onClick="delStep(this)"><img src="../../imgs/remove.png"/></span>';
    }
    xhtml += '</div></li>';
    xhtml += '</ul>';
    $(".step-two-box").append(xhtml);

}

function addStep(t){
    var xhtml = '';
    xhtml += '<select class="" onchange="">';
    xhtml += '<option selected="" value="请选择">请选择</option>';
    xhtml += '<option value="设备品牌">设备品牌</option>';
    xhtml += '<option value="机型">机型</option>';
    xhtml += '<option value="操作系统">操作系统</option>';
    xhtml += '<option value="系统版本号">系统版本号</option>';
    xhtml += '<option value="独立应用名称">独立应用名称</option>';
    xhtml += '<option value="独立应用版本">独立应用版本</option>';
    xhtml += '<option value="登录状态">登录状态</option>';
    xhtml += '</select>';
    $(t).parent().prev().append(xhtml);
     console.log( $(t).parent().prev().attr("id"))
}

function delStep(t){
    var id = $(t).parent().prev().attr("id");
	var num = $("#"+id).find("select").length;
	if(num > 2){
		$("#"+id+" select:last-child").remove();
	}
}