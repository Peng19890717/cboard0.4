package org.cboard.services.sec;
/**
 * Created by Administrator on 2017/5/3 0003.
 */

import org.cboard.consts.BackResultConst;
import org.cboard.consts.PathCondition;
import org.cboard.daocboard.DocDao;
import org.cboard.daokylin.KylinDao;
import org.cboard.dto.sec.FigureResult;
import org.cboard.dto.sec.PathCube;
import org.cboard.pojo.sec.StepFather;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 路径分析，漏斗分析业务层
 * （二次开发）
 *
 * @author wbc
 * @date 2017-05-03
 **/
@Service
public class PathService {

//    @Autowired（kylin的先不用了）
    private KylinDao kylinDao;
    @Autowired
    private DocDao docDao;

    /**
     * 返回路径的数据
     *
     * @param pk_funnel_path
     * @param start_time
     * @param end_time
     * @return
     */
    public FigureResult getPathDatas(
            Integer pk_funnel_path,//路径主键（对应Cboard的dashboard_doc表）
            String start_time,//开始时间（这种形式9999-99-99）
            String end_time//结束时间（这种形式9999-99-99）
    ) {
        FigureResult result = new FigureResult();
        try {
            /* 从mysql获取路径信息
              （主要是要这几个字段的值
               npv_uv、datasource【基本信息】
               vpro_module、vlog_name、vpage_name【过滤自己的条件】
               vparent_pro_module、vparent_log_name、vparent_page_name【过滤父亲的条件】）*/
            List<StepFather> doc_details = docDao.getDetails(pk_funnel_path);
            /* 将数据进行分类（分成4类看PathCondition）
             * 0位置放第一类，1位置放第二类，2位置放第三类，3位置放第四类*/
            Object[] class_datas = this.separate(doc_details);//？？？？？
            /*从kylin中获取路径的数据*/
            //准备容器
            List<PathCube> datas = new ArrayList<>();
            //获取数据
            int count = class_datas.length;//长度===4
            for (int i = 0; i < count; i++) {
                List<PathCube> tmp = getDatas(class_datas, i, start_time, end_time);
                if (tmp != null && tmp.size() > 0) {
                    datas.addAll(tmp);
                }
            }
            result.setLinks(datas.toArray(new PathCube[0]));
            result.fillNodes();//给nodes赋值
            result.setMsg(BackResultConst.SUCCESS);
        } catch (Exception e) {
            result.setMsg(BackResultConst.FAIL);
            result.setErrorMsg(e.getMessage());
        }
        return result;
    }

    private List<PathCube> getDatas(Object[] class_datas, int i, String start_time, String end_time) {
        List<StepFather> list = (List<StepFather>) class_datas[i];
        if (list == null || list.size() <= 0) {
            return null;
        }
        //获取以下参数（每个vo的参数值都一样）
        StepFather vo = list.get(0);
        Integer npv_uv = vo.getNpv_uv();
        String vdata_source = vo.getVdata_source();
        switch (i) {
            case 0://第一类
                return kylinDao.getPathDatas(PathCondition.FISRT.getIndex(), npv_uv, vdata_source, start_time, end_time, list);
            case 1://第二类
                return kylinDao.getPathDatas(PathCondition.SECOND.getIndex(), npv_uv, vdata_source, start_time, end_time, list);
            case 2://第三类
                return kylinDao.getPathDatas(PathCondition.THIRD.getIndex(), npv_uv, vdata_source, start_time, end_time, list);
            default://第四类
                return kylinDao.getPathDatas(PathCondition.FORTH.getIndex(), npv_uv, vdata_source, start_time, end_time, list);
        }
    }

    /* 将数据进行分类（分成4类看PathCondition）
     * 0位置放第一类，1位置放第二类，2位置放第三类，3位置放第四类*/
    private Object[] separate(List<StepFather> doc_details) {
        /*装填容器*/
        Object[] result = fill();
        if (doc_details == null || doc_details.size() <= 0) {
            return result;
        }
        /*数据分类*/
        for (StepFather vo : doc_details) {
            //判断vo属于哪个分类（看枚举类PathCondition）
            PathCondition my_class = vo.judgeClass();
            switch (my_class) {
                case FISRT:
                    ((List<StepFather>) result[0]).add(vo);
                    break;//跳出switch表达式
                case SECOND:
                    ((List<StepFather>) result[1]).add(vo);
                    break;
                case THIRD:
                    ((List<StepFather>) result[2]).add(vo);
                    break;
                default:
                    ((List<StepFather>) result[3]).add(vo);
                    break;
            }

        }
        return result;
    }

    private Object[] fill() {
        Object[] result = new Object[4];
        List<StepFather> first = new ArrayList<>();
        List<StepFather> sec = new ArrayList<>();
        List<StepFather> third = new ArrayList<>();
        List<StepFather> fourth = new ArrayList<>();
        result[0] = first;
        result[1] = sec;
        result[2] = third;
        result[3] = fourth;
        return result;
    }


}
