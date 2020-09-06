package com.mi5.bakeryappnew.model;

public class LoginDetails {

    public String UserName, Password, OutletId, OutletName, Address, MobileNo, CompanyId, msg;

    public LoginDetails(String UserName, String Password, String OutletId, String OutletName,
                        String Address, String MobileNo, String CompanyId, String msg)
    {
        this.UserName = UserName;
        this.Password = Password;
        this.OutletId = OutletId;
        this.OutletName = OutletName;
        this.Address = Address;
        this.MobileNo = MobileNo;
        this.CompanyId = CompanyId;
        this.msg = msg;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
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

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getMobileNo() {
        return MobileNo;
    }

    public void setMobileNo(String mobileNo) {
        MobileNo = mobileNo;
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
