package org.cboard.pojo.sec;

import java.util.ArrayList;

/**
 * Created by hankl on 2017/6/3.
 */
public class DashBoardFunnelDetail {
    private String vname;//名称（漏斗/路径名称）
    private String vdata_source;//数据源（yinhe、tencent）
    private ArrayList<Integer> npv_uv;//统计的是pv还是uv
    private ArrayList<StepsDetails> steps;//保留步数的List集合;

    public String getVname() {
        return vname;
    }

    public void setVname(String vname) {
        this.vname = vname;
    }

    public String getVdata_source() {
        return vdata_source;
    }

    public void setVdata_source(String vdata_source) {
        this.vdata_source = vdata_source;
    }

    public ArrayList<Integer> getNpv_uv() {
        return npv_uv;
    }

    public void setNpv_uv(ArrayList<Integer> npv_uv) {
        this.npv_uv = npv_uv;
    }

    public ArrayList<StepsDetails> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<StepsDetails> steps) {
        this.steps = steps;
    }
}
