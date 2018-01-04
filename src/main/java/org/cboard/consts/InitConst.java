package org.cboard.consts;
/**
 * Created by Administrator on 2017/5/9 0009.
 */

import org.cboard.controller.sec.SecondController;
import org.cboard.dto.sec.DataAnalyzeResult;
import org.cboard.tool.sec.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 时间分析时初始化界面时的查询条件
 * 返回值有：视频源、产品模块、事件名称、筛选条件、筛选条件对应的值、细分属性
 * 注意：
 * 视频源：全部（默认显示）、爱奇艺、腾讯
 * 产品模块：6.0影视中心（默认显示）、6.0首页
 * 事件名称：影视首页-单个推荐位曝光事件（默认显示，对应的日志英文名
 * ————为’recommend_poster_exposure’），显示6.0影视中心对应的
 * ————所有事件名称
 * 细分属性：设备品牌（默认显示）、机型、系统版本号、独立应用名称、
 * ————独立应用版本、登录状态
 * 筛选条件：默认什么都不显示
 * ————但是要给6.0影视中心+影视首页-单个推荐位曝光事件对应的筛选条件和条件值
 * <p>
 * 时间间断： 时（默认显示）、日、周、月
 * 时间段：  开始时间+结束时间，这种形式9999-99-99（eg：2017-05-01）
 * 细分属性：设备品牌对应的所有细分属性
 * ————从10数据库mysql的bi_cboard的表中取
 * ————select distinct dim_column_value //细分属性对应的值
 * ————from dashboard_enum
 * ————where dim_column = 'brand'
 **/
@Component
public class InitConst {

    @Autowired
    private SecondController secondcontroller;
    /*视频源*/
    //key：数据库值；value：中文名；
    private Map<String, String> source_all_name_value = null;
    /*产品模块*/
    //key：数据库值；value：中文名；
    private Map<String, String> pro_all_name_value = null;
    private String pro_default = CommonConst.CC_VIDEO;//默认显示6.0影视中心
    /*事件名称（从数据库中获取）*/
    //key：数据库值；value：中文名；
    private Map<String, String> event_name_value = null;
    /*筛选条件（从数据库中获取，受到产品模块+事件名称的影响）*/
//    //初始化时时间名称选择【影视首页-单个推荐位曝光事件】（这是6.0影视中心的事件）
//    public static final String INIT_EVENT = "'recommend_poster_exposure'";
//    public static final String INIT_EVENT_NAME = "影视首页-单个推荐位曝光事件";

    public static final String INIT_EVENT = "'detail_recommand_clicked'";
    public static final String INIT_EVENT_NAME = "详情页-推荐位影片点击";

    //key：维度英文名（对应kylin的base_clog_analysis的列）
    //value：维度对应的数据库值（去重后的值）
    private Map<String, List<String>> condition_values = null;
    //key：维度英文名；value：维度中文名；
    private Map<String, String> condition_name_value = null;
    /*细分属性（固定的，就只有6个维度，对应kylin的base_clog_analysis的6个列）*/
    //key：细分属性——字段名称（对应kylin的base_clog_analysis的列）
    //value：细分属性——中文名
    private Map<String, String> public_dim = null;
    /*初始化时，细分属性默认选择的是设备品牌*/
    public static final String INIT_DIM_ENAME = "brand";//列名
    public static final String INIT_DIM_CNAME = "设备品牌";//中文名
    //时间间断（只有4个值：时、日、周、月）
    private List<String> period = null;
    /*初始化查询条件*/
    //key：列名；value：列对应的值
    private Map<String, String> init_values = null;
    //key：列名；value：列对应的逻辑运算符（LogicalConst这里有）；
    private Map<String, String> init_relations = null;

    /**
     * 获取初始化查询条件
     * key：列名；value：列对应的值
     *
     * @return
     */
    public synchronized Map<String, String> getInit_values() {
        if (init_values == null || init_values.size() <= 0) {
            initQueryConditions();
        }
        return init_values;
    }

    /**
     * 获取初始化查询条件
     * key：列名；value：列对应的逻辑运算符（LogicalConst这里有）；
     *
     * @return
     */
    public Map<String, String> getInit_relations() {
        if (init_relations == null || init_relations.size() <= 0) {
            initQueryConditions();
        }
        return init_relations;
    }

    private void initQueryConditions() {
        init_values = new HashMap<String, String>();
        init_relations = new HashMap<String, String>();
//        //数据源（全部）
//        init_values.put(CommonConst.DATASOURCE, CommonConst.SOURCE_ALL);
//        init_relations.put(CommonConst.DATASOURCE, LogicalConst.IN);
        //产品模块（6.0影视中心）
        init_values.put(CommonConst.DIM_PRO, CommonConst.CC_VIDEO);
        init_relations.put(CommonConst.DIM_PRO, LogicalConst.EQUALS);
        //事件名称
        init_values.put(CommonConst.EVENT_DIM, INIT_EVENT);
        init_relations.put(CommonConst.EVENT_DIM, LogicalConst.EQUALS);
        //筛选条件（不用过滤）
        //细分属性（不用过滤）
    }

