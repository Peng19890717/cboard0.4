package org.cboard.dto.sec;
/**
 * Created by Administrator on 2017/4/28 0028.
 */


import com.alibaba.fastjson.JSON;
import org.cboard.consts.BackResultConst;
import org.cboard.consts.CommonConst;
import org.cboard.consts.InitConst;
import org.cboard.consts.LogicalConst;
import org.cboard.dto.DataProviderResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 事件分析返回值
 *
 * @author wbc
 * @date 2017-04-28
 **/
public class DataAnalyzeResult extends DataProviderResult {

    public static final String SUCCESS = BackResultConst.SUCCESS;
    public static final String FAIL = BackResultConst.FAIL;

    private String errorMessage;

    public DataAnalyzeResult(String[][] data, String msg) {
        super(data, msg);
    }

    public DataAnalyzeResult(String[][] data, String msg, int resultCount) {
        super(data, msg, resultCount);
    }

    //关系运算符（10种）
    private String[] logical_relations = new String[]{
            LogicalConst.NOTEQUALS,
            LogicalConst.EQUALS,
            LogicalConst.IN,
            LogicalConst.GREATER,
            LogicalConst.GREATER_EQUALS,
            LogicalConst.SMALLER,
            LogicalConst.LIKE,
            LogicalConst.NOTLIKE,
            LogicalConst.ISNULL,
            LogicalConst.ISNOTNULL
    };
    //存放页面中文名（该字段仅仅为SecondController的getPageDatas(...)方法服务）
    private List<String> page_name;

    //数据源(两个值：腾讯、爱奇艺)
    //key：数据库值；value：中文名；
    private Map<String, String> pkg_sources;
    private String pkg_sources_default = CommonConst.SOURCE_ALL;//默认选择的

    //产品模块
    //key：数据库值；value：中文名；
    private Map<String, String> data_sources;
    private String data_sources_default = CommonConst.CC_VIDEO;//产品模块默认选择6.0影视中心

    // key：事件英文名称（对应kylin的base_clog_view的data_source_type，对应pro_log的log_name）；
    // value：数据库事件名称的中文名
    private Map<String, String> event_value_name;
    private String event_value_name_default = InitConst.INIT_EVENT;//默认选择的值

    //key：筛选条件维度英文值（即base_clog_view的列名/维度名）；value:筛选条件（中文名）
    private Map<String, String> condition_col_name;
    //key：筛选条件维度英文值（即base_clog_view的列名/维度名）；
    //value：维度对应的值（去重后的值）
    private Map<String, List<String>> condition_col_value;
    //细分属性
    //key：列维度名（列英文名）；value：列中文名；
    private Map<String, String> dim;
    private String dim_default = CommonConst.BRAND;//细分属性默认选择（设备品牌）
    //初始化查询时：指定细分属性（目前是设备品牌）对应的数据库所有值
    //点击查询按钮时：存储中间细分属性的筛选条件值
    private String[] dim_values;
    //时间间隔（时、日、周、月；只有这4种值；）
    private String[] time = new String[]{CommonConst.HOUR, CommonConst.DAY, CommonConst.WEEK, CommonConst.MONTH};
    //pv数据集
    private String[][] data_pv;
    //uv数据集
    private String[][] data_uv;
    //pv_uv按照时间间隔进行分组
    private String[][] data_pv_uv_time;
    //pv_uv按照时间间隔进行汇总
    private String[][] data_pv_uv_sum;

    public Map<String, String> getEvent_value_name() {
        return event_value_name;
    }

    public void setEvent_value_name(Map<String, String> event_value_name) {
        this.event_value_name = event_value_name;
    }

    public Map<String, String> getCondition_col_name() {
        return condition_col_name;
    }

    public void setCondition_col_name(Map<String, String> condition_col_name) {
        this.condition_col_name = condition_col_name;
    }

    public Map<String, List<String>> getCondition_col_value() {
        return condition_col_value;
    }

