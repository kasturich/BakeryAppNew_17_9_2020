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
import com.mi5.bakeryappnew.model.FinalOrderItem;

import java.util.List;

public class FinalOrderRecyclerAdapter extends RecyclerView.Adapter<FinalOrderRecyclerAdapter.OrderViewHolder> {

    private Context context;
    TextView txtTotalQuantity, txtTotalAmount;
    EditText edtEditQuantity;
    ImageView imgRemoveItem;
    private List<FinalOrderItem> orderItemList;
    FinalOrderItem orderItem;
    int totalItems =0, itemQuantity =0;
    double changedPrice = 0.0;
    public boolean isClickable = true;

    public FinalOrderRecyclerAdapter(Context context,
                                     List<FinalOrderItem> orderItemList) {
        this.context = context;
        this.orderItemList = orderItemList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_recycler_final_list_item, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, final int position) {
        orderItem = orderItemList.get(position);

        holder.txtItemName.setText(orderItem.getItemName());
        holder.edtEditQuantity.setText(orderItem.getItemQuantity());
        holder.txtItemCategoryId.setText(orderItem.getCatId());

        /*double itemPrice = Integer.parseInt(orderItem.getItemQuantity())
                * Double.parseDouble(orderItem.getItemPrice());*/

        holder.txtItemPrice.setText(orderItem.getItemPrice());

        System.out.println("1. adapter changed quantity " + orderItem.getItemQuantity());
        System.out.println("1. adapter changed price " + orderItem.getItemPrice());
    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemName, txtItemPrice, txtItemCategoryId;
        TextView edtEditQuantity;
        int prevTotalItem = 0;

        OrderViewHolder(View itemView) {
            super(itemView);
            txtItemName = itemView.findViewById(R.id.txtItemName);
            edtEditQuantity = itemView.findViewById(R.id.edtEditQuantity);
            txtItemPrice = itemView.findViewById(R.id.txtItemPrice);
            txtItemCategoryId = itemView.findViewById(R.id.txtItemCategoryId);
        }

        private void removeItem(int position) {
            int newPosition = getAdapterPosition();
            orderItemList.remove(newPosition);
            notifyItemRemoved(newPosition);
            notifyItemRangeChanged(newPosition, orderItemList.size());
        }
    }
}
