package org.cboard.pojo.sec;

/**
 * Created by Administrator on 2017/6/8 0008.
 */

import org.cboard.consts.PathCondition;

import java.io.Serializable;

/**
 * 对应Cboard数据库的dashboard_doc和dashboard_doc_father表的数据
 * 主要用于存放这种数据，记录自己是谁、父亲是谁
 *
 * @author wbc
 * @date 2017-06-08
 **/
public class StepFather implements Serializable {

    private Integer doc_id;//对应 dashboard_doc的行主键（对应第几步，通过该主键可获取）

    private Integer npv_uv;//统计的是pv还是uv（1=pv，2=uv）
    private String vdata_source;//数据源

    private String vpro_module;//产品模块的英文名
    private String vlog_name;//事件英文名
    private String vpage_name;//页面名称（中文名）

    private String vparent_pro_module;//父产品模块的英文名
    private String vparent_log_name;//父事件英文名
    private String vparent_page_name;//父页面名称（中文名）

    /**
     * 判断这个vo属于哪个类别（看枚举类PathCondition）
     *
     * @return
     */
    public PathCondition judgeClass() {
        //第一类：没有页面名称  也没有  父页面名称
        if ((vpage_name == null || vpage_name.trim().length() <= 0) &&
                (vparent_page_name == null || vparent_page_name.trim().length() <= 0)) {
            return PathCondition.FISRT;
        /*第二类：没有页面名称    有    父页面名称*/
        } else if ((vpage_name == null || vpage_name.trim().length() <= 0) &&
                (vparent_page_name != null && vparent_page_name.trim().length() > 0)) {
            return PathCondition.SECOND;
        /*第三类：有页面名称    没有    父页面名称*/
        } else if ((vpage_name != null && vpage_name.trim().length() > 0) &&
                (vparent_page_name == null || vparent_page_name.trim().length() <= 0)) {
            return PathCondition.THIRD;
        /*第四类：有页面名称    有    父页面名称*/
        } else {
            return PathCondition.FORTH;
        }
    }

    public Integer getNpv_uv() {
        return npv_uv;
    }

    public void setNpv_uv(Integer npv_uv) {
        this.npv_uv = npv_uv;
    }

    public String getVdata_source() {
        return vdata_source;
    }

    public void setVdata_source(String vdata_source) {
        this.vdata_source = vdata_source;
    }

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

    public Integer getDoc_id() {
        return doc_id;
    }

    public void setDoc_id(Integer doc_id) {
        this.doc_id = doc_id;
    }

    public String getVparent_pro_module() {
        return vparent_pro_module;
    }

    public void setVparent_pro_module(String vparent_pro_module) {
        this.vparent_pro_module = vparent_pro_module;
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

}