    /**
     * 获取数据源
     *
     * @return
     */
    public synchronized Map<String, String> getSource_all_name_value() {
        if (source_all_name_value == null || source_all_name_value.size() <= 0) {
            //初始化
            source_all_name_value = new HashMap<String, String>();
            source_all_name_value.put(CommonConst.SOURCE_ALL, CommonConst.SOURCE_ALL_NAME);//默认显示全部
            source_all_name_value.put(CommonConst.SOURCE_IQIYI, CommonConst.SOURCE_IQIYI_NAME);
            source_all_name_value.put(CommonConst.SOURCE_TENCENT, CommonConst.SOURCE_TENCENT_NAME);
        }
        return source_all_name_value;
    }

    /**
     * 获取产品模块
     *
     * @return
     */
    public synchronized Map<String, String> getPro_all_name_value() {
        if (pro_all_name_value == null || pro_all_name_value.size() <= 0) {
            //初始化
            pro_all_name_value = new HashMap<String, String>();
            pro_all_name_value.put(CommonConst.CC_HOMEPAGE6, CommonConst.CC_HOMEPAGE6_NAME);
            pro_all_name_value.put(CommonConst.CC_VIDEO, CommonConst.CC_VIDEO_NAME);//6.0影视中心（默认显示）
        }
        return pro_all_name_value;
    }

    /**
     * 获取6.0影视中心（产品模块）的所有事件名称（通过CommonConst.CC_VIDEO）
     *
     * @return key：数据库值；value：中文名；
     */
    public synchronized Map<String, String> getEvent_name_value() {
        if (event_name_value == null || event_name_value.size() <= 0) {
            //初始化（通过产品id获取事件名称）
            //初始化时，获取的是6.0影视中心的所有事件
            DataAnalyzeResult dataAnalyzeResult = secondcontroller.getLinkageDatas(CommonConst.CC_VIDEO, null, true);
            event_name_value = dataAnalyzeResult.getEvent_value_name();
        }
        return event_name_value;
    }

    /**
     * 获取筛选条件维度对照
     *
     * @return key：维度英文名；value：维度中文名；
     */
    public synchronized Map<String, List<String>> getCondition_values() {
        if (condition_values == null || condition_values.size() <= 0) {
            //初始化
            this.initConditions();
        }
        return condition_values;
    }

    /**
     * 获取筛选条件对应的值
     *
     * @return key：维度英文名（对应kylin的base_clog_analysis的列）；value：维度对应的数据库值（去重后的值）
     */
    public Map<String, String> getCondition_name_value() {
        if (condition_name_value == null || condition_name_value.size() <= 0) {
            //初始化
            this.initConditions();
        }
        return condition_name_value;
    }

    //初始化筛选条件（condition_values、condition_name_value）
    private void initConditions() {
        //获取6.0影视中心（产品id）、影视首页-单个推荐位曝光事件（事件名称）的筛选维度和其对应的过滤条件
        DataAnalyzeResult dataAnalyzeResult = secondcontroller.getLinkageDatas(CommonConst.CC_VIDEO, INIT_EVENT, true);
        condition_name_value = dataAnalyzeResult.getCondition_col_name();
        condition_values = dataAnalyzeResult.getCondition_col_value();
    }

    /**
     * 获取细分属性
     *
     * @return
     */
    public Map<String, String> getPublic_dim() {
        if (public_dim == null || public_dim.size() <= 0) {
            //初始化
            public_dim = new HashMap<String, String>();
            public_dim.put(CommonConst.BRAND, CommonConst.BRAND_NAME);//设备品牌（默认显示）
            public_dim.put(CommonConst.MODEL, CommonConst.MODEL_NAME);//机型
            public_dim.put(CommonConst.OS_VERCODE, CommonConst.OS_VERCODE_NAME);//系统版本号
            public_dim.put(CommonConst.PKG_NAME, CommonConst.PKG_NAME_NAME);//独立应用名称
            public_dim.put(CommonConst.PKG_VERNAME, CommonConst.PKG_VERNAME_NAME);//独立应用版本
//            public_dim.put(CommonConst.UID, CommonConst.UID_NAME);//登录状态
        }
        return public_dim;
    }

    /**
     * 获取细分属性的过滤条件值（初始化时，获取设备品牌对应所有过滤条件值）
     *
     * @return
     */
    public List<String> getBrandCondition() {
        return getCondition_values().get(CommonConst.BRAND);
    }

    /**
     * 获取时间间断（只有4个值：时、日、周、月）
     *
     * @return
     */
    public List<String> getPeriod() {
        if (period == null || period.size() <= 0) {
            //初始化
            period = new ArrayList<String>();
            period.add(CommonConst.HOUR);//时
            period.add(CommonConst.DAY);
            period.add(CommonConst.WEEK);
            period.add(CommonConst.MONTH);
        }
        return period;
    }

    /**
     * 获取时间段（默认是昨天，实时获取）
     * ——0位置，开始时间==》这种形式9999-99-99 99:99:99（eg：2017-05-01 00:00:00）
     * ——1位置，结束时间==》这种形式9999-99-99 99:99:99（eg：2017-05-01 23:59:59）
     *
     * @return
     */
    public String[] getPeriods() {
        String[] back = new String[2];
        String yesterday = DateUtil.getYesterday(DateUtil.DATE_FORMAT);
        back[0] = yesterday + " 00:00:00";//开始时间（昨天）
        back[1] = yesterday + " 23:59:59";//结束时间（昨天）
        return back;
    }


}
