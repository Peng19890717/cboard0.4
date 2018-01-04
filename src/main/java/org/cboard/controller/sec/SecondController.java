package org.cboard.controller.sec;
/**
 * Created by Administrator on 2017/4/28 0028.
 */

import com.alibaba.fastjson.JSON;
import org.cboard.consts.*;
import org.cboard.controller.DashboardController;
import org.cboard.dto.DataProviderResult;
import org.cboard.dto.sec.DataAnalyzeResult;
import org.cboard.tool.JointSqlTool;
import org.cboard.tool.SqlProvider;
import org.cboard.tool.sec.FormateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 事件分析接口
 *
 * @author wbc
 * @date 2017-04-28
 **/
@RestController
@RequestMapping("/dashboardeventsec")
public class SecondController {

    public static void main(String[] args) {
        Date date = new Date(1496686778990L);
        int year = date.getYear();
        int month = date.getMonth();
        int day = date.getDay();
        int i = 0;
    }

    private static final Long mysqlDataSourceId = DataSourceConst.MYSQL;//对应本地数据
    private static final Long kylinDataSourceId = DataSourceConst.KYLIN;//对应Kylin正式环境

    @Autowired
    private DashboardController dashboardController;
    @Autowired
    private InitConst initTool;

    /**
     * 两种情况
     * 情况一：只传递产品模块id（'CC_Video_6.0'、'CC_HomePage6'），返回事件英文名、事件中文名
     * 情况二：传递产品模块+事件名称
     *
     * @param pro_id   这是什么形式CC_Video_6.0、CC_HomePage6
     * @param event_id
     * @return
     */
    @RequestMapping(value = "/getpagedatas")
    public DataAnalyzeResult getPageDatas(
            @RequestParam(name = "pro_id", required = false) String pro_id,
            @RequestParam(name = "event_id", required = false) String event_id) {
        DataAnalyzeResult back = new DataAnalyzeResult(null, null);
        try {
            /*处理查询条件*/
            pro_id = this.handle(pro_id);
            if (event_id != null && event_id.trim().length() > 0) {//事件英文名有值（所以要获取的是页面中文名）
                event_id = this.handle(event_id);
            }
            /*查询数据*/
            DataAnalyzeResult tmp = null;
            if (event_id == null || event_id.trim().length() <= 0) {//事件英文名无值（要获取事件的英文名）
                tmp = this.getLinkageDatas(pro_id, null, false);
            } else {//事件英文名有值（所以要获取的是页面中文名）
                tmp = this.getLinkageDatas(pro_id, event_id, false);
            }
            /*处理数据*/
            this.handleResult(back, tmp, event_id);
            back.setMsg(DataAnalyzeResult.SUCCESS);
        } catch (Exception e) {
            back.setMsg(DataAnalyzeResult.FAIL);
            back.setErrorMessage(e.getMessage());
        }
        return back;
    }

    //根据
    private void handleResult(
            DataAnalyzeResult back,
            DataAnalyzeResult cup, //存放数据
            String event_id) {
        if (event_id == null || event_id.trim().length() <= 0) {//事件英文名无值（要获取事件的英文名）
            Map<String, String> tmp = cup.getEvent_value_name();
            //tmp中所有的key都带单引号，所以需要去掉
            tmp = FormateUtil.handleMapKey(tmp);
            back.setEvent_value_name(tmp);
        } else {//事件英文名有值（所以要获取的是页面中文名）
            List<String> page_name = cup.getCondition_col_value().get(MysqlConst.PAGE_NAME);
            //去掉单引号
            page_name = FormateUtil.removeList(page_name);
            back.setPage_name(page_name);
        }
    }

