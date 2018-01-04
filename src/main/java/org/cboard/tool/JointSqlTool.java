package org.cboard.tool;
/**
 * Created by Administrator on 2017/4/26 0026.
 */

import org.cboard.consts.CommonConst;
import org.cboard.consts.KylinConst;
import org.cboard.consts.LogicalConst;
import org.cboard.consts.SqlConst;

import java.util.HashMap;
import java.util.Map;

/**
 * 拼接sql的工具类
 *
 * @author wbc
 * @date 2017-04-26
 **/
public class JointSqlTool {

    /**
     * 通过列名、值和关系运算符，拼接sql
     * 注意：
     * 此工具类只负责sql条件的拼接，如果
     * 效果：
     * where column1   =   1  and  column3   =   '小王'  and  column2   in   ('aaa','bbb')
     *
     * @param values    key：列名；value：列对应的值
     *                  value可以存储各种类型的数据，并且可以为多个值或者一个值
     * @param relations key：列名；value：列对应的逻辑运算符（LogicalConst这里有）；
     * @return
     * @throws Exception
     */
    public static synchronized String getWhereCondition(
            Map<String, String> values,
            Map<String, String> relations) throws Exception {
        //校验参数
        judge(values, relations);
        //通过values（值）relations（关系运算符），来确定sql后面的条件
        StringBuffer condition = new StringBuffer();
        condition.append("and");
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String column = entry.getKey();//列名
            String value = entry.getValue();//列值
            String relation = relations.get(column);//关系运算符
            if (relation == null || relation.trim().length() <= 0) {
                throw new Exception("请选择筛选条件与值的关系");
            }
            condition.append(" " + column + " ")
                    .append(" " + relation + " ")
                    .append(" " + value + " ")
                    .append(" and ");
        }
        String back = condition.substring(0, condition.lastIndexOf("and"));
        return back;
    }

    /**
     * 获取pv或者uv对应的sql
     * 对应中间的曲线图（x轴为时间，y轴为pv/uv）
     * 注意：
     * 整体格式是这样的
     * select
     * ——时间室间隔字段，（时、日、周、月；对应SqlConst的SELECT_HOUR~SELECT_MONTH）
     * ——pv或者uv
     * from 麒麟两张表（分别为base_clog_analysis和dim_date_comm）
     * where 视频源、产品模块、时间名称、
     * ——筛选条件（6大维度+【页面名称+专辑】）、细分属性（可有可无））、
     * and 开始时间、结束时间(这个只能用between...and...)
     * group by 时间间隔字段
     * order by 时间间隔字段
     *
     * @param start_time 这种形式 '9999-99-99 00:00:00'
     * @param end_time   这种形式 '9999-99-99 23:59:59'
     * @param time       时间间隔（只有4种值，时，日，周，月;CommonConst）
     * @param values     key：字段维度；value：字段值
     * @param relations  key：字段维度；value：关系条件
     * @param flag       true：pv；   false：uv；
     * @return
     * @throws Exception
     */
    public static String getPVorUVSql(
            String start_time,
            String end_time,
            String time,
            Map<String, String> values,//包括视频源、产品模块、事件名称、细分属性、筛选条件
            Map<String, String> relations,
            Boolean flag
    ) throws Exception {
        //校验
        judge(start_time, end_time, time, values, relations);
        //拼接sql
        StringBuffer sql = new StringBuffer();
        String dim_time = getDim(time);
        sql.append(SqlConst.SELECT);
        sql.append(dim_time);//获取需要统计的维度（时，日，周，月）
        sql.append(",");
        if (flag) {
            sql.append(SqlConst.PV);//重命名为pv
        } else {
            sql.append(SqlConst.UV);//重命名为pv
        }
        sql.append(SqlConst.FROM);
        sql.append(" where " + SqlConst.FILTER_TIME + " between " + start_time + " and " + end_time + " ");
        sql.append(getWhereCondition(values, relations));//过滤查询条件（and后面的内容，并且以and开头）
        sql.append(" group by " + dim_time);
        sql.append(" order by " + dim_time);
        if (CommonConst.WEEK.equals(time)) {//如果是周，则要将year_month（201705）和week_year（19）拼接起来,并且拼接成这种形式
            sql = handlWeek(flag, sql);
        }
        return sql.toString();
    }

    private static StringBuffer handlWeek(Boolean flag, StringBuffer sql) {
        StringBuffer new_sql = new StringBuffer();
        new_sql.append(" select ");
        new_sql.append("    concat(concat(substring(YEAR_MONTH,1,4),'w'), week_year) update_time ,  ");
        if (flag) {
            new_sql.append(" pv ");//得到pv
        } else {
            new_sql.append(" uv ");//得到pv
        }
        new_sql.append(" from ( ");
        new_sql.append(sql);
        new_sql.append(" ) tmp ");
        return new_sql;
    }

    /**
     * @param start_time 这种形式 '9999-99-99 00:00:00'
     * @param end_time   这种形式 '9999-99-99 23:59:59'
     * @param time       时间间隔（只有4种值，时，日，周，月;CommonConst）
     * @param values     key：字段维度；value：字段值
     * @param relations  key：字段维度；value：关系条件       '
     * @param dim        选择的细分属性维度的英文名
     * @param flag       true：按照细分属性获取细粒度的数据；false：获取汇总数据（不用group by 细分属性）；
     * @return
     */
    public static String getExcelSql(
            String start_time,
            String end_time,
            String time,
            Map<String, String> values,//包括视频源、产品模块、事件名称、细分属性、筛选条件
            Map<String, String> relations,
            String dim,//选择的细分属性维度的英文名
            Boolean flag //true：按照细分属性，获取的数据粒度更细；false：获取汇总的数据粒度（即数据不用按照细分属性进行group by）；
    ) throws Exception {
        //校验
        judge(start_time, end_time, time, values, relations);
        //拼接sql
        StringBuffer sql = new StringBuffer();
        sql.append(SqlConst.SELECT);
        String dim_time = null;
        if (flag) {//需要将细分属性进行汇总
            dim_time = getDim(time);
            sql.append(dim_time);//获取需要统计的维度（时，日，周，月）
            sql.append(",");
        }
        sql.append(dim + " dim , ");//细分属性（按照这个字段进行分组）
        sql.append(SqlConst.PV_UV);//已经进行了重命名
        //from
        sql.append(SqlConst.FROM);
        //where
        sql.append(" where " + SqlConst.FILTER_TIME + " between " + start_time + " and " + end_time + " ");
        sql.append(getWhereCondition(values, relations));//过滤查询条件（and后面的内容，并且以and开头）
        //group  by
        sql.append(" group by " + dim + " ");
        if (flag) {//需要按照时间进行分组
            sql.append(" , " + dim_time);
        }
        //order by
        if (flag) {
            sql.append(" order by " + dim_time + " ");
        }
        //如果是周，并且按照细分属性，获取的数据粒度更细
        //则要将year_month（201705）和week_year（19）拼接起来,并且拼接成这种形式
        if (CommonConst.WEEK.equals(time) && flag) {
            sql = handlWeek(sql);
        }
        return sql.toString();
    }

    private static StringBuffer handlWeek(StringBuffer sql) {
        StringBuffer new_sql = new StringBuffer();
        new_sql.append(" select ");
        new_sql.append("    concat(concat(substring(YEAR_MONTH,1,4),'w'), week_year) update_time ,  ");
        new_sql.append("    dim , ");
        new_sql.append("    pv  , uv");//得到pv、uv
        new_sql.append(" from ( ");
        new_sql.append(sql);
        new_sql.append(" ) tmp ");
        return new_sql;
    }

    //校验time和flag的关系是否合法
    //当time有值，则flag必须为true
    //当time为null或者没有值，则flag必须为false
    private static void judge(
            String time,//时间间隔（只有4种值，时，日，周，月）
            Boolean flag
    ) throws Exception {
        if (time != null && time.trim().length() > 0) {//当dim有值
            if (!flag) {//则flag必须为true
                throw new Exception("参数异常");
            }
        } else {
            if (flag) {//则flag必须为false
                throw new Exception("参数异常");
            }
        }
    }

    //获取需要统计的维度（时，日，周，月）
    private static String getDim(String time) throws Exception {
        switch (time) {
            case CommonConst.HOUR:
                return SqlConst.SELECT_HOUR;
            case CommonConst.DAY:
                return SqlConst.SELECT_DAY;
            case CommonConst.WEEK:
                return SqlConst.SELECT_WEEK;
            case CommonConst.MONTH:
                return SqlConst.SELECT_MONTH;
            default://没有匹配上，则抛异常
                throw new Exception("参数异常");
        }
    }

    //校验规则：
    //     values和relations任何一个都必须有值，并且长度相同
    private static void judge(
            Map<String, String> values,
            Map<String, String> relations) throws Exception {
        Integer vSize = values.size();
        Integer rSize = values.size();
        if (values == null || relations == null) {
            throw new Exception("参数为空");
        }
        if (vSize == null || vSize == 0 || rSize == null || rSize == 0 ||
                vSize != rSize) {
            throw new Exception("数据长度有问题");
        }
    }

    //测试代码
    //效果column1   =   1  and  column3   =   '小王'  and  column2   in   ('aaa','bbb')
    public static void main(String[] args) throws Exception {
        Map<String, String> values = new HashMap<String, String>();
        Map<String, String> relations = new HashMap<String, String>();
        values.put("column1", "1");
        values.put("column2", "('aaa','bbb')");
        values.put("column3", "'小王'");
        relations.put("column1", LogicalConst.EQUALS);
        relations.put("column2", LogicalConst.IN);
        relations.put("column3", LogicalConst.EQUALS);
        System.out.println(getWhereCondition(values, relations));
    }

    private static void judge(
            String start_time,
            String end_time,
            String time,
            Map<String, String> values,
            Map<String, String> relations) throws Exception {
        if (start_time == null || start_time.trim().length() <= 0 ||
                end_time == null || end_time.trim().length() <= 0 ||
                values == null || values.size() <= 0 ||
                relations == null || relations.size() <= 0) {
            throw new Exception("参数异常");
        }
    }

    public static Object[] getValues(
            String pkg_source,//数据源(数据库值)
            String pro_id,//产品模块(数据库值)
            String event_id,//事件名称(数据库值)
            String condition_dim,//筛选条件维度英文名
            String condition_relation,//筛选条件关系符
            String condition_value,//筛选条件过滤值
            String dim,//细分属性列英文名
            String dim_value,//细分属性过滤值
            Boolean button_query) {//true：点击按钮的查询；false：点击的是中间的过滤条件进行筛选的查询
        /*准备容器*/
        Object[] back = new Object[2];
        //key：列名；value：过滤值
        Map<String, String> values = new HashMap<String, String>();
        //key：列名；value：关系运算符
        Map<String, String> relations = new HashMap<String, String>();
        /*装载*/
        //数据源（日志没有这个先不用过滤）
//        values.put(KylinConst.PKG_SOURCE, pkg_source);
//        if(pkg_source.split(",").length==2){//表示是这种数据"('yinhe','tencent')"，所以要用in
//            relations.put(KylinConst.PKG_SOURCE, LogicalConst.IN);
//        }else{
//            relations.put(KylinConst.PKG_SOURCE, LogicalConst.EQUALS);
//        }
        //产品模块
        values.put(KylinConst.DATA_SOURCE, pro_id);
        relations.put(KylinConst.DATA_SOURCE, LogicalConst.EQUALS);
        //事件名称
        values.put(KylinConst.DATA_SOURCE_TYPE, event_id);
        relations.put(KylinConst.DATA_SOURCE_TYPE, LogicalConst.EQUALS);
        //筛选条件
        if (condition_dim != null && condition_dim.trim().length() > 0) {  //筛选条件有值时才过滤
            values.put(condition_dim, condition_value);
            relations.put(condition_dim, condition_relation);
        }
        //细分属性
        if (!button_query) {//表示点击的是中间的过滤条件进行筛选的查询，所以需要过滤细分属性
            values.put(dim, dim_value);
            relations.put(dim, LogicalConst.IN);
        }
        back[0] = values;
        back[1] = relations;
        return back;
    }
}
