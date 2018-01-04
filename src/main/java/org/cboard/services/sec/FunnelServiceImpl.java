package org.cboard.services.sec;

import org.cboard.daocboard.DocDao;
import org.cboard.pojo.sec.DashboardFunnel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by hankl on 2017/6/1.
 */
@Service
public class FunnelServiceImpl implements FunnelService {
    @Autowired
    private DocDao docDao;

    @Override
    public Map<Integer, String> getFunnelAnalysisData() {

        Map<Integer, String> map = new HashMap<>();

        List<Map<String, Object>> data = docDao.getFunnelAnalysisData();
        if (data != null && data.size() > 0) {
            for (Map<String, Object> mapData : data) {
                Integer pk_funnel_path = (Integer) mapData.get("pk_funnel_path");//每个漏斗的唯一标识
                String vname = (String) mapData.get("vname");
                map.put(pk_funnel_path, vname);//id为键，vname漏斗对应的名称为值返回map.
            }
        }
        return map;
    }


    @Override
    public Map<Integer, String> getFunnelAnalysisVideoData() {
        Map<Integer, String> map = new HashMap<>();

        List<Map<String, Object>> data = docDao.getFunnelAnalysisVideoData();
        if (data != null && data.size() > 0) {
            for (Map<String, Object> mapData : data) {
                Integer pk_funnel_path = (Integer) mapData.get("pk_funnel_path");//每个漏斗的唯一标识
                String vdata_source = (String) mapData.get("vdata_source");
                map.put(pk_funnel_path, vdata_source);//id为键，vdata_source视频源的名称为值返回map.
            }
        }
        return map;
    }




    @Override
    public int saveFunnelData(List<DashboardFunnel> funnelList) {
        return docDao.saveFunnelData(funnelList);
    }
    @Override
    public Integer selectMaxPfp() {
        Integer maxPfp = docDao.selectMaxPfp();
        return maxPfp == null ? 1 : maxPfp + 1;
    }


}
