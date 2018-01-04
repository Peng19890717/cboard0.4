package org.cboard.dto.sec;

/**
 * Created by Administrator on 2017/5/31 0031.
 */

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * 存放漏斗的最小单元的数据
 *
 * @author wbc
 * @date 2017-05-31
 **/
public class FunnelCube implements Serializable {

    private String event_name;//这两种形式：事件名称；事件名称--页面名称
    private String val;

    public FunnelCube() {}

    public FunnelCube(String event_name, String val) {
        this.event_name = event_name;
        this.val = val;
    }

    public String getEvent_name() {
        return event_name;
    }

    public void setEvent_name(String event_name) {
        this.event_name = event_name;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public static void main(String[] args) {
        FunnelCube c1 = new FunnelCube("事件名称1", "值1");
        FunnelCube c2 = new FunnelCube("事件名称2", "值2");
        FunnelCube c3 = new FunnelCube("事件名称3", "值3");
        FunnelCube[] result = new FunnelCube[3];
        result[0] = c1;
        result[1] = c2;
        result[2] = c3;
        FigureResult result1 = new FigureResult();
        result1.setDatas(result);
        System.out.println(JSON.toJSONString(result1));
    }

}
