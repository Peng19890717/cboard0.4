package org.cboard.consts;
/**
 * Created by Administrator on 2017/5/15 0015.
 */

/**
 * 数据源
 *
 * @author wbc
 * @date 2017-05-15
 **/
public interface DataSourceConst {
    //本地的配置（仅仅张瑶使用）
//    Long MYSQL = 1L;//本地mysql数据库(对应Cboard这个数据库，要用到)
//    Long KYLIN = 2L;//要连接的麒麟
//    String DIM_ENUM="bi_cboard";
//    String ACCESS_PLATFORM = "access_platform";
    //正式环境的配置=测试环境的配置=本地
    Long MYSQL = 6L;//本地mysql数据库(对应Cboard这个数据库，要用到)
    Long KYLIN = 13L;//要连接的麒麟
    String DIM_ENUM = "Cboard";
    String ACCESS_PLATFORM = "access_platform";
}
