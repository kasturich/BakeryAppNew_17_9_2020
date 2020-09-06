package com.mi5.bakeryappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.model.SchemeDetails;

import java.util.List;

public class ShowSchemeDetailsAdapter extends
        RecyclerView.Adapter<ShowSchemeDetailsAdapter.OrderViewHolder>
{

    private Context context;
    private List<SchemeDetails> schemeList;
    SchemeDetails orderItem;

    public ShowSchemeDetailsAdapter(Context context,
                                    List<SchemeDetails> schemeList) {
        this.context = context;
        this.schemeList = schemeList;
    }

    public interface onItemClickListner{
        void onClick(String str);//pass your object types.
    }

    @NonNull
    @Override
    public ShowSchemeDetailsAdapter.OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_show_scheme_list_item, parent, false);
        return new ShowSchemeDetailsAdapter.OrderViewHolder(view);
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
                    System.out.println("selected scheme details " + orderItem.getSchemeId()
                            + "_" + orderItem.getDiscount()
                            + "_" + orderItem.getCategoryId());

                    /*str = orderItem.getSchemeId()
                            + "_" + orderItem.getDiscount()
                            + "_" + orderItem.getCategoryId();*/

                    Toast.makeText(context,
                            "scheme for " + orderItem.getCategory()
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

    class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView txtItemCategory, txtSchemeAmount, txtSchemeId;
        RelativeLayout schemeDetailsRelative;

        OrderViewHolder(View itemView) {
            super(itemView);
            txtItemCategory = itemView.findViewById(R.id.txtItemCategory);
            txtSchemeAmount = itemView.findViewById(R.id.txtSchemeAmount);
            txtSchemeId = itemView.findViewById(R.id.txtSchemeId);
            schemeDetailsRelative = itemView.findViewById(R.id.schemeDetailsRelative);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Toast.makeText(context,
                            "item selected", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }



}
