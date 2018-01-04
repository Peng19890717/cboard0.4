package org.cboard.consts;
/**
 * Created by Administrator on 2017/5/10 0010.
 */

import java.util.HashMap;
import java.util.Map;

/**
 * 独立应用包名、中文名对照容器
 * @author wbc
 * @date 2017-05-10
 **/
public interface PkgNameConst {

    //独立应用包名、中文名对照容器
    //key：独立应用包名
    //value：独立应用中文名
    Map<String,String> PKG_NAME_MAP = new HashMap<String,String>(){
        //初始化数据
        {
            this.put("com.tianci.movieplatform","影视中心/主页");
            this.put("com.coocaa.mall","购物商城");
            this.put("com.tianci.appstore","应用圈");
            this.put("com.coocaa.sky.ccapi","支付模块（独立模块）");
            this.put("com..tianci.eduplatform","教育中心");
        }
    };



}