    /**
     * 产品模块、事件名称、筛选条件联动查询功能
     * 传一个参数：pro_id，获取对应的事件名称
     * 传两个参数：pro_id、event_id，获取对应的所有筛选条件+筛选条件的过滤的值
     *
     * @param pro_id   产品模块id（这种形式   '值'，eg：'CC_Video_6.0'、'CC_HomePage6'）
     * @param event_id 事件名称id（这种形式   '值'）
     * @param reload   是否重新加载（true：从数据库中获取数据；false：从redis缓存中获取8）
     * @return
     */
    @RequestMapping(value = "/getlinkagedatas")
    public DataAnalyzeResult getLinkageDatas(
            @RequestParam(name = "pro_id", required = false) String pro_id,
            @RequestParam(name = "event_id", required = false) String event_id,
            @RequestParam(name = "reload", required = false, defaultValue = "true") Boolean reload) {
        DataAnalyzeResult back = new DataAnalyzeResult(null, null);
        try {
            /*拼接sql（获取数据库信息、表名、sql拼接条件）*/
            //获取sql
            String sql = null;
            if (event_id == null || event_id.trim().length() <= 0) {//获取事件英文名+中文名
                sql = SqlProvider.getEventDatasSql(pro_id);
            } else {//获取筛选条件英文名+中文名+对应的值
                sql = SqlProvider.getLinkageDatasSql(pro_id, event_id);
            }
            //sql转型（将sql转为下面的形式）
            //"{\"sql\":\"select city_code , city_name from area where province_name = '山东省'\"}"
            sql = SqlProvider.exchangeType(sql);
            /*调用dashboardController执行查询（可以参考下面的内容，注意要在数据库中预制一个datasourceId）*/
            DataProviderResult result = dashboardController.getCachedData(mysqlDataSourceId, sql, null, reload);
            //数据转化（将DataProviderResult转化为DataAnalyzeResult类型）
            back = this.exchange(result.getData(), event_id);
            back.setMsg(DataAnalyzeResult.SUCCESS);
        } catch (Exception e) {
            back.setMsg(DataAnalyzeResult.FAIL);
            back.setErrorMessage(e.getMessage());
        }
        return back;
    }

    /**
     * 初始化查询时：只用传递一个参数init（值=true）
     *
     * @param pkg_source
     * @param pro_id
     * @param event_id
     * @param time
     * @param start_time
     * @param end_time
     * @param init
     * @param reload
     * @return
     */
    @RequestMapping(value = "/query")
    public DataAnalyzeResult query(
            @RequestParam(name = "pkg_source", required = false) String pkg_source,//视频源
            @RequestParam(name = "pro_id", required = false) String pro_id,//产品模块id（eg：CC_HomePage6）
            @RequestParam(name = "event_id", required = false) String event_id,//事件名称
            /*筛选条件*/
            @RequestParam(name = "condition_dim", required = false) String condition_dim,//筛选条件维度名称（列名；可以没有值，表示不用过滤筛选条件）
            @RequestParam(name = "condition_relation", required = false) String condition_relation,//筛选条件关系符（只有一个值；也可以没有值，表示不用过滤筛选条件）
            @RequestParam(name = "condition_value", required = false) String condition_value,//筛选条件值（只有一个值；也可以没有值，表示不用过滤筛选条件）
            /*细分属性*/
            @RequestParam(name = "dim", required = false) String dim, //细分属性维度名称（列名；必须有值）
            @RequestParam(name = "dim_value", required = false) String dim_value,//细分属性维度值（中间选择的值，这种形式：值1，值2，值3；可以没有值，如果没值表示不用过滤细分属性）
            /*时间*/
            @RequestParam(name = "time", required = false) String time,//时间间隔（只有四种值：时、日、周、月；对应SqlConst里的SELECT_HOUR ~ SELECT_MONTH）
            @RequestParam(name = "start_time", required = false) String start_time,//开始时间（这种形式9999-99-99）
            @RequestParam(name = "end_time", required = false) String end_time,//结束时间（这种形式9999-99-99）
            /*其它参数*/
            @RequestParam(name = "init", required = false, defaultValue = "false") Boolean init, //是否初始化查询
            //是否点击查询按钮
            // （true：不用过滤细分属性的值；并且时间间隔、开始时间、结束时间不用传递，构建sql时，为默认值，分别为“时”、昨天）
            //（false：表示点击中间的联动按钮进行查询；所以必须传递，时间间隔、时间段、细分属性）
            @RequestParam(name = "button_query", required = false, defaultValue = "false") Boolean button_query,
            @RequestParam(name = "reload", required = false, defaultValue = "false") Boolean reload //是否重新加载;（false：从缓存中获取）
    ) {
        DataAnalyzeResult result = new DataAnalyzeResult(null, null);
        try {
            /*查询条件特殊处理*/
            //表示联动查询要处理细分属性
            if (!init && !button_query) {
                dim_value = handlDim_value(dim, dim_value);
            }
            //不是初始化查询
            if (!init) {
                specialHandler(condition_relation, condition_value);
            }
            /*初始化查询条件（初次打开事件分析界面，点击查询按钮时需要获取相应的查询条件）*/
            if (init || button_query) {
                initQueryCondition(result, init, pro_id, event_id, dim, condition_dim, condition_relation, condition_value, reload);
            }
            /*拼接sql（在拼接sql时进行参数校验）*/
            //查询数据的sql（0位置==》pv_sql；1位置==》uv_sql；2位置==》pv_uv_sql按照时间粒度细分的sql；3位置==》pv_uv_sql汇总的sql）
            String[] sqls = this.getQuerySql(init, pkg_source, pro_id, event_id, condition_dim, condition_relation, condition_value, dim, dim_value, time, start_time, end_time, button_query);
            //数据转化（将里面的4个sql转为这种形式："{\"sql\":\"select city_code , city_name from area where province_name = '山东省'\"}"）
            sqls = SqlProvider.exchangeArrayType(sqls);
            /*查询并且构建（kylin的查询可以用DashboardController的getCachedData(...)方法）*/
            int count_sqls = sqls.length;
            for (int i = 0; i < count_sqls; i++) {//这个循环的长度只为4
                DataProviderResult inner = dashboardController.getCachedData(kylinDataSourceId, sqls[i], null, reload);
                if (i == 0) {//0位置==》pv_sql；
                    result.setData_pv(inner.getData());
                } else if (i == 1) {//1位置==》uv_sql；
                    result.setData_uv(inner.getData());
                } else if (i == 2 && !init && !button_query) {//2位置==》pv_uv_sql按照时间粒度细分的sql；不是初始化查询，并且不是点击查询按钮；
                    result.setData_pv_uv_time(inner.getData());
                } else if (init || button_query) {//3位置==》pv_uv_sql汇总的sql；初始化查询，点击查询按钮时执行这个查询
                    result.setData_pv_uv_sum(inner.getData());
                }
            }
            result.setMsg(DataAnalyzeResult.SUCCESS);
        } catch (Exception e) {
            result.setMsg(DataAnalyzeResult.FAIL);
            result.setErrorMessage(e.getMessage());
        }
        return result;
    }

