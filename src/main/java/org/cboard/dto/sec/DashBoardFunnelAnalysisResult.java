package org.cboard.dto.sec;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hankl on 2017/6/5.
 * 此类封装的是返回给前端的两个map
 */
public class DashBoardFunnelAnalysisResult extends CommonResult {
      //返回个前端的数据封装在两个map里
        Map<Integer ,String> mapData=new HashMap<>();
        Map<Integer,String> mapVideo=new HashMap<>();

    public Map<Integer, String> getMapData() {
        return mapData;
    }

    public void setMapData(Map<Integer, String> mapData) {
        this.mapData = mapData;
    }

    public Map<Integer, String> getMapVideo() {
        return mapVideo;
    }

    public void setMapVideo(Map<Integer, String> mapVideo) {
        this.mapVideo = mapVideo;
    }
}
