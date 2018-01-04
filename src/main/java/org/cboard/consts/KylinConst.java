package org.cboard.consts;

/**
 * Created by Administrator on 2017/5/9 0009.
 */

/**
 * 存储Kylin的信息
 */
public interface KylinConst {
    /*下面是base_clog_analysis对应的信息*/
    String KYLIN_TABLE_NAME = "base_clog_analysis";//事实表表名
    String KYLIN_TIME = "dim_date_comm";//时间对应的表名

    String PKG_SOURCE="pkg_source";//数据源列名
    String DATA_SOURCE="data_source";//产品模块列名
    String DATA_SOURCE_TYPE="data_source_type";//事件名称列名

    /*下面是sessionlog6对应的信息*/
    String SOURCE = "data_source";//产品模块字段
    String VIDEO_SOURCE = "pkg_source";//视频源字段
    String KYLIN_FUNNEL="sessionlog6";//漏斗图取数的字段

}
