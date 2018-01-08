package org.cboard.pojo;

/**
 * Created by yfyuan on 2016/8/26.
 */
public class DashboardCategory {

    private Long id;
    private String userId;
    private String name;
    private Integer sort;//一级标签排序

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

}
