package org.cboard.daokylin;


import org.apache.ibatis.annotations.Param;
import org.cboard.dto.sec.FunnelCube;
import org.cboard.dto.sec.PathCube;
import org.cboard.pojo.sec.DashboardFunnel;
import org.cboard.pojo.sec.StepFather;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 路径分析，漏斗分析持久层
 * （二次开发）
 *
 * @author wbc
 * @date 2017-05-03
 **/
@Repository
public interface KylinDao {

    /**
     * 漏斗只获取uv
     * 从kylin中获取漏斗数据（有两个字段，一个为名称（滤事件英文名+页面中文名  或者  只有事件英文名 ），另一个为相应的uv值）
     * <p>
     * 如果过滤事件英文名，则sql的样式为：
     * select count( distinct deviceid ) val ,
     * event_name
     * from sessionlog6
     * where
     * 下面过滤事件中文名和产品模块
     * ——( event_name = ''  and data_source = '') or
     * ——( event_name = ''  and data_source = '' ) or
     * ——( event_name = ''  and data_source = '' )
     * 这里过滤公共维度：视频源（本期先不过滤）、产品模块
     * group by event_name
     * <p>
     * <p>
     * 如果过滤事件英文名+页面中文名，则sql的样式为： <p>
     * select
     * ——count( distinct deviceid ) val ,
     * ——concat( concat(event_name,'--' ), page_name ) event_name
     * from session_log6
     * where
     * 这里过滤公共维度：视频源（本期先不过滤）
     * 下面过滤过滤事件中文名、页面中文名称和产品模块
     * ——( event_name = ''  and page_name = '' and data_source = '') or
     * ——( event_name = ''  and page_name = '' and data_source = '' ) or
     * ——( event_name = ''  and page_name = '' and data_source = '' )
     * group by  concat( concat(event_name,'--' ), page_name )
     *
     * @param flag       true：表示过滤事件英文名+页面中文名；false：只过滤事件英文名；
     * @param datasource 数据源
     * @param start_time 开始时间 这种格式 9999-99-99
     * @param end_time   结束时间 这种格式 9999-99-99
     * @param list       数据集（存放事件英文名、页面中文名）
     * @return
     */
    List<FunnelCube> getFunnelDatas(
            @Param("flag") boolean flag,
            @Param("datasource") String datasource,
            @Param("start_time") String start_time,
            @Param("end_time") String end_time,
            @Param("list") List<DashboardFunnel> list);

    /**
     * 从kylin中获取数据
     *
     * @param condition  过滤情况（一共有4种）
     * @param npv_uv     统计的是pv还是uv（1=pv，2=uv）
     * @param datasource 数据源
     * @param start_time 开始时间 这种格式 9999-99-99
     * @param end_time   结束时间 这种格式 9999-99-99
     * @param list       存放过滤条件（只会有一种，即PathCondition枚举类里面的任意一种）
     * @return
     */
    List<PathCube> getPathDatas(
            @Param("condition") Integer condition,
            @Param("npv_uv") Integer npv_uv,
            @Param("datasource") String datasource,
            @Param("start_time") String start_time,
            @Param("end_time") String end_time,
            @Param("list") List<StepFather> list
    );

}
