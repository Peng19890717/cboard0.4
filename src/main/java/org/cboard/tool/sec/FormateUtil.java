package org.cboard.tool.sec;
/**
 * Created by Administrator on 2017/6/6 0006.
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 和格式相关的工具类
 *
 * @author wbc
 * @date 2017-06-06
 **/
public class FormateUtil {

    /**
     * 去掉value中的单引号
     *
     * @param value 这种形式：'值'
     * @return
     */
    public static String remove(String value) {
        StringBuffer tmp = new StringBuffer();
        if (value == null || value.trim().length() <= 0) {
            return tmp.toString();
        }
        tmp.append(value.substring(value.indexOf("'") + 1, value.lastIndexOf("'")));
        return tmp.toString();
    }

    /**
     * 去掉list里面String前后的单引号
     *
     * @param values
     * @return
     */
    public static List<String> removeList(List<String> values) {
        List<String> cup = new ArrayList<>();
        if (values == null || values.size() <= 0) {
            return null;//没有数据返回null这个是前端要求的（这样的话，前端好处理）
        }
        for(String val : values){
            cup.add(remove(val));
        }
        return cup;
    }

    /**
     * 去掉map中key前面和后面的单引号
     * 将key从这种形式  '值'   变为这种形式  值
     *
     * @param value key：这种形式'值'
     * @return
     */
    public static Map<String, String> handleMapKey(Map<String, String> value) {
        Map<String, String> cup = new HashMap<>();
        if (value == null || value.size() <= 0) {//没有数据就不用处理
            return cup;
        }
        for (Map.Entry<String, String> entry : value.entrySet()) {
            cup.put(remove(entry.getKey()), entry.getValue());
        }
        return cup;
    }

}
