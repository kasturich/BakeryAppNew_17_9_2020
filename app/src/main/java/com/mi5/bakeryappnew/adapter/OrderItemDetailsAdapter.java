package com.mi5.bakeryappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.model.OrderItemDetails;
import com.mi5.bakeryappnew.model.SchemeDetails;

import java.util.List;

public class OrderItemDetailsAdapter extends BaseAdapter
{
    Context mContext;
    LayoutInflater inflater;

    List<OrderItemDetails> orderItemDetailsList;

    public OrderItemDetailsAdapter(Context context, List<OrderItemDetails> orderItemDetailsList)
    {
        this.mContext = context;
        this.orderItemDetailsList = orderItemDetailsList;
    }

    @Override
    public int getCount() {
        return orderItemDetailsList.size();
    }

    @Override
    public Object getItem(int i) {

        return orderItemDetailsList.get(i); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.custom_order_details_list_item, parent, false);
        }

        TextView txtItemName = convertView.findViewById(R.id.txtItemName);
        TextView txtItemQuantity = convertView.findViewById(R.id.txtItemQuantity);
        TextView txtItemAmount = convertView.findViewById(R.id.txtItemAmount);

        OrderItemDetails orderItemDetails = (OrderItemDetails) getItem(position);

        if(orderItemDetails.getMsg().equals("0"))
        {

        }
        else
        {
            txtItemName.setText(orderItemDetails.getItemName());
            txtItemQuantity.setText(orderItemDetails.getItemQuantity());
            txtItemAmount.setText(orderItemDetails.getItemPrice());
        }

        return convertView;
    }
}
