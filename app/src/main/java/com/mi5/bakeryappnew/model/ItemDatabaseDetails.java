package com.mi5.bakeryappnew.model;

public class ItemDatabaseDetails {
    public String ItemId, ItemName, CompanyId, id,
            CategoryId, Category, UnitId, Unit, Amount;

    public ItemDatabaseDetails(String id, String ItemId, String ItemName, String Amount,
                               String CategoryId, String Category, String UnitId,
                               String Unit,  String CompanyId)
    {
        this.id = id;
        this.ItemId = ItemId;
        this.ItemName = ItemName;
        this.Amount = Amount;
        this.CategoryId = CategoryId;
        this.Category = Category;
        this.UnitId = UnitId;
        this.Unit = Unit;
        this.CompanyId = CompanyId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

}
