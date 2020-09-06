package com.mi5.bakeryappnew.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.model.AddItemsForOrder;

import java.util.List;

public class ConfirmOrderRecyclerAdapter extends RecyclerView.Adapter<ConfirmOrderRecyclerAdapter.OrderViewHolder> {

    private Context context;
    TextView txtTotalQuantity, txtTotalAmount;
    EditText edtEditQuantity;
    ImageView imgRemoveItem;
    private List<AddItemsForOrder> orderItemList;
    AddItemsForOrder orderItem;
    int totalItems =0, itemQuantity =0;
    double changedPrice = 0.0;
    public boolean isClickable = true;

    public ConfirmOrderRecyclerAdapter(Context context,
                                       List<AddItemsForOrder> orderItemList,
                                       TextView txtTotalQuantity,
                                       TextView txtTotalAmount) {
        this.context = context;
        this.orderItemList = orderItemList;
        this.txtTotalQuantity = txtTotalQuantity;
        this.txtTotalAmount = txtTotalAmount;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_recycler_list_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, final int position) {
        orderItem = orderItemList.get(position);

        holder.txtItemName.setText(orderItem.getItemName());
        holder.edtEditQuantity.setText(orderItem.getItemQuantity());
        holder.txtItemCategoryId.setText(orderItem.getCatId());

        double itemPrice = Integer.parseInt(orderItem.getItemQuantity())
                * Double.parseDouble(orderItem.getItemPrice());

        holder.txtItemPrice.setText(String.valueOf(itemPrice));

        System.out.println("1. adapter changed quantity " + orderItem.getItemQuantity());
        System.out.println("1. adapter changed price " + orderItem.getItemPrice());

        holder.imgRemoveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(context,
                        "clicked for remove the item", Toast.LENGTH_SHORT).show();

                holder.removeItem(position);
                //notifyDataSetChanged();
                notifyItemRemoved(position);
            }
        });
    }

    public void setEnableView(View view) {
        System.out.println("isClickable " + isClickable);
        System.out.println("isClickable view " + view);
        //view.setEnabled(false);
        if(isClickable) {
            return;
        }
        else
        {
            view.setEnabled(false);
            view.setClickable(false);
            //edtEditQuantity.setEnabled(false);
        }
        // do your click stuff
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemName, txtItemPrice, txtItemCategoryId;
        ImageView imgRemoveItem;
        EditText edtEditQuantity;
        int prevTotalItem = 0;

        OrderViewHolder(View itemView) {
            super(itemView);
            txtItemName = itemView.findViewById(R.id.txtItemName);
            edtEditQuantity = itemView.findViewById(R.id.edtEditQuantity);
            txtItemPrice = itemView.findViewById(R.id.txtItemPrice);
            txtItemCategoryId = itemView.findViewById(R.id.txtItemCategoryId);
            imgRemoveItem = itemView.findViewById(R.id.imgRemoveItem);

            edtEditQuantity.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    System.out.println("before text change "
                            + orderItemList.get(getAdapterPosition()).getItemQuantity());

                    if(orderItemList.get(getAdapterPosition())
                            .getItemQuantity().equals(""))
                    {
                        prevTotalItem = 0;
                    }
                    else {
                        prevTotalItem = Integer.parseInt(orderItemList.get(getAdapterPosition())
                                .getItemQuantity());
                    }

                    System.out.println("item quantity in text changer " + prevTotalItem);
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    orderItemList.get(getAdapterPosition())
                            .setItemQuantity(edtEditQuantity.getText().toString());

                    System.out.println("on text change "
                    + orderItemList.get(getAdapterPosition()).getItemQuantity());
                }

                @Override
                public void afterTextChanged(Editable editable) {

                    System.out.println("order list changed quantity = "
                    + edtEditQuantity.getText().toString());

                    String itemPrice = orderItemList.get(getAdapterPosition())
                            .getItemPrice();

                    System.out.println("item price in text changer " + itemPrice);

                    if(edtEditQuantity.getText().toString().equals("")) {

                        changedPrice = 0 * Double.parseDouble(itemPrice);

                        /*orderItemList.get(getAdapterPosition())
                                .setItemPrice(String.valueOf(changedPrice));*/

                        txtItemPrice.setText(String.valueOf(changedPrice));
                    }
                    else
                    {
                        changedPrice = Integer.parseInt(edtEditQuantity.getText().toString())
                                * Double.parseDouble(itemPrice);

                        /*orderItemList.get(getAdapterPosition())
                                .setItemPrice(String.valueOf(changedPrice));*/

                        itemQuantity = Integer.parseInt(edtEditQuantity.getText().toString());

                        txtItemPrice.setText(String.valueOf(changedPrice));
                    }
                }
            });
        }

        private void removeItem(int position) {
            int newPosition = getAdapterPosition();
            orderItemList.remove(newPosition);
            notifyItemRemoved(newPosition);
            notifyItemRangeChanged(newPosition, orderItemList.size());
        }
    }
}
