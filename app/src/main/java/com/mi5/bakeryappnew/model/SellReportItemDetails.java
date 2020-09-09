package com.mi5.bakeryappnew.model;

public class SellReportItemDetails {

    public String outletId, OrderId, FinalBillAmount, msg, PaymentType;

    public SellReportItemDetails(String outletId, String OrderId, String FinalBillAmount,
                                 String msg, String PaymentType)
    {
        this.outletId = outletId;
        this.OrderId = OrderId;
        this.FinalBillAmount = FinalBillAmount;
        this.msg = msg;
        this.PaymentType = PaymentType;
    }

    public String getOutletId() {
        return outletId;
    }

    public void setOutletId(String outletId) {
        this.outletId = outletId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getFinalBillAmount() {
        return FinalBillAmount;
    }

    public void setFinalBillAmount(String finalBillAmount) {
        FinalBillAmount = finalBillAmount;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPaymentType() {
        return PaymentType;
    }

    public void setPaymentType(String paymentType) {
        PaymentType = paymentType;
    }
}
