package com.mi5.bakeryappnew.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.model.SchemeDetails;
import com.mi5.bakeryappnew.model.SchemeDetails;
import com.mi5.bakeryappnew.other.SchemeIdInterface;

import java.util.List;

public class ConfirmOrderSchemeRecyclerAdapter
        extends RecyclerView.Adapter<ConfirmOrderSchemeRecyclerAdapter.OrderViewHolder> {

    private Context context;
    private List<SchemeDetails> schemeList;
    private int checkedPosition = 0;
    int selected_position;
    onItemClickListner onItemClickListner;
    private int selectedPos = 0;
    private boolean clickable = true;
    SchemeDetails orderItem;
    String schemeIds="0";
    SchemeIdInterface schemeIdInterface;
    String str;

    public ConfirmOrderSchemeRecyclerAdapter(Context context,
                                             List<SchemeDetails> schemeList) {
        this.context = context;
        this.schemeList = schemeList;
        //this.onItemClickListner = onItemClickListner;
    }

    public interface onItemClickListner{
        void onClick(String str);//pass your object types.
    }
    
    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_show_scheme_list_item, parent, false);
        return new OrderViewHolder(view);
    }

    public void setOnItemClickListner(ConfirmOrderSchemeRecyclerAdapter.onItemClickListner
                                              onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
    }

    @Override
    public void onBindViewHolder(final OrderViewHolder holder, final int position) {
        orderItem = schemeList.get(position);

        holder.txtItemCategory.setText(orderItem.getCategory());
        holder.txtSchemeAmount.setText(orderItem.getDiscount() + " % discount");
        holder.txtSchemeId.setText(orderItem.getSchemeId());

        //holder.bind(orderItem);
        System.out.println("holder.getAdapterPosition() " + holder.getAdapterPosition());

        final String id = orderItem.getSchemeId();

        holder.schemeDetailsRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    System.out.println("scheme id " + schemeList.get(position).getSchemeId());

                    //schemeIds =  schemeList.get(position).getSchemeId()+ "/"+schemeIds;

                    schemeIds =  schemeIds  + "/" + schemeList.get(position).getSchemeId()
                            + "_" + schemeList.get(position).getDiscount()
                            + "_" + schemeList.get(position).getCategoryId();

                    System.out.println("schemeIds " + schemeIds);

                    System.out.println("selected scheme details "
                            + schemeIds);

                    /*str = orderItem.getSchemeId()
                            + "_" + orderItem.getDiscount()
                            + "_" + orderItem.getCategoryId();*/

                    onItemClickListner.onClick(schemeList.get(position).getSchemeId()
                            + "_" + schemeList.get(position).getDiscount()
                            + "_" + schemeList.get(position).getCategoryId());

                    //onItemClickListner.onClick(schemeIds);

                    //onItemClickListner.onClick(str);

                    Toast.makeText(context,
                            "scheme for " + schemeList.get(position).getCategory()
                                    + " is selected ", Toast.LENGTH_SHORT).show();

                    //holder.schemeDetailsRelative.setClickable(false);
                }
                catch (Exception e)
                {
                    e.getMessage();
                    System.out.println("error " + e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return schemeList.size();
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
        notifyDataSetChanged();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemCategory, txtSchemeAmount, txtSchemeId;
        RelativeLayout schemeDetailsRelative;

        OrderViewHolder(View itemView) {
            super(itemView);
            txtItemCategory = itemView.findViewById(R.id.txtItemCategory);
            txtSchemeAmount = itemView.findViewById(R.id.txtSchemeAmount);
            txtSchemeId = itemView.findViewById(R.id.txtSchemeId);
            schemeDetailsRelative = itemView.findViewById(R.id.schemeDetailsRelative);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    onItemClickListner.onClick(orderItem.getSchemeId()
                            + "_" + orderItem.getDiscount()
                            + "_" + orderItem.getCategoryId());

                    Toast.makeText(context,
                            "item selected", Toast.LENGTH_SHORT).show();
                }
            });*/
        }
    }
}
