/*
 daterangpicker 时间控件 初始化 daterangepicker方法 add by wanghaihua
 */
!(function($, moment){
	$.fn.date_range_picker=function(options){
	var defaultOptions={
      "autoApply": false,
      "opens": 'right',
      format:'YYYY/MM/DD',
     //  autoUpdateInput: true,
      "alwaysShowCalendars": true,
      "ranges": {
          //'今天': [moment(), moment()],
          '昨天': [moment().subtract(1, 'days'), moment().subtract(1, 'days')],
          '过去7天': [moment().subtract(7, 'days'), moment().subtract(1, 'days')],
          '上个月': [moment().subtract(1, 'month').startOf('month'), moment().subtract(1, 'month').endOf('month')],
          '过去30天': [moment().subtract(30, 'days'), moment().subtract(1, 'days')],
          '过去60天': [moment().subtract(60, 'days'), moment().subtract(1, 'days')]
      },
       "alwaysShowCalendars": true,
          "locale": {
            "format": "MM/DD/YYYY",
            "separator": " - ",
            "applyLabel": "确定",
            "cancelLabel": "取消",
            "fromLabel": "From",
            "toLabel": "To",
            "customRangeLabel": "Custom",
            "daysOfWeek": [
                "周日",
                "周一",
                "周二",
                "周三",
                "周四",
                "周五",
                "周六"
            ],
            "monthNames": [
                "一月",
                "二月",
                "三月",
                "四月",
                "五月",
                "六月",
                "七月",
                "八月",
                "九月",
                "十月",
                "十一月",
                "十二月"
            ],
            "firstDay": 1
        },
    "startDate":sessionStorage.beginTime!=undefined?sessionStorage.beginTime:moment().subtract(30, 'days'),
    "endDate":sessionStorage.endTime!=undefined?sessionStorage.endTime:moment().subtract(1, 'days')
     };
    var option=$.extend(defaultOptions,options);
    $(this).daterangepicker(option,
		function(start, end, label) {
		 console.log("New date range selected: ' + start.format('YYYY-MM-DD') + ' to ' + end.format('YYYY-MM-DD') + ' (predefined range: ' + label + ')");
		});
	}
})(window.jQuery, window.moment);
/*
基本折线图
 */
function getLineChart(id){
	var myChart = echarts.init(document.getElementById(id));
	option = {
    title: {
        text: ''
    },
    tooltip : {
        trigger: 'axis',
        axisPointer: {
            type: 'cross',
            label: {
                backgroundColor: '#6a7985'
            }
        }
    },
    legend: {
        data:['邮件营销','联盟广告']
    },
    toolbox: {
        feature: {
            saveAsImage: {}
        }
    },
    grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
    },
    xAxis : [
        {
            type : 'category',
            boundaryGap : false,
            data : ['周一','周二','周三','周四','周五','周六','周日']
        }
    ],
    yAxis : [
        {
            type : 'value'
        }
    ],
    series : [
        {
            name:'邮件营销',
            type:'line',
            stack: '总量',
            areaStyle: {normal: {}},
            data:[120, 132, 101, 134, 90, 230, 210]
        },
        {
            name:'联盟广告',
            type:'line',
            stack: '总量',
            areaStyle: {normal: {}},
            data:[220, 182, 191, 234, 290, 330, 310]
        }
    ]
};
   // 使用刚指定的配置项和数据显示图表。
        myChart.setOption(option);
}
/*
模糊搜索框 add by wanghaihua
 */
// (function(){
       // $("div:visible",$(this).parents(".searchW").next().next()).hide();
       //      $("div",$(this).parents(".searchW").next().next()).filter(function(index){
       //      return $('span',this).html().lastIndexOf($(obj).val().toLocaleUpperCase())>=0;
       //    }).show();
// })();
$(function(){
    $("input[type='text']","#search_condition").keyup(function(){
        var obj=this;
       $("li","#search_condition").hide();
       $("li","#search_condition").filter(function(index){
        return $(this).text().indexOf($(obj).val())>=0;
       }).show();
    });
})
$("#singleTime").date_range_picker({singleDatePicker: true});
$("#rangeTime").date_range_picker();
