package org.cboard.services.fourth;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.cboard.dao.BoardDao;
import org.cboard.dao.CategoryDao;
import org.cboard.pojo.DashboardBoard;
import org.cboard.pojo.DashboardCategory;
import org.cboard.services.ServiceStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Iterator;

/**
 * Created by Administrator on 2018/1/9 0009.
 */
@Repository
public class DashboardService {


    @Autowired
    private BoardDao boardDao;

    @Autowired
    private CategoryDao categoryDao;

    /**
     * 更新一级菜单分类
     *
     * @param json
     * @return
     */
    public ServiceStatus updateCategory(@RequestParam(name = "json") String json) throws Exception {
//        String json = "{\"data\":[{\"id\":2,\"sort\":1},{\"id\":3,\"sort\":2},{\"id\":4,\"sort\":3}]}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray array = jsonObject.getJSONArray("data");
        Iterator<Object> iterator = array.iterator();
        while (iterator.hasNext()) {
            JSONObject object = (JSONObject) iterator.next();
            Long id = object.getLong("id");
            Integer sort = object.getInteger("sort");
            //do something
            DashboardCategory dashboardCategory = new DashboardCategory();
            dashboardCategory.setId(id);
            dashboardCategory.setSort(sort);
            check(id, sort);//不合法直接抛异常
            categoryDao.updateSort(dashboardCategory);
        }
        return new ServiceStatus(ServiceStatus.Status.Success, "success");
    }

    private synchronized void check(Long id, Integer sort) throws Exception {
        if (id == null || sort == null) {
            throw new Exception("传入参数异常id的值为【" + id + "】，sort的值为【" + sort + "】");
        }
    }

    /**
     * 更新二级菜单分类
     *
     * @param json
     * @return
     */
    public ServiceStatus updateBoard(@RequestParam(name = "json") String json) throws Exception {
//        String json = "{\"data\":[{\"id\":2,\"sort\":1},{\"id\":3,\"sort\":2},{\"id\":4,\"sort\":3}]}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray array = jsonObject.getJSONArray("data");
        Iterator<Object> iterator = array.iterator();
        while (iterator.hasNext()) {
            JSONObject object = (JSONObject) iterator.next();
            Long id = object.getLong("id");
            Integer sort = object.getInteger("sort");
            //do something
            DashboardBoard dashboardBoard = new DashboardBoard();
            dashboardBoard.setId(id);
            dashboardBoard.setSort(sort);
            check(id, sort);//不合法直接抛异常
            boardDao.updateSort(dashboardBoard);
        }
        return new ServiceStatus(ServiceStatus.Status.Success, "success");
    }

    public static void main(String[] args) {
        String json = "{\"data\":[{\"id\":2,\"sort\":1},{\"id\":3,\"sort\":2},{\"id\":4,\"sort\":3}]}";
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray array = jsonObject.getJSONArray("data");
        Iterator<Object> iterator = array.iterator();
        while (iterator.hasNext()) {
            JSONObject object = (JSONObject) iterator.next();
            Long id = object.getLong("id");
            Integer sort = object.getInteger("sort");
            //do something

        }
    }

}
