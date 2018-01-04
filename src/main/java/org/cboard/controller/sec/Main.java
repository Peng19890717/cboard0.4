package org.cboard.controller.sec;/**
 * Created by Administrator on 2017/6/17 0017.
 */

import java.util.ArrayList;
import java.util.List;

/**
 * @author wbc
 * @date 2017-06-17
 **/
public class Main {

    public static String[] mytest_datas = new String[]{"1", "4", "8", "5", "9", "10", "6", "12", "2", "13", "7", "11", "3"};

    public static void main(String[] args) {
        List<String> datas = new ArrayList<String>();
        int count = mytest_datas.length;
        for (int i = 0; i < count; i++) {
            datas.add(mytest_datas[i]);
        }
        //测试函数式编程
        datas.parallelStream();

        datas.stream().filter(item ->
                Integer.valueOf(item) > 10
        ).forEach(item -> System.out.println(item));

        System.out.println(
                datas.stream().filter(
                        item -> Integer.valueOf(item) > 10
                ).count()
        );

//        //测试map
//        List<String> result = new ArrayList<>();
//        Object[] tmp = datas.stream().map(item -> {
//            if (Integer.valueOf(item) > 10) {
//                return item;
//            }else{
//                return null;
//            }
//        }).toArray();
//        for(Object o : tmp){
//            System.out.println(o);
//        }
    }

}