    //给参数增加单引号
    //将value变为'value'
    private String handle(String value) {
        if (value != null && value.trim().length() > 0) {//有值的时候才进行处理
            value = "'" + value + "'";
        }
        return value;
    }

    private String handlDim_value(String dim, String dim_value) throws Exception {
        if (dim == null || dim.trim().length() <= 0) {//要传递细分属性
            throw new Exception("联动查询需要传递dim值");
        }
        if (dim_value == null || dim_value.trim().length() <= 0) {//没有传递细分属性的过滤条件
            dim_value = " ('') ";
        } else {
            dim_value = " (" + dim_value + ") ";
        }
        return dim_value;
    }

    //如果关系运算符（condition_relation）是为空、不为空时，condition_value为空字符串
    //当。。。                            是包含、不包含是，condition_value应该变为这样'%condition_value%'
    private void specialHandler(String condition_relation, String condition_value) {
        if (condition_relation.equals(LogicalConst.ISNULL) || condition_relation.equals(LogicalConst.ISNULL.trim()) ||
                condition_relation.equals(LogicalConst.ISNOTNULL) || condition_relation.equals(LogicalConst.ISNOTNULL.trim())) {
            condition_value = "";
        } else if (condition_relation.equals(LogicalConst.LIKE) || condition_relation.equals(LogicalConst.LIKE.trim()) ||
                condition_relation.equals(LogicalConst.NOTLIKE) || condition_relation.equals(LogicalConst.NOTLIKE.trim())) {
            condition_value = " '%" + condition_value + "%' ";
        }
    }

    //给result的查询条件赋值
    private void initQueryCondition(
            DataAnalyzeResult result,
            Boolean init,//是否初始化时的查询
            String pro_id, //产品模块id（eg：CC_HomePage6）
            String event_id,//事件名称
            String dim,//细分属性维度名称（列名；必须有值）
            String condition_dim,//筛选条件维度名称（列名；可以没有值，表示不用过滤筛选条件）
            String condition_relation,//筛选条件关系符（只有一个值；也可以没有值，表示不用过滤筛选条件）
            String condition_value, Boolean reload) {//筛选条件值（只有一个值；也可以没有值，表示不用过滤筛选条件）
        if (init) {
            //初始化查询时
            init(result);
        } else {
            //点击button进行查询时，更新查询条件（只更新中间的细分属性对应的过滤值）
            initByDim_Values(result, pro_id, event_id, dim, condition_dim, condition_relation, condition_value, reload);
        }
    }

