package org.cboard.consts;
/**
 * Created by Administrator on 2017/5/11 0011.
 */


/**
 * 普通的常量容器
 *
 * @author wbc
 * @date 2017-05-11
 **/
public interface CommonConst {
    //时间对应的维度列名
    String TIME_DIM="update_time";//过滤的话只能用between...and...
    //事件名称对应的kylin的base_clog_analysis维度列名
    String EVENT_DIM="data_source_type";

    String START_TIME_TAIL="00:00:00";
    String END_TIME_TAIL="23:59:59";

    //时间间隔
    String HOUR = "时";
    String DAY = "日";
    String WEEK = "周";
    String MONTH = "月";

    //数据源
    String DATASOURCE="pkg_source";//数据源对应的kylin的base_clog_analysis维度列名
    String SOURCE_IQIYI = "'yinhe'";
    String SOURCE_IQIYI_NAME = "爱奇艺";
    String SOURCE_TENCENT = "'tencent'";
    String SOURCE_TENCENT_NAME = "腾讯";
    String SOURCE_ALL = "('yinhe','tencent')";
    String SOURCE_ALL_NAME = "全部";

    //产品模块
    String DIM_PRO="data_source";//产品模块对应的kylin的base_clog_analysis维度列名
    String CC_HOMEPAGE6 = "'CC_HomePage6'";//6.0首页（数据库值）
    String CC_HOMEPAGE6_NAME = "6.0首页";//中文名
    String CC_VIDEO = "'CC_Video_6.0'";//6.0影视中心
    String CC_VIDEO_NAME = "6.0影视中心";

    //细分属性6大公共维度（字段名+对应的中文名）
    String BRAND = "brand";
    String BRAND_NAME = "设备品牌";
    String MODEL = "model";
    String MODEL_NAME = "机型";
    String OS_VERCODE = "os_vername";
    String OS_VERCODE_NAME = "系统版本号";
    String PKG_NAME = "pkg_name";
    String PKG_NAME_NAME = "独立应用名称";
    String PKG_VERNAME = "pkg_vername";
    String PKG_VERNAME_NAME = "独立应用版本";
    String UID = "uid";
    String UID_NAME = "登录状态";
//    hkl  增加 6.3
    Integer NFUNNEL_PATH_FUNNEL=1;//1 代表的是漏斗
    String STR[] = {"爱奇艺","腾讯"};//这里是视频源的信息。


}