package com.mi5.bakeryappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.model.SchemeDetails;
import com.mi5.bakeryappnew.model.SellReportDetails;
import com.mi5.bakeryappnew.model.SellReportItemDetails;

import java.util.List;

public class SellReportItemAdapter extends BaseAdapter
{
    Context mContext;
    LayoutInflater inflater;

    SellReportItemDetails sellReportDetails;
    List<SellReportItemDetails> sellReportDetailsList;
    static int srNo=0;

    public SellReportItemAdapter(Context context, List<SellReportItemDetails> sellReportDetailsList)
    {
        this.mContext = context;
        this.sellReportDetailsList = sellReportDetailsList;
    }

    @Override
    public int getCount() {
        return sellReportDetailsList.size();
    }

    @Override
    public Object getItem(int i) {

        return sellReportDetailsList.get(i); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.custom_sell_report_list_item, parent, false);
        }

        TextView txtOrderCount = convertView.findViewById(R.id.txtOrderCount);
        TextView txtTotalOrderAmount = convertView.findViewById(R.id.txtTotalOrderAmount);
        TextView txtPaymentMode = convertView.findViewById(R.id.txtPaymentMode);

        LinearLayout countLinear = convertView.findViewById(R.id.countLinear);
        RelativeLayout toprelative = convertView.findViewById(R.id.toprelative);
        RelativeLayout bottomRelative = convertView.findViewById(R.id.bottomRelative);
        RelativeLayout errorRelative = convertView.findViewById(R.id.errorRelative);

        SellReportItemDetails sellReportDetails = (SellReportItemDetails) getItem(position);

        if(sellReportDetails.getMsg().equals("0"))
        {
            errorRelative.setVisibility(View.VISIBLE);
            toprelative.setVisibility(View.GONE);
            bottomRelative.setVisibility(View.GONE);
            countLinear.setVisibility(View.GONE);
        }
        else
        {
            srNo = srNo+1;
            errorRelative.setVisibility(View.GONE);
            toprelative.setVisibility(View.VISIBLE);
            bottomRelative.setVisibility(View.VISIBLE);
            countLinear.setVisibility(View.VISIBLE);

            txtOrderCount.setText(String.valueOf(position+1));
            txtTotalOrderAmount.setText(sellReportDetails.getFinalBillAmount() + " "
                    + mContext.getResources().getString(R.string.rs));
            txtPaymentMode.setText(sellReportDetails.getPaymentType());
        }

        return convertView;
    }
}
