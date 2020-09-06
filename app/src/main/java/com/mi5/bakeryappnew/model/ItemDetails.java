package com.mi5.bakeryappnew.model;

public class ItemDetails {
    public String ItemId, ItemName, CompanyId, msg,
            CategoryId, Category, UnitId, Unit, Amount;

    public ItemDetails(String ItemId, String ItemName,
                       String CategoryId, String Category, String UnitId,
                       String Unit, String Amount, String CompanyId, String msg)
    {
        this.ItemId = ItemId;
        this.ItemName = ItemName;
        this.CategoryId = CategoryId;
        this.Category = Category;
        this.UnitId = UnitId;
        this.Unit = Unit;
        this.Amount = Amount;
        this.CompanyId = CompanyId;
        this.msg = msg;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String ItemId) {
        ItemId = ItemId;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String ItemName) {
        ItemName = ItemName;
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

    public String getUnitId() {
        return UnitId;
    }

    public void setUnitId(String unitId) {
        UnitId = unitId;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getAmount() {
        return Amount;
    }

    public void setAmount(String amount) {
        Amount = amount;
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
