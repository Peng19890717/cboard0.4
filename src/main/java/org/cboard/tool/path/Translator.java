package org.cboard.tool.path;
/**
 * Created by Administrator on 2017/6/14 0014.
 */

import java.util.Map;

/**
 * 该工具类用于数据转义
 * eg：
 * 产品模块英文名转为相应中文名
 * 事件分析...
 *
 * @author wbc
 * @date 2017-06-14
 **/
public class Translator {


    /**
     * value这种形式：
     * 情况一：产品模块英文名——事件英文名——页面中文名
     * 情况二：产品模块英文名——事件英文名
     * 所以通过split之后只替换0位置和1位置的数据
     *
     * @param value
     * @param translator
     * @return
     */
    public static String translate(String value, Map<String, String> translator) {
        //准备容器
        StringBuffer back = new StringBuffer();
        if (value == null || value.trim().length() <= 0 ||
                translator == null || translator.size() <= 0) {//不做处理
            return back.toString();
        }
        /*拆分并且替换*/
        String[] array_str = value.split("——");
        if (array_str == null || (array_str.length != 2 && array_str.length != 3)) {//格式不对不做处理
            return back.toString();
        }
        for (int i = 0; i < 2; i++) {//只替换前两个位置的数据
            String cn = transENToCN(array_str[i],translator);//获取相应的中文值
            array_str[i]=cn;
        }
        /*拼接*/
        if(array_str.length==2){//产品模块英文名——事件英文名
            back.append(array_str[0]).append("——").append(array_str[1]);
        }else{//产品模块英文名——事件英文名——页面中文名
            back.append(array_str[0]).append("——").append(array_str[1]).append("——").append(array_str[2]);
        }
        return back.toString();
    }

    /**
     * 将英文转为中文
     *
     * @param en
     * @param translator key：对应的英文值；value：对应的中文值；
     *                   目前来说key可以为产品模块or事件分析的英文名
     * @return
     */
    public static String transENToCN(String en, Map<String, String> translator) {
        StringBuffer back = new StringBuffer();//用这个防止null的出现
        //校验
        if (en == null || en.trim().length() <= 0 ||
                translator == null || translator.size() <= 0) {
            return back.toString();
        }
        /*转化*/
        back.append(translator.get(en));
        return back.toString();
    }

}
