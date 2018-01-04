package org.cboard.dto.sec;
/**
 * Created by Administrator on 2017/6/9 0009.
 */

import java.io.Serializable;

/**
 * 存放路径最小单元的数据（该数据是从kylin中获取）
 *
 * @author wbc
 * @date 2017-06-09
 **/
public class PathCube implements Serializable{

    private String target;//自己
    private String source;//父亲
    private String val;//值

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

}