    //当只有细分属性，或者筛选条件和细分属性不同时，则DataAnalyzeResult里的dim_values值，只受到细分属性的影响
    //当有细分属性+筛选条件，并且筛选条件=细分属性，则DataAnalyzeResult里的dim_values值，受到细分属性+筛选条件共同影响
    private void initByDim_Values(
            DataAnalyzeResult result,
            String pro_id, //产品模块id（eg：CC_HomePage6）
            String event_id,//事件名称
            String dim,//细分属性维度名称（列名；必须有值）
            String condition_dim,//筛选条件维度名称（列名；可以没有值，表示不用过滤筛选条件）
            String condition_relation,//筛选条件关系符（只有一个值；也可以没有值，表示不用过滤筛选条件）
            String condition_value,//筛选条件值（只有一个值；也可以没有值，表示不用过滤筛选条件）
            Boolean reload) {
        /*获取需要拼接的sql*/
        StringBuffer sql = new StringBuffer();
        //当只有细分属性，或者筛选条件和细分属性不同时，则DataAnalyzeResult里的dim_values值，只受到细分属性的影响
        if ((condition_dim == null || condition_dim.trim().length() <= 0) ||//没有选择筛选条件
                (condition_relation == null || condition_relation.length() <= 0) ||//没有选择筛选条件
                (dim != null && condition_dim != null && !dim.trim().equals(condition_dim.trim()))//细分属性和筛选条件不一样
                ) {
            sql.append(this.getDistinctValueSql(pro_id, event_id, dim));
        } else {//当有细分属性+筛选条件，并且筛选条件=细分属性，则DataAnalyzeResult里的dim_values值，受到细分属性+筛选条件共同影响
            sql.append(this.getDistinctValueSql(pro_id, event_id, dim));
            if (condition_dim != null && condition_dim.trim().length() > 0 &&
                    condition_relation != null && condition_relation.trim().length() > 0) {//有筛选条件
                sql.append(" and ");
                sql.append(" " + MysqlConst.ENUM_DIM_COLUMN_VALUE + " ");//维度列这个字段
                if (!LogicalConst.ISNULL.contains(condition_relation) &&
                        !LogicalConst.ISNOTNULL.contains(condition_relation)) {//当关系值是is null 或者 is not null则不用加condition_relation
                    sql.append(" " + condition_relation + " ");
                }
                sql.append(" " + condition_value + " ");
            }
        }
        /*查询*/
        //sql转型（将sql转为下面的形式）
        //"{\"sql\":\"select city_code , city_name from area where province_name = '山东省'\"}"
        String tmp_sql = SqlProvider.exchangeType(sql.toString());
        DataProviderResult tmp_result = dashboardController.getCachedData(mysqlDataSourceId, tmp_sql, null, reload);
        /*赋值*/
        String[][] data = tmp_result.getData();
        List<String> list = new ArrayList<String>();
        int count = data.length;
        if (count <= 1) {//没数据
            result.setDim_values(new String[0]);
            return;
        }
        for (int i = 1; i < count; i++) {//第一行是格式
            list.add("'" + data[i][0] + "'");//只有一列
        }
        result.setDim_values(list.toArray(new String[0]));
    }

    //根据产品模块id，事件名称id，维度列的值来获取维度对应的去重后的数据
    private String getDistinctValueSql(
            String pro_id,  //这种形式'值'
            String event_id,//这种形式'值'
            String dim) {//没有单引号（细分属性=维度列名）
        StringBuffer sql = new StringBuffer();
        sql.append(" select ");
        sql.append("        dim_column_value");
        sql.append(" from " + MysqlConst.DASHBOARD_ENUM + " ");
        sql.append(" where pro_module = " + pro_id);/*产品模块*/
        sql.append(" and event_name = " + event_id);
        sql.append(" and dim_column = '" + dim + "' ");
        return sql.toString();
    }

    //初始化查询时，需要初始化查询条件
    //包括：视频源（固定3个）、产品模块（固定2个）、事件名称、筛选条件、筛选条件过滤值、
    //细分属性、细分属性过滤值
    private void init(DataAnalyzeResult result) {
        /*视频源（固定3个）*/
        result.setPkg_sources(initTool.getSource_all_name_value());
        /*产品模块*/
        String pro_default = result.getData_sources_default();//默认的产品模块id（6.0影视中心）
        result.setData_sources(initTool.getPro_all_name_value());
        /*事件名称（通过产品模块id（6.0影视中心）动态获取）*/
        String event_id_default = result.getEvent_value_name_default();//默认的事件名称(影视首页-单个推荐位曝光事件)
        result.setEvent_value_name(
                this.getLinkageDatas(pro_default, null, true).getEvent_value_name()
        );
        /*筛选条件（根据产品模块id+事件名称id获取）*/
        DataAnalyzeResult tmp = this.getLinkageDatas(pro_default, event_id_default, true);
        result.setCondition_col_name(
                tmp.getCondition_col_name()
        );
        result.setCondition_col_value(
                tmp.getCondition_col_value()
        );
        /*细分属性*/
        result.setDim(initTool.getPublic_dim());
        result.setDim_values(
                tmp.getCondition_col_value().get(InitConst.INIT_DIM_ENAME).toArray(new String[0])
        );
        //时间间隔（时、日、周、月）==》已经有了，定死的DataAnalyzeResult的time字段
    }

