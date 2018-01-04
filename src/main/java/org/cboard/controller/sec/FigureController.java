package org.cboard.controller.sec;

/**
 * Created by Administrator on 2017/5/31 0031.
 */


import org.cboard.consts.BackResultConst;
import org.cboard.consts.CommonConst;
import org.cboard.daocboard.DocDao;
import org.cboard.daokylin.KylinDao;
import org.cboard.dto.sec.CommonResult;
import org.cboard.dto.sec.DashBoardFunnelAnalysisResult;
import org.cboard.dto.sec.FigureResult;
import org.cboard.dto.sec.FunnelCube;
import org.cboard.pojo.sec.DashBoardFunnelDetail;
import org.cboard.pojo.sec.DashboardFunnel;
import org.cboard.pojo.sec.StepsDetails;
import org.cboard.services.sec.FunnelService;
import org.cboard.services.sec.PathService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 漏斗、路径分析的controller
 *
 * @author wbc
 * @date 2017-05-31
 **/
@RestController
@RequestMapping("/dashboardfiguresec")
public class FigureController {

    @Autowired
    private DocDao docDao;
//    @Autowired  （kylin的先不用了）
    private KylinDao kylinDao;

    @Autowired
    private FunnelService funnelService;
    @Autowired
    private PathService pathService;//路径分析业务层

    private CommonResult commonResult = new CommonResult();

    /**
     * 获取路径分析的数据
     *
     * @return
     */
    @RequestMapping(value = "/getpathdatas")
    public FigureResult getPathDatas(
            @RequestParam(name = "pk_funnel_path", required = true) Integer pk_funnel_path,//漏斗主键（对应Cboard的dashboard_doc表）
            @RequestParam(name = "start_time", required = true) String start_time,//开始时间（这种形式9999-99-99）
            @RequestParam(name = "end_time", required = true) String end_time//结束时间（这种形式9999-99-99）
    ) {
        return pathService.getPathDatas(pk_funnel_path, start_time, end_time);
    }

