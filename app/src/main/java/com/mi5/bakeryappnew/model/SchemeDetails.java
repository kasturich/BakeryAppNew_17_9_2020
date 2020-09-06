package com.mi5.bakeryappnew.model;

public class SchemeDetails {

    public String CompanyId, SchemeId, CategoryId, Category, OutletId, OutletName,
            Discount, FromDate, ToDate, msg;

    public SchemeDetails(String CompanyId, String SchemeId, String CategoryId,
                         String Category, String OutletId, String OutletName, String Discount,
                         String FromDate, String ToDate, String msg)
    {
        this.CompanyId = CompanyId;
        this.SchemeId = SchemeId;
        this.CategoryId = CategoryId;
        this.Category = Category;
        this.OutletId = OutletId;
        this.OutletName = OutletName;
        this.Discount = Discount;
        this.FromDate = FromDate;
        this.ToDate = ToDate;
        this.msg = msg;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getCompanyId() {
        return CompanyId;
    }

    public void setCompanyId(String companyId) {
        CompanyId = companyId;
    }

    public String getSchemeId() {
        return SchemeId;
    }

    public void setSchemeId(String schemeId) {
        SchemeId = schemeId;
    }

    public String getCategoryId() {
        return CategoryId;
    }

    public void setCategoryId(String categoryId) {
        CategoryId = categoryId;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getOutletId() {
        return OutletId;
    }

    public void setOutletId(String outletId) {
        OutletId = outletId;
    }

    public String getOutletName() {
        return OutletName;
    }

    public void setOutletName(String outletName) {
        OutletName = outletName;
    }

    public String getFromDate() {
        return FromDate;
    }

    public void setFromDate(String fromDate) {
        FromDate = fromDate;
    }

    public String getToDate() {
        return ToDate;
    }

    public void setToDate(String toDate) {
        ToDate = toDate;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
