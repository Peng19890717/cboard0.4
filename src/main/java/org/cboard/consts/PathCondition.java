package org.cboard.consts;
/**
 * Created by Administrator on 2017/6/9 0009.
 */

/**
 * 查询漏斗时的4中过滤情况
 *
 * @author wbc
 * @date 2017-06-09
 **/
public enum PathCondition {

    FISRT("FISRT", 1),  //情况一：自己是产品模块+事件英文名称；           爸爸是父产品模块+父事件英文名称
    SECOND("SECOND", 2), //情况二：自己是产品模块+事件英文名称；           爸爸是父产品模块+父事件英文名称+父页面中文名
    THIRD("THIRD", 3),  //情况三：自己是产品模块+事件英文名称+页面中文名；爸爸是父产品模块+父事件英文名称
    FORTH("FORTH", 4) ;  //情况四：自己是产品模块+事件英文名称+页面中文名；爸爸是父产品模块+父事件英文名称+父页面中文名 ;

    private String name;
    private Integer index;

    PathCondition(String name, Integer index) {
        this.name = name;
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }
}

