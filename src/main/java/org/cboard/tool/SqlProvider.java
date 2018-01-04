package org.cboard.tool;
/**
 * Created by Administrator on 2017/5/6 0006.
 */

import org.cboard.consts.DataSourceConst;

/**
 * 专门提供Sql的工具
 *
 * @author wbc
 * @date 2017-05-06
 **/
public class SqlProvider {

    /**
     * sql转型（将sql转为下面的形式）
     * "{\"sql\":\"select city_code , city_name from area where province_name = '山东省'\"}"
     * 注意：
     * 这个工具很多方法都会使用
     *
     * @param sql
     * @return
     */
    public static String exchangeType(String sql) {
        StringBuffer result = new StringBuffer();
        result.append("{\"sql\":\"");
        result.append(sql);
        result.append("\"}");
        return result.toString();
    }

    /**
     * sql转型（将数组里面的每个sql转为下面的形式）
     * "{\"sql\":\"select city_code , city_name from area where province_name = '山东省'\"}"
     * 注意：
     * 这个工具很多方法都会使用
     *
     * @param sql_array 里面存放的是sql
     * @return 返回一个新的数组
     */
    public static String[] exchangeArrayType(String[] sql_array) {
        if (sql_array == null || sql_array.length <= 0) {
            return new String[0];
        }
        int count = sql_array.length;
        String[] back = new String[count];
        for (int i = 0; i < count; i++) {
            back[i] = exchangeType(sql_array[i]);
        }
        return back;
    }


    /**
     * 产品模块、事件名称、筛选条件联动查询功能SQL
     * 通过产品unique_id，获取对应的事件中文名+英文名
     *
     * @param pro_id 产品unique_id，对应access_platform.pro_product的pro_unique_id
     * @return
     */
    public static String getEventDatasSql(String pro_id) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select ");
        sql.append("       distinct ");
        sql.append("        concat('''',a.event_name,'''') log_name , ");/*事件id*/
        sql.append("       b.log_desc log_desc ");/*事件中文名*/
        sql.append(" from " + DataSourceConst.DIM_ENUM + ".dashboard_enum a , ");
        sql.append("      " + DataSourceConst.ACCESS_PLATFORM + ".pro_log b  ");/*日志档案*/
        sql.append(" where a.event_name = b.log_name ");
        sql.append(" and a.pro_module = " + pro_id + " ");
        return sql.toString();
    }

    /**
     * 产品模块、事件名称、筛选条件联动查询功能SQL
     * 通过产品unique_id，获取对应的事件中文名+英文名
     *
     * @param pro_id   这种形式   '值'
     * @param event_id 这种形式   '值'
     * @return
     */
    public static String getLinkageDatasSql(String pro_id, String event_id) {
        StringBuffer sql = new StringBuffer();
        sql.append(" select ");
        sql.append("    distinct ");
        sql.append("    a.dim_column , ");/*维度列（英文名）*/
        sql.append("    d.dim_column_name , ");/*维度列中文名*/
        sql.append("    concat('''',a.dim_column_value,'''') dim_column_value ");/*维度列对应的值*/
        sql.append(" from " + DataSourceConst.DIM_ENUM + ".dashboard_enum a , ");
        sql.append("      " + DataSourceConst.DIM_ENUM + ".dashboard_dim d , ");/*kylin的base_clog_view表的维度档案表*/
        sql.append("      " + DataSourceConst.ACCESS_PLATFORM + ".pro_log b  ");/*日志档案*/
        sql.append(" where a.event_name = b.log_name ");
        sql.append(" and a.dim_column=d.dim_column ");
        sql.append(" and dim_column_value != '' and dim_column_value is not null  ");//去掉没有值的行（埋点的问题）
        sql.append(" and b.log_name = " + event_id + " ");/*事件英文名*/
        sql.append(" and a.pro_module = " + pro_id + " ");/*产品英文名*/
        sql.append(" and a.dim_column <> 'uid' ");//去掉uid的值（这个用于判断登录状态的，但是在这里查询出来没用）
        return sql.toString();
    }

}
