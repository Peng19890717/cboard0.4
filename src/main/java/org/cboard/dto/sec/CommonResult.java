package org.cboard.dto.sec;/**
 * Created by Administrator on 2017/5/31 0031.
 */

import java.io.Serializable;

/**
 * 结果集的父类，用于存储错误信息和标识符
 * @author wbc
 * @date 2017-05-31
 **/
public class CommonResult implements Serializable {
    //取BackResultConst的值
    private String msg;//标识符：值=1时表示数据正常;fail表示异常数据
    private String errorMsg;//异常的详细信息

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

}