    /**
     * 获取数据集的查询sql
     *
     * @param init
     * @param pkg_source
     * @param pro_id
     * @param event_id
     * @param condition_dim
     * @param condition_relation
     * @param condition_value
     * @param dim
     * @param dim_value
     * @param time
     * @param start_time
     * @param end_time
     * @param button_query
     * @return 0位置==》pv_sql；1位置==》uv_sql；2位置==》pv_uv_sql按照时间粒度细分的sql；3位置==》pv_uv_sql汇总的sql
     * @throws Exception
     */
    private String[] getQuerySql(
            Boolean init,
            String pkg_source,
            String pro_id,
            String event_id,
            String condition_dim,
            String condition_relation,
            String condition_value,
            String dim,
            String dim_value,
            String time,
            String start_time,
            String end_time,
            Boolean button_query) throws Exception {
        String[] sql_array = null;
        if (init) {//初始化查询
            sql_array = getInitSql();
        } else {//其它查询
            //点击查询按钮进行查询 + 点击中间查询联动条件（时间间隔、时间段、细分属性）进行查询
            sql_array = getButton_LinkageQuerySql(pkg_source, pro_id, event_id, condition_dim, condition_relation, condition_value, dim, dim_value, time, start_time, end_time, button_query);
        }
        return sql_array;
    }

    /**
     * 准备以下数据：
     * String start_time,开始时间
     * String end_time,  结束时间
     * String time,时间维度（时、日、周、月）
     * Map<String, String> values,    key：字段维度；value：字段值
     * Map<String, String> relations  key：字段维度；value：关系条件
     *
     * @return
     */
    private String[] getInitSql() throws Exception {
        /* 准备sql容器
         *（0位置==》pv_sql；1位置==》uv_sql；2位置==》pv_uv_sql按照时间粒度细分的sql；3位置==》pv_uv_sql汇总的sql）*/
        String[] sql_array = new String[4];
        /*获取查询条件*/
        String[] times = initTool.getPeriods();
        //开始时间（昨天）
        String start_time = "'" + times[0] + "'";
        //结束时间（昨天）
        String end_time = "'" + times[1] + "'";
        //时间维度（时）
        String time = CommonConst.HOUR;
        //筛选维度_过滤值
        Map<String, String> values = initTool.getInit_values();
        //筛选维度_关系运算符
        Map<String, String> relations = initTool.getInit_relations();
        //细分属性维度英文名（列英文名）
        String dim_ename = InitConst.INIT_DIM_ENAME;
        /*获取pv*/
        sql_array[0] = JointSqlTool.getPVorUVSql(start_time, end_time, time, values, relations, true);
        /*获取uv*/
        sql_array[1] = JointSqlTool.getPVorUVSql(start_time, end_time, time, values, relations, false);
        /*获取pv+uv（粒度细，按照时间进行分组）*/
        sql_array[2] = JointSqlTool.getExcelSql(start_time, end_time, time, values, relations, dim_ename, true);
        /*获取pv+uv（汇总值）*/
        sql_array[3] = JointSqlTool.getExcelSql(start_time, end_time, null, values, relations, dim_ename, false);
        return sql_array;
    }

