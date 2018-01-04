package org.cboard.consts;

/**
 * Created by Administrator on 2017/5/12 0012.
 */

/**
 * 提供拼接sql的一些常量
 */
public interface SqlConst {

    String SELECT = " select ";
    String GROUP_BY = " group by ";
    String ORDER_BY = " order by ";
    //统计pv
    String PV = " count(" + KylinConst.KYLIN_TABLE_NAME + ".deviceid) pv ";
    //统计uv
    String UV = " count(distinct(" + KylinConst.KYLIN_TABLE_NAME + ".deviceid)) uv ";
    //统计pv+uv
    String PV_UV = " count(" + KylinConst.KYLIN_TABLE_NAME + ".deviceid) pv , " +
            "count(distinct(" + KylinConst.KYLIN_TABLE_NAME + ".deviceid)) uv ";
    //统计的时间维度（时、日、周、月）
    //下面4个属性可以放在select 、group by 、order后面
    String SELECT_HOUR = " " + KylinConst.KYLIN_TABLE_NAME + ".update_time  ";
    String SELECT_DAY = " " + KylinConst.KYLIN_TABLE_NAME + ".update_date ";
    String SELECT_WEEK = " " + KylinConst.KYLIN_TIME + ".week_year ," + KylinConst.KYLIN_TIME + ".year_month ";
    String SELECT_MONTH = " " + KylinConst.KYLIN_TIME + ".year_month ";
    //过滤时间时用到的字段
    String FILTER_TIME = " " + KylinConst.KYLIN_TABLE_NAME + ".update_time  ";
    String FROM =
            " from " + KylinConst.KYLIN_TABLE_NAME + " " +
                    " left join " + KylinConst.KYLIN_TIME + " " +
                    " on " + KylinConst.KYLIN_TABLE_NAME + ".update_date = " + KylinConst.KYLIN_TIME + ".comm_date ";

    

}