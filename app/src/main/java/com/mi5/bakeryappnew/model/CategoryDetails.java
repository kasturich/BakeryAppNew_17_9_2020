package com.mi5.bakeryappnew.model;

public class CategoryDetails {
    public String CategoryId, Category, CompanyId, msg;

    public CategoryDetails(String CategoryId, String Category, String CompanyId, String msg)
    {
        this.CategoryId = CategoryId;
        this.Category = Category;
        this.CompanyId = CompanyId;
        this.msg = msg;
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
