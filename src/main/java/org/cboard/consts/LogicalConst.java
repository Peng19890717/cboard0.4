package org.cboard.consts;

/**
 * 逻辑运算符
 * 用于动态拼接sql
 * Created by Administrator on 2017/4/26 0026.
 */
public interface LogicalConst {

    String NOTEQUALS = " != ";      //不等于
    String EQUALS = " = ";          //等于
    String IN = " in ";             //等于
    String GREATER = " > ";         //大于
    String GREATER_EQUALS = " >= ";//大于等于
    String SMALLER = " < ";         //小于
    String SMALLER_EQUALS = " <= ";//小于等于

    String LIKE = " like ";              //包含
    String NOTLIKE = " not like ";      //不包含
    String ISNULL = " is null ";        //空（对应的值传空字符串）
    String ISNOTNULL = " is not null ";//不为空（对应的值传空字符串）

}