    private String[] getButton_LinkageQuerySql(
            String pkg_source,//数据源(数据库值)
            String pro_id,//产品模块(数据库值)
            String event_id,//事件名称(数据库值)
            String condition_dim,//筛选条件维度英文名
            String condition_relation,//筛选条件关系符
            String condition_value,//筛选条件过滤值
            String dim,//细分属性列英文名
            String dim_value,//细分属性过滤值
            String time,//时间间隔（只有四种值：时、日、周、月；对应SqlConst里的SELECT_HOUR ~ SELECT_MONTH）
            String start_time,//开始时间（这种形式9999-99-99）
            String end_time,//结束时间（这种形式9999-99-99）
            Boolean button_query) throws Exception {
        /* 准备sql容器
         *（0位置==》pv_sql；1位置==》uv_sql；2位置==》pv_uv_sql按照时间粒度细分的sql；3位置==》pv_uv_sql汇总的sql）*/
        String[] sql_array = new String[4];
        /*获取查询条件*/
        if (button_query) {//点击查询按钮，则时间间隔为时，时间段位昨天，并且不用过滤细分属性
            time = CommonConst.HOUR;
            String[] tmp_time = initTool.getPeriods();
            start_time = "'" + tmp_time[0] + "'";
            end_time = "'" + tmp_time[1] + "'";
        } else {//点击中间联动按钮（时间间隔、时间段、细分属性）进行查询
            //开始时间
            start_time = "'" + start_time + " " + CommonConst.START_TIME_TAIL + "'";
            //结束时间
            end_time = "'" + end_time + " " + CommonConst.END_TIME_TAIL + "'";
        }
        /*获取条件*/
        Object[] array_map = JointSqlTool.getValues(pkg_source, pro_id, event_id, condition_dim, condition_relation, condition_value, dim, dim_value, button_query);
        //筛选维度_过滤值
        Map<String, String> values = (Map<String, String>) array_map[0];
        //筛选维度_关系运算符
        Map<String, String> relations = (Map<String, String>) array_map[1];
        /*获取pv*/
        sql_array[0] = JointSqlTool.getPVorUVSql(start_time, end_time, time, values, relations, true);
        /*获取uv*/
        sql_array[1] = JointSqlTool.getPVorUVSql(start_time, end_time, time, values, relations, false);
        /*获取pv+uv（粒度细，按照时间进行分组）*/
        sql_array[2] = JointSqlTool.getExcelSql(start_time, end_time, time, values, relations, dim, true);
        /*获取pv+uv（汇总值）*/
        sql_array[3] = JointSqlTool.getExcelSql(start_time, end_time, time, values, relations, dim, false);
        return sql_array;
    }

    private DataAnalyzeResult exchange(String[][] result, String event_id) {
        DataAnalyzeResult back = new DataAnalyzeResult(null, "1");

        if (result == null || result.length <= 1) {//没有数据(第一行为表结构)
            return back;
        }
        int outCount = result.length;//外层数组的长度
        if (event_id == null || event_id.trim().length() <= 0) {//获取事件英文名+中文名（2列：事件id、事件中文名）
            Map<String, String> event_value_name = new HashMap<String, String>();
            for (int i = 1; i < outCount; i++) {//第一行数据是列名
                event_value_name.put(result[i][0], result[i][1]);
            }
            back.setEvent_value_name(event_value_name);
        } else {//获取筛选条件英文名+中文名+对应的值（3列数据：维度列（英文名）、维度列中文名、维度列对应的值）
            // key：筛选条件维度英文名（即base_clog_view的列名/维度名）；
            // value:筛选条件（中文名）
            Map<String, String> condition_col_name = new HashMap<String, String>();
            //key：筛选条件维度英文名（即base_clog_view的列名/维度名）；
            //value：维度对应的值（去重后的值）
            Map<String, List<String>> condition_col_value = new HashMap<String, List<String>>();
            for (int i = 1; i < outCount; i++) {//第一行数据是列名
                //装填数据
                load(condition_col_name, condition_col_value, result[i]);
            }
            back.setCondition_col_name(condition_col_name);
            back.setCondition_col_value(condition_col_value);
        }
        String json = JSON.toJSONString(back);
        return back;
    }

    private void load(
            Map<String, String> condition_col_name,
            Map<String, List<String>> condition_col_value,
            String[] line) {//（3列数据：维度列（英文名）、维度列中文名、维度列对应的值）
        String dim_column = line[0];//维度英文名
        //condition_col_name
        // key：筛选条件维度英文名（即base_clog_view的列名/维度名）；
        // value:筛选条件（中文名）
        if (!condition_col_name.containsKey(dim_column)) {
            condition_col_name.put(dim_column, line[1]);
        }
        //condition_col_value
        //key：筛选条件维度英文名（即base_clog_view的列名/维度名）；
        //value：维度对应的值（去重后的值）
        if (condition_col_value.containsKey(dim_column)) {
            condition_col_value.get(dim_column).add(line[2]);
        } else {
            List<String> list = new ArrayList<String>();
            list.add(line[2]);
            condition_col_value.put(dim_column, list);
        }
    }

}
