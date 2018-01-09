package org.cboard.dao;

import org.cboard.pojo.DashboardCategory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by yfyuan on 2016/8/26.
 */
@Repository
public interface CategoryDao {

    List<DashboardCategory> getCategoryList();

    int save(DashboardCategory dashboardCategory);

    long countExistCategoryName(Map<String, Object> map);

    int update(DashboardCategory dashboardCategory);

    //modified by wbc 2018-01-09 start 更新sort字段的值
    int updateSort(DashboardCategory dashboardCategory);
    //modified by wbc 2018-01-09 end 更新sort字段的值


    int delete(Long id);
}
