package com.mi5.bakeryappnew.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.model.ItemDatabaseDetails;
import com.mi5.bakeryappnew.model.ItemDetails;
import com.mi5.bakeryappnew.model.SchemeDetails;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ItemDatabaseDetailsAdapter extends BaseAdapter
{
    Context mContext;
    LayoutInflater inflater;

    ItemDatabaseDetails itemDatabaseDetails;
    List<ItemDatabaseDetails> itemDetailsList;

    String itemName, strTotalItemQuantity, keyItem, valueItem;
    int itemCount=0, totalQuantity=0;

    String valueItemSeparator[];

    Map<String, String> map = new TreeMap<String, String>();
    Set keys;
    Iterator ii;

    public ItemDatabaseDetailsAdapter(Context context, List<ItemDatabaseDetails> itemDetailsList)
    {
        this.mContext = context;
        this.itemDetailsList = itemDetailsList;
    }

    @Override
    public int getCount() {
        return itemDetailsList.size();
    }

    @Override
    public Object getItem(int i) {

        return itemDetailsList.get(i); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).
                    inflate(R.layout.custom_generate_list_item, parent, false);
        }

        TextView txtItemName = convertView.findViewById(R.id.txtItemName);
        TextView txtItemCategory = convertView.findViewById(R.id.txtItemCategory);
        TextView txtItemAmount = convertView.findViewById(R.id.txtItemAmount);
        TextView txtItemUnit = convertView.findViewById(R.id.txtItemUnit);
        TextView txtAddButton = convertView.findViewById(R.id.txtAddButton);

        RelativeLayout discountDateRelative = convertView.findViewById(R.id.discountDateRelative);
        RelativeLayout itemDiscountDetailsRelative = convertView.findViewById(R.id.itemDiscountDetailsRelative);
        RelativeLayout itemCategoryDetailsRelative = convertView.findViewById(R.id.itemCategoryDetailsRelative);
        RelativeLayout outletDetailsRelative = convertView.findViewById(R.id.outletDetailsRelative);
        RelativeLayout errorRelative = convertView.findViewById(R.id.errorRelative);
        RelativeLayout addButtonRelative = convertView.findViewById(R.id.addButtonRelative);

        itemDatabaseDetails = (ItemDatabaseDetails) getItem(position);

        if(itemDatabaseDetails.getItemId().equals("0"))
        {
            errorRelative.setVisibility(View.VISIBLE);
            addButtonRelative.setVisibility(View.GONE);
            itemDiscountDetailsRelative.setVisibility(View.GONE);
            itemCategoryDetailsRelative.setVisibility(View.GONE);
            outletDetailsRelative.setVisibility(View.GONE);
        }
        else
        {
            errorRelative.setVisibility(View.GONE);
            itemDiscountDetailsRelative.setVisibility(View.VISIBLE);
            itemCategoryDetailsRelative.setVisibility(View.VISIBLE);
            outletDetailsRelative.setVisibility(View.VISIBLE);
            addButtonRelative.setVisibility(View.VISIBLE);

            txtItemName.setText(itemDatabaseDetails.getItemName());
            txtItemCategory.setText(itemDatabaseDetails.getCategory());
            txtItemAmount.setText(itemDatabaseDetails.getAmount());
            txtItemUnit.setText(itemDatabaseDetails.getUnit());

            /*txtAddButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("add button item id " + itemDetailsList.get(position).getItemId());

                    if(map.size() == 0)
                    {
                        itemCount = itemCount+1;
                        itemName = itemDetailsList.get(position).getItemName();
                        strTotalItemQuantity = itemName +"_"+ String.valueOf(itemCount);
                        System.out.println("strTotalItemQuantity " + strTotalItemQuantity);
                        //adding total item quantity into the map with item name
                        map.put(itemName, strTotalItemQuantity);
                        keys = map.keySet();
                        System.out.println("1. size of key " + keys.size());
                        ii = keys.iterator();
                    }
                    else {
                        for (int i = 0; i <= map.size(); i++) {
                            while (ii.hasNext()) {
                                keyItem = (String) ii.next();
                                valueItem = map.get(keyItem).toString();
                                System.out.println(" on button click in for = " + " key = " + keyItem
                                        + ", value = " + valueItem);
                                valueItemSeparator = valueItem.split("_");

                                if(valueItemSeparator[0].equals(itemDetailsList.get(position).getItemName()))
                                {
                                    itemCount = itemCount+1;
                                    itemName = itemDetailsList.get(position).getItemName();
                                    strTotalItemQuantity = itemName +"_"+ String.valueOf(itemCount);

                                    System.out.println("strTotalItemQuantity in If " + strTotalItemQuantity);

                                    //adding total item quantity into the map with item name
                                    map.put(itemName, strTotalItemQuantity);
                                    keys = map.keySet();
                                    System.out.println("size of key in If " + keys.size());
                                    ii = keys.iterator();
                                }
                                else
                                {
                                    itemCount = 0;
                                    itemCount = itemCount+1;
                                    itemName = itemDetailsList.get(position).getItemName();
                                    strTotalItemQuantity = itemName +"_"+ String.valueOf(itemCount);

                                    System.out.println("strTotalItemQuantity in else " + strTotalItemQuantity);

                                    //adding total item quantity into the map with item name
                                    map.put(itemName, strTotalItemQuantity);
                                    keys = map.keySet();
                                    System.out.println("1. size of key in else " + keys.size());
                                    ii = keys.iterator();
                                }
                            }
                        }
                    }
                }
            });*/
        }
        return convertView;
    }
}
