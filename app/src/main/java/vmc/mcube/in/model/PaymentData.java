package vmc.mcube.in.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by gousebabjan on 16/12/16.
 */

public class PaymentData implements Parcelable{

    private String c_name,c_mail,c_phone,c_TId,c_Invoice;
    private double c_Amt;

    public double getC_Amt() {
        return c_Amt;
    }

    public void setC_Amt(double c_Amt) {
        this.c_Amt = c_Amt;
    }

    public String getC_name() {
        return c_name;
    }

    public void setC_name(String c_name) {
        this.c_name = c_name;
    }

    public String getC_mail() {
        return c_mail;
    }

    public void setC_mail(String c_mail) {
        this.c_mail = c_mail;
    }

    public String getC_phone() {
        return c_phone;
    }

    public void setC_phone(String c_phone) {
        this.c_phone = c_phone;
    }

    public String getC_TId() {
        return c_TId;
    }

    public void setC_TId(String c_TId) {
        this.c_TId = c_TId;
    }

    public String getC_Invoice() {
        return c_Invoice;
    }

    public void setC_Invoice(String c_Invoice) {
        this.c_Invoice = c_Invoice;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
