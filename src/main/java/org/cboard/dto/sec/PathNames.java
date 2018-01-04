package org.cboard.dto.sec;
/**
 * Created by Administrator on 2017/6/9 0009.
 */

import java.io.Serializable;

/**
 * 桑基图中出现的名称
 *
 * @author wbc
 * @date 2017-06-09
 **/
public class PathNames implements Serializable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
