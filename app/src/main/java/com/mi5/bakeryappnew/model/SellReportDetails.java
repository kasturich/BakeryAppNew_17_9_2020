package com.mi5.bakeryappnew.model;

public class SellReportDetails {

    public String outletId, DailyBillAmount, FinalBillAmount, TotalDailyDiscount, TotalOnlinePayment,
            TotalCashPayment, TotalCardPayment, DailyBillCount, msg, PaymentType;

    public SellReportDetails(String outletId, String DailyBillAmount, String FinalBillAmount,
                             String TotalDailyDiscount, String TotalOnlinePayment,
                             String TotalCashPayment, String TotalCardPayment,
                             String DailyBillCount, String msg, String PaymentType)
    {
        this.outletId = outletId;
        this.DailyBillAmount = DailyBillAmount;
        this.FinalBillAmount = FinalBillAmount;
        this.TotalDailyDiscount = TotalDailyDiscount;
        this.TotalOnlinePayment = TotalOnlinePayment;
        this.TotalCashPayment = TotalCashPayment;
        this.TotalCardPayment = TotalCardPayment;
        this.DailyBillCount = DailyBillCount;
        this.msg = msg;
        this.PaymentType = PaymentType;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public String getDailyBillAmount() {
        return DailyBillAmount;
    }

    public void setDailyBillAmount(String dailyBillAmount) {
        DailyBillAmount = dailyBillAmount;
    }

    public String getFinalBillAmount() {
        return FinalBillAmount;
    }

    public void setFinalBillAmount(String finalBillAmount) {
        FinalBillAmount = finalBillAmount;
    }

    public String getTotalDailyDiscount() {
        return TotalDailyDiscount;
    }

    public void setTotalDailyDiscount(String totalDailyDiscount) {
        TotalDailyDiscount = totalDailyDiscount;
    }

    public String getTotalOnlinePayment() {
        return TotalOnlinePayment;
    }

    public void setTotalOnlinePayment(String totalOnlinePayment) {
        TotalOnlinePayment = totalOnlinePayment;
    }

    public String getTotalCashPayment() {
        return TotalCashPayment;
    }

    public void setTotalCashPayment(String totalCashPayment) {
        TotalCashPayment = totalCashPayment;
    }

    public String getTotalCardPayment() {
        return TotalCardPayment;
    }

    public void setTotalCardPayment(String totalCardPayment) {
        TotalCardPayment = totalCardPayment;
    }

    public String getDailyBillCount() {
        return DailyBillCount;
    }

    public void setDailyBillCount(String dailyBillCount) {
        DailyBillCount = dailyBillCount;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
