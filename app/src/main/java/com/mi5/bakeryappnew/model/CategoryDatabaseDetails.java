package com.mi5.bakeryappnew.model;

public class CategoryDatabaseDetails {
    public String id, CategoryId, Category, CompanyId, msg;

    public CategoryDatabaseDetails(String id, String CategoryId, String Category, String CompanyId)
    {
        this.id = id;
        this.CategoryId = CategoryId;
        this.Category = Category;
        this.CompanyId = CompanyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String CategoryId) {
        CategoryId = CategoryId;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        Category = Category;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(String companyId) {
        CompanyId = companyId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