    @RequestMapping(value = "/getfunneldatas")
    public FigureResult getFunnelDatas(
            @RequestParam(name = "pk_funnel_path", required = true) Integer pk_funnel_path,//漏斗主键（对应Cboard的dashboard_doc表）
            @RequestParam(name = "start_time", required = true) String start_time,//开始时间（这种形式9999-99-99）
            @RequestParam(name = "end_time", required = true) String end_time//结束时间（这种形式9999-99-99）
    ) {
        FigureResult result = new FigureResult();
        try {
            /*参数校验*/
            judge(pk_funnel_path, start_time, end_time);
            /*获取漏斗信息*/
            List<DashboardFunnel> list_OneDoc = docDao.getOneDoc(pk_funnel_path);
            if (list_OneDoc == null || list_OneDoc.size() <= 0) {
                throw new Exception("获取不到漏斗档案信息");
            }
            /* 将漏斗信息分组（只有事件名称为一组，有事件名称和页面名称为另一组）
             * 分组的标准，是否有页面中文名vpage_name*/
            //位置0：只需要过滤事件名称的数据（如果没有则为长度是0的List）
            //位置1：需要过滤有事件名称和页面名称为另一组（如果没有则为长度是0的List）
            Object[] tmp = separate(list_OneDoc);
            /*根据分组信息进行查询（从麒麟中查询，并且是for循环查询（这个和斌哥+苏鑫商量了，只能for循环查询，因为kylin模型不支持一个sql过滤两个产品模块））*/
            List<FunnelCube> tmp_cup = new ArrayList<FunnelCube>();
            List<DashboardFunnel> list_first = (List<DashboardFunnel>) tmp[0];
            List<DashboardFunnel> list_sec = (List<DashboardFunnel>) tmp[1];
            //查询只需要过滤事件名称的数据
            if (list_first.size() > 0) {//有数据的情况才去查询
                tmp_cup.addAll(
                        kylinDao.getFunnelDatas(false, list_first.get(0).getVdata_source(), start_time, end_time, list_first)
                );
            }
            //查询要过滤有事件名称和页面名称的数据
            if (list_sec.size() > 0) {//有数据的情况才去查询
                tmp_cup.addAll(
                        kylinDao.getFunnelDatas(true, list_sec.get(0).getVdata_source(), start_time, end_time, list_sec)
                );
            }
            result.setDatas(tmp_cup.toArray(new FunnelCube[0]));
            result.setMsg(BackResultConst.SUCCESS);
        } catch (Exception e) {
            result.setMsg(BackResultConst.FAIL);
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }

    /* 将漏斗信息分组（只有事件名称为一组，有事件名称和页面名称为另一组）
     * 分组的标准，是否有页面中文名vpage_name*/
    //位置0：只需要过滤事件名称的数据（如果没有则为长度是0的List）
    //位置1：需要过滤有事件名称和页面名称为另一组（如果没有则为长度是0的List）
    private Object[] separate(List<DashboardFunnel> list_oneDoc) {
        /*准备容器*/
        Object[] back = new Object[2];
        List<DashboardFunnel> only_event_name = new ArrayList<DashboardFunnel>();//存放只需要过滤事件名称的数据
        List<DashboardFunnel> event_page = new ArrayList<DashboardFunnel>();//存放需要过滤有事件名称和页面名称
        back[0] = only_event_name;
        back[1] = event_page;
        /*给容器赋值*/
        if (list_oneDoc == null || list_oneDoc.size() <= 0) {
            return back;
        }
        for (DashboardFunnel vo : list_oneDoc) {

            String vpage_name = vo.getVpage_name();
            if (vpage_name == null || vpage_name.trim().length() <= 0) {//只需要过滤事件名称的数据
                only_event_name.add(vo);
            } else {//需要过滤有事件名称和页面名称
                event_page.add(vo);
            }
        }
        return back;
    }

    private void judge(Integer pk_funnel_path, String start_time, String end_time) throws Exception {
        if (pk_funnel_path == null ||
                start_time == null || start_time.trim().length() <= 0 ||
                end_time == null || end_time.trim().length() <= 0) {
            throw new Exception("参数异常");
        }
    }

    @RequestMapping(value = "/savefunneldata", method = RequestMethod.POST, consumes = "application/json")
    public CommonResult saveFunnelData(@RequestBody DashBoardFunnelDetail dashBoardFunnelDetail) {


        if (dashBoardFunnelDetail != null) {
            ArrayList<DashboardFunnel> dashboardFunnels = new ArrayList<>();

            String vdata_source = dashBoardFunnelDetail.getVdata_source();
            ArrayList<Integer> npv_uv = dashBoardFunnelDetail.getNpv_uv();
            ArrayList<StepsDetails> steps = dashBoardFunnelDetail.getSteps();
            Integer maxPfp = funnelService.selectMaxPfp();

            for (int j = 0; j < npv_uv.size(); j++) {
                for (int i = 0; i < steps.size(); i++) {

                    DashboardFunnel dashboardFunnel = new DashboardFunnel();//接受数据的实体

                    dashboardFunnel.setVlog_name(steps.get(i).getVlog_name());
                    dashboardFunnel.setVpage_name(steps.get(i).getVpage_name());
                    dashboardFunnel.setVpro_module(steps.get(i).getVpro_module());
                    dashboardFunnel.setVname(dashBoardFunnelDetail.getVname());
                    dashboardFunnel.setVdata_source(dashBoardFunnelDetail.getVdata_source());
                    dashboardFunnel.setNpv_uv(npv_uv.get(j));
                    dashboardFunnel.setPk_funnel_path(maxPfp);
                    dashboardFunnel.setNfunnel_path(CommonConst.NFUNNEL_PATH_FUNNEL);
                    dashboardFunnel.setNstep((i + 1) + "");

                    dashboardFunnels.add(dashboardFunnel);

                }
            }

            int i = funnelService.saveFunnelData(dashboardFunnels);
            if (i > 0) {
                commonResult.setMsg(BackResultConst.SUCCESS);
            } else {
                throw new RuntimeException("插入失败");
            }
        }
        return commonResult;
    }

    @RequestMapping(value = "/getFunnelAnalysisData")
    public DashBoardFunnelAnalysisResult getDashBoardFunnelAnalysis() {

        DashBoardFunnelAnalysisResult funnelAnalysisResult = new DashBoardFunnelAnalysisResult();
        try {
            Map<Integer, String> analysisData = funnelService.getFunnelAnalysisData();
            Map<Integer, String> VideoData = funnelService.getFunnelAnalysisVideoData();
            funnelAnalysisResult.setMapData(analysisData);
            funnelAnalysisResult.setMapVideo(VideoData);
            funnelAnalysisResult.setMsg(BackResultConst.SUCCESS);
        } catch (Exception e) {
            funnelAnalysisResult.setMsg(BackResultConst.FAIL);
            funnelAnalysisResult.setErrorMsg(e.getMessage());

        }

        return funnelAnalysisResult;
    }


}




