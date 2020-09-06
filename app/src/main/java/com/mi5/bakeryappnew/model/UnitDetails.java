package com.mi5.bakeryappnew.model;

public class UnitDetails {
    public String UnitId, Unit, CompanyId, msg;

    public UnitDetails(String UnitId, String Unit, String CompanyId, String msg)
    {
        this.UnitId = UnitId;
        this.Unit = Unit;
        this.CompanyId = CompanyId;
        this.msg = msg;
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
