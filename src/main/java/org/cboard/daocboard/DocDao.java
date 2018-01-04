package org.cboard.daocboard;

/**
 * Created by Administrator on 2017/5/31 0031.
 */

import org.apache.ibatis.annotations.Param;
import org.cboard.pojo.sec.DashboardFunnel;
import org.cboard.pojo.sec.StepFather;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 漏斗/路径档案的查询接口
 */
@Repository
public interface DocDao {
    //通过漏斗/路径主键获取档案数据
    //一个漏斗/路径对应多条数据
    List<DashboardFunnel> getOneDoc(@Param("pk_funnel_path") int pk_funnel_path);

    List<Map<String, Object>> getFunnelAnalysisData();

    List<Map<String, Object>> getFunnelAnalysisVideoData();


    int saveFunnelData(@Param("funnelList") List<DashboardFunnel> funnelList);//保存漏斗分析的数据到数据库里。

    Integer selectMaxPfp();//返回数据库里最大的漏斗的唯一标示符

    /**
     * 通过漏斗主键获取数据
     * 获取的信息包括：
     * ——统计的是pv还是uv、数据源、（基本信息）
     * ——产品模块英文名、事件英文名、页面中文名称、（自己的条件）
     * ——父产品模块英文名、父事件英文名、父页面中文名称（爸爸的条件）
     *
     * @param pk_funnel_path
     * @return
     */
    List<StepFather> getDetails(@Param("pk_funnel_path") Integer pk_funnel_path);

}
