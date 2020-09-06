package com.mi5.bakeryappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.model.SchemeDetails;

import java.util.List;

public class SchemeDetailsAdapter extends BaseAdapter
{
    Context mContext;
    LayoutInflater inflater;

    SchemeDetails schemeDetails;
    List<SchemeDetails> schemeDetailsList;

    public SchemeDetailsAdapter(Context context, List<SchemeDetails> schemeDetailsList)
    {
        this.mContext = context;
        this.schemeDetailsList = schemeDetailsList;
    }

    @Override
    public int getCount() {
        return schemeDetailsList.size();
    }

    @Override
    public Object getItem(int i) {

        return schemeDetailsList.get(i); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.custom_scheme_list_item, parent, false);
        }

        TextView txtOutletName = convertView.findViewById(R.id.txtOutletName);
        TextView txtItemCategory = convertView.findViewById(R.id.txtItemCategory);
        TextView txtItemDiscount = convertView.findViewById(R.id.txtItemDiscount);
        TextView txtDiscountFromDate = convertView.findViewById(R.id.txtDiscountFromDate);
        TextView txtDiscountToDate = convertView.findViewById(R.id.txtDiscountToDate);

        RelativeLayout discountDateRelative = convertView.findViewById(R.id.discountDateRelative);
        RelativeLayout itemDiscountDetailsRelative = convertView.findViewById(R.id.itemDiscountDetailsRelative);
        RelativeLayout itemCategoryDetailsRelative = convertView.findViewById(R.id.itemCategoryDetailsRelative);
        RelativeLayout outletDetailsRelative = convertView.findViewById(R.id.outletDetailsRelative);
        RelativeLayout errorRelative = convertView.findViewById(R.id.errorRelative);

        SchemeDetails schemeDetails = (SchemeDetails) getItem(position);

        if(schemeDetails.getSchemeId().equals("0"))
        {
            errorRelative.setVisibility(View.VISIBLE);
            discountDateRelative.setVisibility(View.GONE);
            itemDiscountDetailsRelative.setVisibility(View.GONE);
            itemCategoryDetailsRelative.setVisibility(View.GONE);
            outletDetailsRelative.setVisibility(View.GONE);
        }
        else
        {
            errorRelative.setVisibility(View.GONE);
            discountDateRelative.setVisibility(View.VISIBLE);
            itemDiscountDetailsRelative.setVisibility(View.VISIBLE);
            itemCategoryDetailsRelative.setVisibility(View.VISIBLE);
            //outletDetailsRelative.setVisibility(View.VISIBLE);

            //txtOutletName.setText(schemeDetails.getOutletName());
            txtItemCategory.setText(schemeDetails.getCategory());
            txtItemDiscount.setText(schemeDetails.getDiscount() + " %");
            txtDiscountFromDate.setText(schemeDetails.getFromDate());
            txtDiscountToDate.setText(schemeDetails.getToDate());
        }

        return convertView;
    }
}