    public void setCondition_col_value(Map<String, List<String>> condition_col_value) {
        this.condition_col_value = condition_col_value;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Map<String, String> getPkg_sources() {
        return pkg_sources;
    }

    public void setPkg_sources(Map<String, String> pkg_sources) {
        this.pkg_sources = pkg_sources;
    }

    public Map<String, String> getData_sources() {
        return data_sources;
    }

    public void setData_sources(Map<String, String> data_sources) {
        this.data_sources = data_sources;
    }

    public Map<String, String> getDim() {
        return dim;
    }

    public void setDim(Map<String, String> dim) {
        this.dim = dim;
    }

    public String[] getLogical_relations() {
        return logical_relations;
    }

    public void setLogical_relations(String[] logical_relations) {
        this.logical_relations = logical_relations;
    }

    public String[] getDim_values() {
        return dim_values;
    }

    public void setDim_values(String[] dim_values) {
        this.dim_values = dim_values;
    }

    public String[] getTime() {
        return time;
    }

    public String[][] getData_pv() {
        return data_pv;
    }

    public void setData_pv(String[][] data_pv) {
        this.data_pv = data_pv;
    }

    public String[][] getData_uv() {
        return data_uv;
    }

    public void setData_uv(String[][] data_uv) {
        this.data_uv = data_uv;
    }

    public String[][] getData_pv_uv_time() {
        return data_pv_uv_time;
    }

    public void setData_pv_uv_time(String[][] data_pv_uv_time) {
        this.data_pv_uv_time = data_pv_uv_time;
    }

    public String[][] getData_pv_uv_sum() {
        return data_pv_uv_sum;
    }

    public void setData_pv_uv_sum(String[][] data_pv_uv_sum) {
        this.data_pv_uv_sum = data_pv_uv_sum;
    }

    public String getPkg_sources_default() {
        return pkg_sources_default;
    }

    public void setPkg_sources_default(String pkg_sources_default) {
        this.pkg_sources_default = pkg_sources_default;
    }

    public String getData_sources_default() {
        return data_sources_default;
    }

    public void setData_sources_default(String data_sources_default) {
        this.data_sources_default = data_sources_default;
    }

    public String getEvent_value_name_default() {
        return event_value_name_default;
    }

    public void setEvent_value_name_default(String event_value_name_default) {
        this.event_value_name_default = event_value_name_default;
    }

    public String getDim_default() {
        return dim_default;
    }

    public void setDim_default(String dim_default) {
        this.dim_default = dim_default;
    }

    public void setTime(String[] time) {
        this.time = time;
    }

    //返回的Json测试
    public static void main(String[] args) {
        DataAnalyzeResult result = new DataAnalyzeResult(null, "1");
        //数据源
        Map<String, String> pkg_sources = new HashMap<String, String>();
        pkg_sources.put(CommonConst.SOURCE_ALL, CommonConst.SOURCE_ALL_NAME);
        pkg_sources.put(CommonConst.SOURCE_IQIYI, CommonConst.SOURCE_IQIYI_NAME);
        pkg_sources.put(CommonConst.SOURCE_TENCENT, CommonConst.SOURCE_TENCENT_NAME);
        result.setPkg_sources(pkg_sources);
        //产品模块
        Map<String, String> data_sources = new HashMap<String, String>();
        data_sources.put(CommonConst.CC_HOMEPAGE6, CommonConst.CC_HOMEPAGE6_NAME);
        data_sources.put(CommonConst.CC_VIDEO, CommonConst.CC_VIDEO_NAME);
        result.setData_sources(data_sources);
        //事件名称
        Map<String, String> event_value_name = new HashMap<String, String>();
        event_value_name.put(InitConst.INIT_EVENT, InitConst.INIT_EVENT_NAME);//默认选择
        event_value_name.put("'searc_hotList_AD_click'", "【大家都在搜】页面推荐的广告点击事件");
        event_value_name.put("'cashier_first_button_clicked'", "收银台-一级按钮-点击");
        event_value_name.put("'cashier_second_button_clicked'", "收银台-二级按钮-点击");
        event_value_name.put("'avoid_button_clicked'", "已开通免密界面-按钮-点击");
        event_value_name.put("'goldvip_button_clicked'", "有赠送黄金VIP的支付成功界面--按钮-点击");
        event_value_name.put("'exit_pay'", "退出支付界面");
        result.setEvent_value_name(event_value_name);
        /*筛选条件*/
        //key：筛选条件维度英文值（即base_clog_view的列名/维度名）；value:筛选条件（中文名）
        Map<String, String> condition_col_name = new HashMap<String, String>();
        condition_col_name.put("brand", "设备品牌");
        condition_col_name.put("model", "机型");
        result.setCondition_col_name(condition_col_name);
        //key：筛选条件维度英文值（即base_clog_view的列名/维度名）；
        //value：维度对应的值（去重后的值）
        Map<String, List<String>> condition_col_value = new HashMap<String, List<String>>();
        List<String> one = new ArrayList<String>();
        one.add("skyworth");
        one.add("SmartCore");
        condition_col_value.put("brand", one);
        List<String> two = new ArrayList<String>();
        two.add("i71S");
        two.add("14K");
        condition_col_value.put("model", two);
        result.setCondition_col_value(condition_col_value);
        /*细分属性（6大维度）*/
        //key：列维度名（列英文名）；value：列中文名；
        Map<String, String> dim = new HashMap<String, String>();
        dim.put(CommonConst.BRAND, CommonConst.BRAND_NAME);
        dim.put(CommonConst.MODEL, CommonConst.MODEL_NAME);
        dim.put(CommonConst.OS_VERCODE, CommonConst.OS_VERCODE_NAME);
        dim.put(CommonConst.PKG_NAME, CommonConst.PKG_NAME_NAME);
        dim.put(CommonConst.PKG_VERNAME, CommonConst.PKG_VERNAME_NAME);
        dim.put(CommonConst.UID, CommonConst.UID_NAME);
        result.setDim(dim);
        //指定细分属性对应的数据库所有值
        String[] dim_values = new String[]{"skyworth", "SmartCore"};
        result.setDim_values(dim_values);

        /*准备数据集*/
        //pv数据集
        String[][] data_pv = new String[][]{
                {"time", "pv"},
                {"20170102", "100"},
                {"20170103", "200"},
                {"20170104", "300"},
                {"20170105", "400"}
        };
        result.setData_pv(data_pv);
        //uv数据集
        String[][] data_uv = new String[][]{
                {"time", "uv"},
                {"2017-01-02 10:00:00", "100"},
                {"20170102 11:00:00", "200"},
                {"20170102 12:00:00", "300"},
                {"20170102 13:00:00", "400"}
        };
        result.setData_uv(data_uv);
        //pv_uv按照时间间隔进行分组
        String[][] data_pv_uv_time = new String[][]{
                {"time", "dim", "pv", "uv"},
                {"201701", "skyworth", "100", "50"},
                {"201702", "SmartCore", "200", "100"},
                {"201703", "coocaa", "600", "300"},
                {"201704", "songxia", "300", "150"}
        };

        result.setData_pv_uv_time(data_pv_uv_time);
        //pv_uv按照时间间隔进行汇总
        String[][] data_pv_uv_sum = new String[][]{
                {"dim", "pv", "uv"},
                {"skyworth", "100", "50"},
                {"SmartCore", "200", "100"},
                {"coocaa", "600", "300"},
                {"songxia", "300", "150"}
        };
        result.setData_pv_uv_sum(data_pv_uv_sum);

        String json = JSON.toJSONString(result);
        System.out.println(json);
    }

    public List<String> getPage_name() {
        return page_name;
    }

    public void setPage_name(List<String> page_name) {
        this.page_name = page_name;
    }

}
