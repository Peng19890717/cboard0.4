package org.cboard.services.sec;

import org.cboard.pojo.sec.DashboardFunnel;

import java.util.List;
import java.util.Map;

/**
 * Created by hankl on 2017/6/1.
 * 漏斗分析的服务层
 */
public interface FunnelService {

    Map<Integer, String> getFunnelAnalysisData();//获取到关于漏斗分析的数据

    Map<Integer, String> getFunnelAnalysisVideoData();

    int saveFunnelData(List<DashboardFunnel> funnelList);

    Integer selectMaxPfp();//获取到最大的标示符

}