package org.cboard.pojo.sec;


import java.io.Serializable;

/**
 * 存储路径/漏斗信息
 *
 * @author wbc
 * @date 2017-05-03
 **/
public class DashboardFunnel implements Serializable{

    private Integer id;//行主键
    private Integer pk_funnel_path;//漏斗/路径的唯一标识符
    private Integer nfunnel_path;//用于区分是漏斗，还是路径
    private String vname;//名称（漏斗/路径名称）
    private String vdata_source;//数据源（yinhe、tencent）
    private String nstep;//第几步
    private String vpro_module;//产品模块的英文名（kylin也存储的是英文名）
    private String vlog_name;//事件英文名
    private String vpage_name;//页面名称（中文名）==》这个字段的值可以没有
    private String vparent_log_name;//父事件英文名（存储多个事件的英文名中间用逗号隔开）
    private String vparent_page_name;//父页面名称（中文名）==》这个字段的值可以没有（存储多个父页面中文名，中间用逗号隔开）
    private Integer npv_uv;//统计的是pv还是uv（为路径分析服务）

    public String getVpro_module() {
        return vpro_module;
    }

    public void setVpro_module(String vpro_module) {
        this.vpro_module = vpro_module;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPk_funnel_path() {
        return pk_funnel_path;
    }

    public void setPk_funnel_path(Integer pk_funnel_path) {
        this.pk_funnel_path = pk_funnel_path;
    }

    public Integer getNfunnel_path() {
        return nfunnel_path;
    }

    public void setNfunnel_path(Integer nfunnel_path) {
        this.nfunnel_path = nfunnel_path;
    }

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

    public String getNstep() {
        return nstep;
    }

    public void setNstep(String nstep) {
        this.nstep = nstep;
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

    public String getVparent_log_name() {
        return vparent_log_name;
    }

    public void setVparent_log_name(String vparent_log_name) {
        this.vparent_log_name = vparent_log_name;
    }

    public String getVparent_page_name() {
        return vparent_page_name;
    }

    public void setVparent_page_name(String vparent_page_name) {
        this.vparent_page_name = vparent_page_name;
    }

    public Integer getNpv_uv() {
        return npv_uv;
    }

    public void setNpv_uv(Integer npv_uv) {
        this.npv_uv = npv_uv;
    }



}
