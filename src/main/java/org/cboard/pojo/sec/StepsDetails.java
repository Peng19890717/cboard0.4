package org.cboard.pojo.sec;

/**
 * Created by hankl on 2017/6/4.
 * 用来接收steps里的数据信息。
 */
public class StepsDetails {

    private String vpro_module;//产品模块
    private String vlog_name;//事件
    private String vpage_name;//页面名称

    public String getVpro_module() {
        return vpro_module;
    }

    public void setVpro_module(String vpro_module) {
        this.vpro_module = vpro_module;
    }

    public String getVlog_name() {
        return vlog_name;
    }

    public void setVlog_name(String vlog_name) {
        this.vlog_name = vlog_name;
    }

    public String getVpage_name() {
        return vpage_name;
    }

    public void setVpage_name(String vpage_name) {
        this.vpage_name = vpage_name;
    }




}
