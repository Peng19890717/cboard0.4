package org.cboard.dto.sec;

/**
 * Created by Administrator on 2017/5/31 0031.
 */

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wbc
 * @date 2017-05-31
 **/
public class FigureResult extends CommonResult {
    /*漏斗的*/
    private FunnelCube[] datas;
    /*路径的*/
    private PathNames[] nodes;//桑基图中出现的名称列表（这里面的数据从links的source和target中获取放到set中（因为set有去重的功能））
    private PathCube[] links;//桑基图中出现的数据

    /**
     * 将links里面的target、source进行去重并放到nodes中
     */
    public void fillNodes() {
        if (links == null || links.length <= 0) {//没数据不处理
            return;
        }
        /*准备容器*/
        //Set不允许有重复值
        Set<String> cup = new HashSet<>();
        /*装填数据*/
        for (PathCube vo : links) {
            cup.add(vo.getSource());
            cup.add(vo.getTarget());
        }
        /*通过cup里面的数据构建nodes*/
        List<PathNames> names = new ArrayList<>();
        int count = cup.size();
        for(String name : cup ){
            PathNames tmp = new PathNames();
            tmp.setName(name);
            names.add(tmp);
        }
        /*赋值*/
        nodes = names.toArray(new PathNames[0]);
    }


    public FunnelCube[] getDatas() {
        return datas;
    }

    public void setDatas(FunnelCube[] datas) {
        this.datas = datas;
    }

    public PathNames[] getNodes() {
        return nodes;
    }

    public void setNodes(PathNames[] nodes) {
        this.nodes = nodes;
    }

    public PathCube[] getLinks() {
        return links;
    }

    public void setLinks(PathCube[] links) {
        this.links = links;
    }


}
