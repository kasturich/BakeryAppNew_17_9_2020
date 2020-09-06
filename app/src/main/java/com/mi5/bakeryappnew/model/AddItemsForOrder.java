package com.mi5.bakeryappnew.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class AddItemsForOrder  implements Parcelable {

    String itemId, itemName, itemQuantity, itemPrice, catId;

    public AddItemsForOrder(String itemId, String itemName,
                            String itemQuantity, String itemPrice, String catId)
    {
        this.itemId = itemId;
        this.itemName = itemName;
        this.itemQuantity = itemQuantity;
        this.itemPrice = itemPrice;
        this.catId = catId;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(String itemPrice) {
        this.itemPrice = itemPrice;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(String itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    // Parcelling part
    public AddItemsForOrder(Parcel in){
        String[] data = new String[3];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.itemId = data[0];
        this.itemName = data[1];
        this.itemQuantity = data[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.itemId,
                this.itemName,
                this.itemQuantity});
    }
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public AddItemsForOrder createFromParcel(Parcel in) {
            return new AddItemsForOrder(in);
        }

        public AddItemsForOrder[] newArray(int size) {
            return new AddItemsForOrder[size];
        }
    };


}
