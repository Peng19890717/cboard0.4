package org.cboard.controller.fourth;

import org.cboard.controller.BaseController;
import org.cboard.services.ServiceStatus;
import org.cboard.services.fourth.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2018/1/9 0009.
 */
@RestController
@RequestMapping("/dashboardmenu")
public class MenuController extends BaseController {

    @Autowired
    protected DashboardService dashboardService;

    /**
     * 更新一级菜单分类
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/updateCategory")
    public ServiceStatus updateCategory(@RequestParam(name = "json") String json) throws Exception {
        return dashboardService.updateCategory(json);
    }

    /**
     * 更新二级菜单分类
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/updateBoard")
    public ServiceStatus updateBoard(@RequestParam(name = "json") String json) throws Exception {
        return dashboardService.updateBoard(json);
    }

}
