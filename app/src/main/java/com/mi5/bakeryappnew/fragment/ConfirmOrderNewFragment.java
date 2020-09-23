package com.mi5.bakeryappnew.fragment;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.activity.NavigationDrawerActivity;
import com.mi5.bakeryappnew.adapter.ConfirmOrderRecyclerAdapter;
import com.mi5.bakeryappnew.adapter.ConfirmOrderSchemeRecyclerAdapter;
import com.mi5.bakeryappnew.adapter.FinalOrderRecyclerAdapter;
import com.mi5.bakeryappnew.database.MyDBHandler;
import com.mi5.bakeryappnew.model.AddItemsForOrder;
import com.mi5.bakeryappnew.model.FinalOrderItem;
import com.mi5.bakeryappnew.model.ItemDatabaseDetails;
import com.mi5.bakeryappnew.model.SchemeDetails;
import com.mi5.bakeryappnew.other.BillDateDialogFragment;
import com.mi5.bakeryappnew.other.DateDialogFragment;
import com.mi5.bakeryappnew.utility.UrlStrings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfirmOrderNewFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConfirmOrderNewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmOrderNewFragment extends Fragment
implements ConfirmOrderSchemeRecyclerAdapter.onItemClickListner{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Bundle bundle = new Bundle();
    AddItemsForOrder addItemsForOrder, addItemsForBackOrder, tempFinalItemOrder, finalItemOrder,
    finalItemOrderWithDiscount, schemeDiscount, schemeNonDiscount;
    List<AddItemsForOrder> addItemsForOrderList;
    ArrayList<AddItemsForOrder> addItemsForBackOrderList;
    ArrayList<AddItemsForOrder> addItemsForOrdersParcelableList;
    List<AddItemsForOrder> tempItemList;
    List<AddItemsForOrder> finalItemList;
    List<AddItemsForOrder> finalItemOrderWithDiscountList;
    List<AddItemsForOrder> schemeDiscountList;
    List<AddItemsForOrder> schemeNonDiscountList;
    List<AddItemsForOrder> tempFinalItemOrderList;
    ItemDatabaseDetails itemDatabaseDetails;
    List<ItemDatabaseDetails> itemDatabaseDetailsList;
    List<String> catIdList;

    boolean isDiscount = false;

    JSONArray req = new JSONArray();
    JSONObject reqObj = null;

    SharedPreferences sharedpreferences;
    String jsonSharedPreference, companyId, outletId;
    boolean hasLoggedIn, downloadData;
    String strGuestName, strGuestContactNo, strGuestAddress;

    TextView txtDate, txtTime, btnConfirm,
            txtTotalAmount, txtTotalQuantity,
            btnTempBill, txtContinueOrderingTitle, txtGetSchemesTitle;
    EditText edtGuestName, edtGuestContactNo, edtGuestAddress;
    RecyclerView orderedItemRecyclerList, finalItemRecyclerListView;
    RecyclerView schemeListView;
    ProgressBar forgotPasswordProgressBar;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    ProgressBar schemeProgressBar;
    RadioGroup paymentModeRadioGroup;
    RadioButton paymentModeCash, paymentModeCard, paymentModeOnline, radioButton;

    String strItemQuantity;
    String strItemPrice;
    String strItemName;
    String strItemCategoryId;

    int totalItems =0 ;
    double strTotalAmount =0.0, discountOnItemPrice=0.0, totalAmount=0.0,
            discountedAmount, nonDiscountedAmount, discountAddition=0.0, nonDiscountAddition=0.0,
    noSchemeCatIdAmount=0.0;

    String schemeAmount="0.0", schemeCategoryId, strPaymentMode;
    String[] mainSchemeId, schemeId;
    String selectedSchemeId="0", finalSelectedSchemeId, finalDiscountedAmount;

    String strSelectedSchemeId;

    ConfirmOrderRecyclerAdapter confirmOrderRecyclerAdapter;

    String strSchemeId, strCatId;
    List<String> tempSchemeIdList = new ArrayList<String>();
    List<String> schemeIdList = new ArrayList<String>();
    SchemeDetails schemeDetails;
    List<SchemeDetails> showSchemeList = new ArrayList<SchemeDetails>();
    List<SchemeDetails> schemeDetailsList = new ArrayList<SchemeDetails>();
    List<Date>inBetweenDateList = new ArrayList<Date>();
    ConfirmOrderSchemeRecyclerAdapter confirmOrderSchemeDetailsAdapter;

    FinalOrderItem finalOrderItem;
    List<FinalOrderItem> finalOrderItemList;
    FinalOrderRecyclerAdapter finalOrderRecyclerAdapter;

    int orderId = 0;
    SoapObject response, responseScheme;

    boolean serverConnection=false;

    public ConfirmOrderNewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConfirmOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ConfirmOrderNewFragment newInstance(String param1, String param2) {
        ConfirmOrderNewFragment fragment = new ConfirmOrderNewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    View mainView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_confirm_order, container, false);

        ((NavigationDrawerActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.nav_header_confirm_order));

        txtDate = mainView.findViewById(R.id.txtDate);
        txtTime = mainView.findViewById(R.id.txtTime);
        edtGuestName = mainView.findViewById(R.id.edtGuestName);
        edtGuestContactNo = mainView.findViewById(R.id.edtGuestContactNo);
        edtGuestAddress = mainView.findViewById(R.id.edtGuestAddress);
        orderedItemRecyclerList = mainView.findViewById(R.id.orderedItemRecyclerList);
        finalItemRecyclerListView = mainView.findViewById(R.id.finalItemRecyclerListView);
        btnConfirm = mainView.findViewById(R.id.btnConfirm);
        btnTempBill = mainView.findViewById(R.id.btnTempBill);
        txtTotalQuantity = mainView.findViewById(R.id.txtTotalQuantity);
        txtTotalAmount = mainView.findViewById(R.id.txtTotalAmount);
        schemeListView = mainView.findViewById(R.id.schemeListView);
        paymentModeRadioGroup = mainView.findViewById(R.id.paymentModeRadioGroup);
        paymentModeCash = mainView.findViewById(R.id.paymentModeCash);
        paymentModeCard = mainView.findViewById(R.id.paymentModeCard);
        paymentModeOnline = mainView.findViewById(R.id.paymentModeOnline);
        schemeProgressBar = mainView.findViewById(R.id.schemeProgressBar);
        txtContinueOrderingTitle = mainView.findViewById(R.id.txtContinueOrderingTitle);
        txtGetSchemesTitle = mainView.findViewById(R.id.txtGetSchemesTitle);

        bundle = this.getArguments();
        if (bundle != null) {
            addItemsForOrder = (AddItemsForOrder) bundle.getParcelable("addOrderItem");
            addItemsForOrdersParcelableList = bundle.getParcelableArrayList(
                    "addItemsForOrdersParcelableList");
        }
        System.out.println("addItemsForOrder " + addItemsForOrder);

        addItemsForOrderList = new ArrayList<AddItemsForOrder>();
        addItemsForBackOrderList = new ArrayList<AddItemsForOrder>();
        finalItemOrderWithDiscountList = new ArrayList<AddItemsForOrder>();
        schemeDiscountList = new ArrayList<AddItemsForOrder>();
        schemeNonDiscountList = new ArrayList<AddItemsForOrder>();
        finalOrderItemList = new ArrayList<FinalOrderItem>();
        //addItemsForOrderList.add(addItemsForOrder);
        addItemsForOrderList.add(addItemsForOrder);

        for(int i=0; i < addItemsForOrdersParcelableList.size(); i++)
        {
            System.out.println("i " + i);
            System.out.println("id in confirm order "
                    + addItemsForOrdersParcelableList.get(i).getItemId());
            System.out.println("count in confirm order "
                    + addItemsForOrdersParcelableList.get(i).getItemQuantity());
            System.out.println("amount in confirm order "
                    + addItemsForOrdersParcelableList.get(i).getItemPrice());
        }

        sharedpreferences = getActivity().getSharedPreferences("LoginDetails",
                Context.MODE_PRIVATE);

        hasLoggedIn = sharedpreferences.getBoolean("hasLoggedIn", false);
        downloadData = sharedpreferences.getBoolean("downloadData", false);

        jsonSharedPreference = sharedpreferences.getString("JSON", "");

        //System.out.println("downloadData" + downloadData);

        if (jsonSharedPreference != null) {
            try {
                JSONArray jarray = new JSONArray(jsonSharedPreference);
                //System.out.println("jarray length " + jarray.length());
                JSONObject json_obj = jarray.getJSONObject(0);
                for (int i=0; i<jarray.length(); i++)
                {
                    //System.out.println("personal info user_id " + user_id);
                    companyId = json_obj.getString("CompanyId");
                    outletId = json_obj.getString("OutletId");
                    //System.out.println("company id " + companyId);
                }

            } catch (JSONException e) {

                System.out.println("sharedpreference error = " + e.getMessage());
            }
        }

        itemDatabaseDetailsList = new ArrayList<ItemDatabaseDetails>();
        tempItemList = new ArrayList<AddItemsForOrder>();
        finalItemList = new ArrayList<AddItemsForOrder>();
        tempFinalItemOrderList = new ArrayList<AddItemsForOrder>();
        catIdList = new ArrayList<String>();

        btnConfirm.setEnabled(false);

        try {
            tempItemList = removeDuplicates(addItemsForOrdersParcelableList);
            schemeDiscountList = removeDuplicates(addItemsForOrdersParcelableList);

            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat timeDf = new SimpleDateFormat("HH:ss a", Locale.getDefault());
            String formattedDate = df.format(c);
            txtDate.setText(formattedDate);

            txtDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    BillDateDialogFragment dateDialogFragment = new BillDateDialogFragment();
                    dateDialogFragment.show(getActivity().getFragmentManager(), "Date Picker");
                }
            });

            Calendar cal = Calendar.getInstance();
            Date currentLocalTime = cal.getTime();
            DateFormat date = new SimpleDateFormat("hh:mm a", Locale.US);
            //you can get seconds by adding  "...:ss" to it
            //date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

            String localTime = date.format(currentLocalTime);
            txtTime.setText(localTime);

            txtGetSchemesTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    txtContinueOrderingTitle.setEnabled(false);
                    txtContinueOrderingTitle.setBackgroundColor(
                            getResources().getColor(R.color.horizontal_line));
                    txtContinueOrderingTitle.setTextColor(
                            getResources().getColor(R.color.dark_brown));
                    finalOrderItemList.clear();

                    for (int i = 0; i < tempItemList.size(); i++) {
                        view = orderedItemRecyclerList.getChildAt(i);
                        EditText itemQuantity = view.findViewById(R.id.edtEditQuantity);
                        TextView itemPrice = view.findViewById(R.id.txtItemPrice);
                        TextView itemName = view.findViewById(R.id.txtItemName);
                        TextView itemCategoryId = view.findViewById(R.id.txtItemCategoryId);

                        strItemQuantity = itemQuantity.getText().toString();
                        strItemPrice = itemPrice.getText().toString();
                        strItemName = itemName.getText().toString();
                        strItemCategoryId = itemCategoryId.getText().toString();

                        System.out.println("on scheme button click quantity " + strItemQuantity);
                        System.out.println("on scheme button click price " + strItemPrice);
                        System.out.println("on scheme button click name " + strItemName);
                        System.out.println("on scheme button click cat id " + strItemCategoryId);

                        finalOrderItem = new FinalOrderItem(
                                tempItemList.get(i).getItemId(),
                                strItemName,
                                strItemQuantity,
                                strItemPrice,
                                strItemCategoryId
                        );
                        finalOrderItemList.add(finalOrderItem);
                    }

                    finalOrderRecyclerAdapter = new FinalOrderRecyclerAdapter(getActivity().
                            getApplicationContext(), finalOrderItemList);
                    finalOrderRecyclerAdapter.notifyDataSetChanged();
                    finalItemRecyclerListView.setAdapter(finalOrderRecyclerAdapter);

                    orderedItemRecyclerList.setVisibility(View.GONE);
                    finalItemRecyclerListView.setVisibility(View.VISIBLE);

                    System.out.println("selected date " + txtDate.getText().toString());
                    if(isNetworkAvailable()) {
                        showSchemeList.clear();
                        schemeDetailsList.clear();
                        DownloadSchemeAsync downloadSchemeAsync = new DownloadSchemeAsync(txtDate.getText().toString());
                        downloadSchemeAsync.execute();
                    }
                    else
                    {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Please check internet connection.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            txtContinueOrderingTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(finalOrderItemList.size()>0)
                    {
                        for (int i = 0; i < tempItemList.size(); i++) {
                            view = finalItemRecyclerListView.getChildAt(i);
                            TextView itemQuantity = view.findViewById(R.id.edtEditQuantity);
                            TextView itemPrice = view.findViewById(R.id.txtItemPrice);
                            TextView itemName = view.findViewById(R.id.txtItemName);
                            TextView itemCategoryId = view.findViewById(R.id.txtItemCategoryId);

                            strItemQuantity = itemQuantity.getText().toString();
                            strItemPrice = itemPrice.getText().toString();
                            strItemName = itemName.getText().toString();
                            strItemCategoryId = itemCategoryId.getText().toString();

                            System.out.println("on button click quantity " + strItemQuantity);
                            System.out.println("on button click price " + strItemPrice);
                            System.out.println("on button click name " + strItemName);
                            System.out.println("on button click cat id " + strItemCategoryId);

                            totalItems = totalItems +
                                    Integer.parseInt(itemQuantity.getText().toString());

                            strTotalAmount = strTotalAmount +
                                    Double.parseDouble(itemPrice.getText().toString());

                            addItemsForBackOrder = new AddItemsForOrder(
                                    tempItemList.get(i).getItemId(),
                                    strItemName,
                                    strItemQuantity,
                                    strItemPrice,
                                    tempItemList.get(i).getCatId()
                            );
                            addItemsForBackOrderList.add(addItemsForBackOrder);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < tempItemList.size(); i++) {
                            view = orderedItemRecyclerList.getChildAt(i);
                            TextView itemQuantity = view.findViewById(R.id.edtEditQuantity);
                            TextView itemPrice = view.findViewById(R.id.txtItemPrice);
                            TextView itemName = view.findViewById(R.id.txtItemName);
                            TextView itemCategoryId = view.findViewById(R.id.txtItemCategoryId);

                            strItemQuantity = itemQuantity.getText().toString();
                            strItemPrice = itemPrice.getText().toString();
                            strItemName = itemName.getText().toString();
                            strItemCategoryId = itemCategoryId.getText().toString();

                            System.out.println("on button click quantity " + strItemQuantity);
                            System.out.println("on button click price " + strItemPrice);
                            System.out.println("on button click name " + strItemName);
                            System.out.println("on button click cat id " + strItemCategoryId);

                            totalItems = totalItems +
                                    Integer.parseInt(itemQuantity.getText().toString());

                            strTotalAmount = strTotalAmount +
                                    Double.parseDouble(itemPrice.getText().toString());

                            addItemsForBackOrder = new AddItemsForOrder(
                                    tempItemList.get(i).getItemId(),
                                    strItemName,
                                    strItemQuantity,
                                    strItemPrice,
                                    tempItemList.get(i).getCatId()
                            );
                            addItemsForBackOrderList.add(addItemsForBackOrder);
                        }
                    }


                    Bundle bundle = new Bundle();
                    bundle.putParcelable("addOrderBackItem", addItemsForBackOrder);
                    bundle.putParcelableArrayList("addBackOrderList", addItemsForBackOrderList);
                    Fragment fragment = new GenerateOrderNewFragment();
                    fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .addToBackStack("generateOrder")
                            .commit();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                        fragmentTransaction.addToBackStack("generateOrder");
                        fragmentTransaction.commit();

                        // set the toolbar title
                        // getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                        //toolbar_title.setText(getResources().getString(R.string.app_name));
                    }
                }
            });

            /*if(isNetworkAvailable()) {
                showSchemeList.clear();
                DownloadSchemeAsync downloadSchemeAsync = new DownloadSchemeAsync(txtDate.getText().toString());
                downloadSchemeAsync.execute();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Please check internet connection.", Toast.LENGTH_SHORT).show();
            }*/

            GridLayoutManager mGrid = new GridLayoutManager(getActivity().
                    getApplicationContext(), 1);
            orderedItemRecyclerList.setLayoutManager(mGrid);
            orderedItemRecyclerList.setHasFixedSize(true);

            GridLayoutManager mGrid1 = new GridLayoutManager(getActivity().
                    getApplicationContext(), 1);
            schemeListView.setLayoutManager(mGrid1);
            schemeListView.setHasFixedSize(true);

            GridLayoutManager mGrid2 = new GridLayoutManager(getActivity().
                    getApplicationContext(), 1);
            finalItemRecyclerListView.setLayoutManager(mGrid2);
            finalItemRecyclerListView.setHasFixedSize(true);

            confirmOrderRecyclerAdapter = new ConfirmOrderRecyclerAdapter(getActivity().
                    getApplicationContext(), tempItemList, txtTotalQuantity, txtTotalAmount);
            confirmOrderRecyclerAdapter.notifyDataSetChanged();
            orderedItemRecyclerList.setAdapter(confirmOrderRecyclerAdapter);

            //generateJSON();
            for(int i=0; i<tempItemList.size(); i++)
            {
                catIdList.add(tempItemList.get(i).getCatId());
            }

            btnTempBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    txtGetSchemesTitle.setEnabled(false);

                    txtGetSchemesTitle.setBackgroundColor(
                            getResources().getColor(R.color.horizontal_line));
                    txtGetSchemesTitle.setTextColor(
                            getResources().getColor(R.color.dark_brown));

                    txtContinueOrderingTitle.setEnabled(false);
                    txtContinueOrderingTitle.setBackgroundColor(
                            getResources().getColor(R.color.horizontal_line));
                    txtContinueOrderingTitle.setTextColor(
                            getResources().getColor(R.color.dark_brown));

                    totalItems = 0;
                    strTotalAmount = 0;
                    orderId = orderId + 1;

                    showSchemeList.clear();
                    //schemeListView.removeAllViewsInLayout();
                    schemeListView.setVisibility(View.GONE);

                    edtGuestName.setVisibility(View.VISIBLE);
                    edtGuestContactNo.setVisibility(View.VISIBLE);
                    edtGuestAddress.setVisibility(View.VISIBLE);
                    paymentModeRadioGroup.setVisibility(View.VISIBLE);
                    btnConfirm.setVisibility(View.VISIBLE);
                    btnTempBill.setEnabled(false);
                    btnTempBill.setBackgroundColor(getResources()
                            .getColor(R.color.horizontal_line));
                    btnTempBill.setTextColor(getResources().getColor(R.color.dark_brown));

                    if(finalOrderItemList.size() > 0)
                    {
                        for (int i = 0; i < tempItemList.size(); i++) {
                            mainView = finalItemRecyclerListView.getChildAt(i);
                            TextView itemQuantity = mainView.findViewById(R.id.edtEditQuantity);
                            TextView itemPrice = mainView.findViewById(R.id.txtItemPrice);
                            TextView itemName = mainView.findViewById(R.id.txtItemName);
                            TextView itemCategoryId = mainView.findViewById(R.id.txtItemCategoryId);

                            strItemQuantity = itemQuantity.getText().toString();
                            strItemPrice = itemPrice.getText().toString();
                            strItemName = itemName.getText().toString();
                            strItemCategoryId = itemCategoryId.getText().toString();

                            System.out.println("on button click quantity " + strItemQuantity);
                            System.out.println("on button click price " + strItemPrice);
                            System.out.println("on button click name " + strItemName);
                            System.out.println("on button click cat id " + strItemCategoryId);

                            totalItems = totalItems +
                                    Integer.parseInt(itemQuantity.getText().toString());

                            strTotalAmount = strTotalAmount +
                                    Double.parseDouble(itemPrice.getText().toString());

                            finalItemOrder = new AddItemsForOrder(
                                    tempItemList.get(i).getItemId(),
                                    strItemName,
                                    strItemQuantity,
                                    strItemPrice,
                                    tempItemList.get(i).getCatId()
                            );
                            finalItemList.add(finalItemOrder);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < tempItemList.size(); i++) {
                            mainView = orderedItemRecyclerList.getChildAt(i);
                            TextView itemQuantity = mainView.findViewById(R.id.edtEditQuantity);
                            TextView itemPrice = mainView.findViewById(R.id.txtItemPrice);
                            TextView itemName = mainView.findViewById(R.id.txtItemName);
                            TextView itemCategoryId = mainView.findViewById(R.id.txtItemCategoryId);

                            strItemQuantity = itemQuantity.getText().toString();
                            strItemPrice = itemPrice.getText().toString();
                            strItemName = itemName.getText().toString();
                            strItemCategoryId = itemCategoryId.getText().toString();

                            System.out.println("on button click quantity " + strItemQuantity);
                            System.out.println("on button click price " + strItemPrice);
                            System.out.println("on button click name " + strItemName);
                            System.out.println("on button click cat id " + strItemCategoryId);

                            totalItems = totalItems +
                                    Integer.parseInt(itemQuantity.getText().toString());

                            strTotalAmount = strTotalAmount +
                                    Double.parseDouble(itemPrice.getText().toString());

                            finalItemOrder = new AddItemsForOrder(
                                    tempItemList.get(i).getItemId(),
                                    strItemName,
                                    strItemQuantity,
                                    strItemPrice,
                                    tempItemList.get(i).getCatId()
                            );
                            finalItemList.add(finalItemOrder);
                        }
                    }

                    System.out.println("finalItemList size on temp btn " + finalItemList.size());

                    //calculate for scheme applied on item
                    if(isDiscount) {
                        if (schemeDiscountList.size() > 0) {
                            System.out.println("list with discount " + schemeDiscountList.size());

                            for (int i = 0; i < schemeDiscountList.size(); i++) {
                                discountAddition = discountAddition +
                                        Double.parseDouble(schemeDiscountList.get(i).getItemPrice());

                                totalAmount = totalAmount + Double.parseDouble(finalItemList.get(i).getItemPrice());

                                System.out.println("totalAmount " + totalAmount);

                                finalItemOrderWithDiscount = new AddItemsForOrder(
                                        schemeDiscountList.get(i).getItemId(),
                                        schemeDiscountList.get(i).getItemName(),
                                        schemeDiscountList.get(i).getItemQuantity(),
                                        schemeDiscountList.get(i).getItemPrice(),
                                        schemeDiscountList.get(i).getCatId()
                                );
                                finalItemOrderWithDiscountList.add(finalItemOrderWithDiscount);
                            }
                        }
                    }
                    else //calculate for no scheme applied on item
                    {
                        //schemeDiscountList.clear();
                        for(int i=0; i<finalItemList.size(); i++)
                        {
                            System.out.println("strSelectedSchemeId " + strSelectedSchemeId);
                            System.out.println("schemeAmount for loop " + schemeAmount);
                            System.out.println("schemeCategoryId for loop " + schemeCategoryId);

                            System.out.println("finalItemList id for loop "
                                    + finalItemList.get(i).getCatId());

                            nonDiscountedAmount = nonDiscountedAmount
                                    + Double.parseDouble(finalItemList.get(i).getItemPrice());

                            totalAmount = totalAmount + Double.parseDouble(finalItemList.get(i).getItemPrice());

                            System.out.println("totalAmount " + totalAmount);
                            //nonDiscountAddition = nonDiscountAddition + nonDiscountedAmount;
                            System.out.println("nonDiscountedAmount " + nonDiscountedAmount);
                            System.out.println("nonDiscountAddition " + nonDiscountAddition);

                            finalItemOrderWithDiscount = new AddItemsForOrder(
                                    finalItemList.get(i).getItemId(),
                                    finalItemList.get(i).getItemName(),
                                    finalItemList.get(i).getItemQuantity(),
                                    finalItemList.get(i).getItemPrice(),
                                    finalItemList.get(i).getCatId()
                            );
                            finalItemOrderWithDiscountList.add(finalItemOrderWithDiscount);
                        }
                    }

                    strTotalAmount = discountAddition + nonDiscountedAmount;

                    System.out.println("strTotalAmount for loop " + strTotalAmount);

                    txtTotalQuantity.setText(String.valueOf(totalItems));
                    txtTotalAmount.setText(String.valueOf(strTotalAmount));

                    btnConfirm.setEnabled(true);

                }
            });

            if (paymentModeRadioGroup!= null)
            {
                paymentModeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        // checkedId is the RadioButton selected
                        switch(checkedId) {
                            case(R.id.paymentModeCard):
                                strPaymentMode = paymentModeCard.getText().toString();
                                break;
                            case(R.id.paymentModeCash):
                                strPaymentMode = paymentModeCash.getText().toString();
                                break;
                            case(R.id.paymentModeOnline):
                                strPaymentMode = paymentModeOnline.getText().toString();
                                break;
                        }
                    }
                });
            }

            /*btnTempBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    System.out.println("big else go to web service");
                    //schemeListView.setEnabled(false);
                    txtGetSchemesTitle.setEnabled(false);

                    txtGetSchemesTitle.setBackgroundColor(
                            getResources().getColor(R.color.horizontal_line));
                    txtGetSchemesTitle.setTextColor(
                            getResources().getColor(R.color.dark_brown));

                    txtContinueOrderingTitle.setEnabled(false);
                    txtContinueOrderingTitle.setBackgroundColor(
                            getResources().getColor(R.color.horizontal_line));
                    txtContinueOrderingTitle.setTextColor(
                            getResources().getColor(R.color.dark_brown));

                    totalItems = 0;
                    strTotalAmount = 0;
                    orderId = orderId + 1;

                    showSchemeList.clear();
                    //schemeListView.removeAllViewsInLayout();
                    schemeListView.setVisibility(View.GONE);

                    edtGuestName.setVisibility(View.VISIBLE);
                    edtGuestContactNo.setVisibility(View.VISIBLE);
                    edtGuestAddress.setVisibility(View.VISIBLE);
                    paymentModeRadioGroup.setVisibility(View.VISIBLE);
                    btnConfirm.setVisibility(View.VISIBLE);
                    btnTempBill.setEnabled(false);
                    btnTempBill.setBackgroundColor(getResources()
                            .getColor(R.color.horizontal_line));
                    btnTempBill.setTextColor(getResources().getColor(R.color.dark_brown));

                    if(finalOrderItemList.size() > 0)
                    {
                        for (int i = 0; i < tempItemList.size(); i++) {
                            view = finalItemRecyclerListView.getChildAt(i);
                            TextView itemQuantity = view.findViewById(R.id.edtEditQuantity);
                            TextView itemPrice = view.findViewById(R.id.txtItemPrice);
                            TextView itemName = view.findViewById(R.id.txtItemName);
                            TextView itemCategoryId = view.findViewById(R.id.txtItemCategoryId);

                            strItemQuantity = itemQuantity.getText().toString();
                            strItemPrice = itemPrice.getText().toString();
                            strItemName = itemName.getText().toString();
                            strItemCategoryId = itemCategoryId.getText().toString();

                            System.out.println("on button click quantity " + strItemQuantity);
                            System.out.println("on button click price " + strItemPrice);
                            System.out.println("on button click name " + strItemName);
                            System.out.println("on button click cat id " + strItemCategoryId);

                            totalItems = totalItems +
                                    Integer.parseInt(itemQuantity.getText().toString());

                            strTotalAmount = strTotalAmount +
                                    Double.parseDouble(itemPrice.getText().toString());

                            finalItemOrder = new AddItemsForOrder(
                                    tempItemList.get(i).getItemId(),
                                    strItemName,
                                    strItemQuantity,
                                    strItemPrice,
                                    tempItemList.get(i).getCatId()
                            );
                            finalItemList.add(finalItemOrder);
                        }
                    }
                    else
                    {
                        for (int i = 0; i < tempItemList.size(); i++) {
                            view = orderedItemRecyclerList.getChildAt(i);
                            TextView itemQuantity = view.findViewById(R.id.edtEditQuantity);
                            TextView itemPrice = view.findViewById(R.id.txtItemPrice);
                            TextView itemName = view.findViewById(R.id.txtItemName);
                            TextView itemCategoryId = view.findViewById(R.id.txtItemCategoryId);

                            strItemQuantity = itemQuantity.getText().toString();
                            strItemPrice = itemPrice.getText().toString();
                            strItemName = itemName.getText().toString();
                            strItemCategoryId = itemCategoryId.getText().toString();

                            System.out.println("on button click quantity " + strItemQuantity);
                            System.out.println("on button click price " + strItemPrice);
                            System.out.println("on button click name " + strItemName);
                            System.out.println("on button click cat id " + strItemCategoryId);

                            totalItems = totalItems +
                                    Integer.parseInt(itemQuantity.getText().toString());

                            strTotalAmount = strTotalAmount +
                                    Double.parseDouble(itemPrice.getText().toString());

                            finalItemOrder = new AddItemsForOrder(
                                    tempItemList.get(i).getItemId(),
                                    strItemName,
                                    strItemQuantity,
                                    strItemPrice,
                                    tempItemList.get(i).getCatId()
                            );
                            finalItemList.add(finalItemOrder);
                        }
                    }

                    System.out.println("finalItemList size " + finalItemList.size());
                    System.out.println("strSelectedSchemeId " + strSelectedSchemeId);
                    //System.out.println("strSelectedSchemeId size " + strSelectedSchemeId.length());

                    boolean sameCategory = false;
                    //calculation on final item list
                    for(int i=0; i<finalItemList.size(); i++)
                    {
                        System.out.println("finalItemList id " + finalItemList.get(i).getCatId());
                        totalAmount = totalAmount + Double.parseDouble(finalItemList.get(i).getItemPrice());
                        //check scheme is applied or not
                        if(strSelectedSchemeId != null)
                        {
                            mainSchemeId = strSelectedSchemeId.replace("0/", "").split("/");
                            System.out.println("mainSchemeId length " + mainSchemeId.length);

                            //scheme for loop
                            for(int j=0; j<mainSchemeId.length; j++)
                            {
                                System.out.println("i " + i + " j " + j);
                                System.out.println("mainSchemeId data " + mainSchemeId[j]);
                                schemeId = mainSchemeId[j].split("_");
                                schemeAmount = schemeId[1];
                                schemeCategoryId = schemeId[2];

                                System.out.println("item category " + finalItemList.get(i).getCatId());
                                System.out.println("scheme category " + schemeCategoryId);

                                //item category id and scheme category id are same
                                if(finalItemList.get(i).getCatId().equals(schemeCategoryId))
                                {
                                    sameCategory = true;

                                    discountOnItemPrice =
                                            (Double.parseDouble(finalItemList.get(i).getItemPrice())
                                                    *
                                                    (Double.parseDouble(schemeAmount) / 100));

                                    System.out.println("discountOnItemPrice " + discountOnItemPrice);

                                    discountedAmount = Double.parseDouble(finalItemList.get(i).getItemPrice()) -
                                            discountOnItemPrice;

                                    discountAddition = discountAddition + discountedAmount;

                                    System.out.println("discountedAmount " + discountedAmount);
                                    System.out.println("discountAddition " + discountAddition);

                                    finalItemOrderWithDiscount = new AddItemsForOrder(
                                            finalItemList.get(i).getItemId(),
                                            finalItemList.get(i).getItemName(),
                                            finalItemList.get(i).getItemQuantity(),
                                            String.valueOf(discountedAmount),
                                            finalItemList.get(i).getCatId()
                                    );
                                    finalItemOrderWithDiscountList.add(finalItemOrderWithDiscount);
                                }
                                //item category id and scheme category id are not same
                                *//*else if (! finalItemList.get(i).getCatId().equals(schemeCategoryId))
                                {
                                    noSchemeCatIdAmount = 0.0;
                                }*//*
                                else //for non discount
                                {
                                    nonDiscountedAmount = nonDiscountedAmount
                                            + Double.parseDouble(finalItemList.get(i).getItemPrice());

                                    //nonDiscountAddition = nonDiscountAddition + nonDiscountedAmount;
                                    System.out.println("nonDiscountedAmount " + nonDiscountedAmount);
                                    System.out.println("nonDiscountAddition " + nonDiscountAddition);

                                    finalItemOrderWithDiscount = new AddItemsForOrder(
                                            finalItemList.get(i).getItemId(),
                                            finalItemList.get(i).getItemName(),
                                            finalItemList.get(i).getItemQuantity(),
                                            finalItemList.get(i).getItemPrice(),
                                            finalItemList.get(i).getCatId()
                                    );
                                    finalItemOrderWithDiscountList.add(finalItemOrderWithDiscount);
                                }
                            }
                        }
                        else // scheme is not applied ie. no discount
                        {
                            nonDiscountedAmount = nonDiscountedAmount
                                    + Double.parseDouble(finalItemList.get(i).getItemPrice());

                            //nonDiscountAddition = nonDiscountAddition + nonDiscountedAmount;
                            System.out.println("nonDiscountedAmount " + nonDiscountedAmount);
                            System.out.println("nonDiscountAddition " + nonDiscountAddition);

                            finalItemOrderWithDiscount = new AddItemsForOrder(
                                    finalItemList.get(i).getItemId(),
                                    finalItemList.get(i).getItemName(),
                                    finalItemList.get(i).getItemQuantity(),
                                    finalItemList.get(i).getItemPrice(),
                                    finalItemList.get(i).getCatId()
                            );
                            finalItemOrderWithDiscountList.add(finalItemOrderWithDiscount);

                        }
                    }

                    strTotalAmount = discountAddition + nonDiscountedAmount;

                    System.out.println("strTotalAmount for loop " + strTotalAmount);

                    txtTotalQuantity.setText(String.valueOf(totalItems));
                    txtTotalAmount.setText(String.valueOf(strTotalAmount));

                    btnConfirm.setEnabled(true);
                }
            });*/

            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    alertDialogBuilder = new AlertDialog.Builder(getActivity());

                    View promptsView = getActivity().getLayoutInflater().inflate(R.layout.custom_confirmation_dialog, null);

                    forgotPasswordProgressBar = promptsView.findViewById(R.id.forgotPasswordProgressBar);
                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    // set dialog message
                    alertDialogBuilder
                            //.setMessage(getResources().getString(R.string.forgot_password_title))
                            .setCancelable(true)
                            .setPositiveButton(getResources().getString(R.string.confirmation_dialog_yes), null)
                            .setNegativeButton(getResources().getString(R.string.confirmation_dialog_no), null)
                            .create();

                    // create alert dialog
                    alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                    Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                    Button theNegativeButton = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);

                    theButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            alertDialog.dismiss();

                            if(edtGuestContactNo.getText().toString().length() == 0)
                            {
                                edtGuestContactNo.setError("Enter Mobile Number");
                                edtGuestContactNo.requestFocus();
                            }
                            else if(edtGuestContactNo.getText().toString().length()<10)
                            {
                                edtGuestContactNo.setError("Enter Valid Mobile Number");
                                edtGuestContactNo.requestFocus();
                            }
                            else if(paymentModeRadioGroup.getCheckedRadioButtonId() == -1)
                            {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Select Payment mode", Toast.LENGTH_SHORT).show();
                                paymentModeRadioGroup.requestFocus();
                            }
                            else
                            {
                                System.out.println("strPaymentMode " + strPaymentMode);
                                generateJSON();
                                //insertOrderIntoDB();

                                if(isNetworkAvailable()) {
                                    ConfirmOrderAsync confirmOrderAsync = new ConfirmOrderAsync();
                                    confirmOrderAsync.execute();
                                }
                                else
                                {
                                    Toast.makeText(getActivity().getApplicationContext(),
                                            "Please check internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

                    theNegativeButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            alertDialog.dismiss();
                        }
                    });
                }
            });
        }
        catch (Exception e)
        {
            e.getMessage();
        }
        return mainView;
    }

    @Override
    public void onClick(String str) {

        System.out.println("str = " + str);
        schemeId = str.split("_");
        if(schemeId[0].equals(""))
        {
            selectedSchemeId = "0";
        }
        else {
            //selectedSchemeId = selectedSchemeId +","+ schemeId[0];
            selectedSchemeId = schemeId[0];
        }
        schemeAmount = schemeId[1];
        schemeCategoryId = schemeId[2];

        System.out.println("scheme amount " + schemeAmount);
        System.out.println("scheme category Id " + schemeCategoryId);

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm =(ConnectivityManager)getActivity().getSystemService
                (Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        System.out.println("connection " +isConnected);
        return activeNetwork != null && activeNetwork.isConnected();
    }

    public void getSchemes(List<String> catIdList, String selectedDate)
    {
        try {

            for(int j=0; j <schemeDetailsList.size(); j++) {

                strCatId = schemeDetailsList.get(j).getCategoryId();

                //System.out.println("strCatId " + strCatId);

                for(int i=0 ; i<catIdList.size();i++)
                {
                    //System.out.println("catIdList.get(i) " + catIdList.get(i));
                    if(catIdList.get(i).equals(strCatId))
                    {
                        //System.out.println("add cat id " + catIdList.get(i));
                        System.out.println("add scheme id " + schemeDetailsList.get(j).getSchemeId());
                        tempSchemeIdList.add(schemeDetailsList.get(j).getSchemeId());
                    }
                }
            }
            //System.out.println("temp Scheme Id list size " + tempSchemeIdList.size());

            schemeIdList = removeDuplicateSchemeId(tempSchemeIdList);
            //System.out.println("schemeIdList size " + schemeIdList.size());

            for(int i=0; i<schemeIdList.size();i++)
            {
                System.out.println("schemeIdList id " + schemeIdList.get(i));
                System.out.println("schemeDetailsList size " + schemeDetailsList.size());

                for(int j=0; j<schemeDetailsList.size(); j++)
                {
                    if(schemeIdList.get(i).equals(schemeDetailsList.get(j).getSchemeId()))
                    {
                        String strEndDate = schemeDetailsList.get(j).getToDate();
                        String strFromDate = schemeDetailsList.get(j).getFromDate();

                        System.out.println("strEndDate " + strEndDate);
                        System.out.println("strFromDate " + strFromDate);
                        System.out.println("selected date " + selectedDate);

                        try {

                            SimpleDateFormat dateFormat1 = new SimpleDateFormat(
                                    "dd/MM/yyyy", Locale.US);
                            //String currentDate = dateFormat1.format(new Date());
                            Date endDate = dateFormat1.parse(strEndDate);
                            Date fromDate = dateFormat1.parse(strFromDate);
                            Date toDate = dateFormat1.parse(selectedDate);

                            String currentDate = selectedDate;
                            System.out.println(dateFormat1.format(endDate));

                            if(fromDate.compareTo(toDate) * toDate.compareTo(endDate) >= 0)
                            {
                                System.out.println("current date " + toDate+" is between "
                                        +fromDate + " and " + endDate);

                                schemeDetails = new SchemeDetails(
                                    schemeDetailsList.get(j).getCompanyId(),
                                    schemeDetailsList.get(j).getSchemeId(),
                                    schemeDetailsList.get(j).getCategoryId(),
                                    schemeDetailsList.get(j).getCategory(),
                                    schemeDetailsList.get(j).getOutletId(),
                                    schemeDetailsList.get(j).getOutletName(),
                                    schemeDetailsList.get(j).getDiscount(),
                                    schemeDetailsList.get(j).getFromDate(),
                                    schemeDetailsList.get(j).getToDate(),
                                    "1");
                                showSchemeList.add(schemeDetails);
                            }

                            /*if (dateFormat1.parse(currentDate).before
                                    (dateFormat1.parse(strEndDate))) {

                                System.out.println("currentDate before " + currentDate);
                                System.out.println("strEndDate before " + strEndDate);
                            }

                            if (dateFormat1.parse(currentDate).after
                                    (dateFormat1.parse(strEndDate))) {

                                System.out.println("currentDate in if " + currentDate);
                                System.out.println("strEndDate in if " + strEndDate);
                            }
                            else
                            {
                                schemeDetails = new SchemeDetails(
                                        schemeDetailsList.get(j).getCompanyId(),
                                        schemeDetailsList.get(j).getSchemeId(),
                                        schemeDetailsList.get(j).getCategoryId(),
                                        schemeDetailsList.get(j).getCategory(),
                                        schemeDetailsList.get(j).getOutletId(),
                                        schemeDetailsList.get(j).getOutletName(),
                                        schemeDetailsList.get(j).getDiscount(),
                                        schemeDetailsList.get(j).getFromDate(),
                                        schemeDetailsList.get(j).getToDate(),
                                        "1"
                                );
                                showSchemeList.add(schemeDetails);
                            }*/
                        }
                        catch (Exception e)
                        {
                            e.getMessage();
                        }
                    }
                }
            }

            System.out.println("showSchemeList size " + showSchemeList.size());

            /*Collections.sort(showSchemeList, new Comparator<SchemeDetails>() {

                @Override
                public int compare(SchemeDetails o1, SchemeDetails o2) {
                    try {
                        System.out.println("from time " + new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(o1.getFromDate()));
                        System.out.println("to time " + new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(o1.getToDate()));

                        return new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(o2.getToDate())
                                .compareTo(new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(o1.getToDate()));
                    } catch (ParseException e) {
                        return 0;
                    }
                }
            });*/
            System.out.println("showSchemeList size " + showSchemeList.size());

            for(int i=0; i<showSchemeList.size(); i++)
            {
                System.out.println("showSchemeList id " + showSchemeList.get(i).getSchemeId());
            }

            confirmOrderSchemeDetailsAdapter = new ConfirmOrderSchemeRecyclerAdapter(
                    getActivity().getApplicationContext(), showSchemeList);
            confirmOrderSchemeDetailsAdapter.notifyDataSetChanged();
            schemeListView.setAdapter(confirmOrderSchemeDetailsAdapter);

            confirmOrderSchemeDetailsAdapter.setOnItemClickListner
                    (new ConfirmOrderSchemeRecyclerAdapter.onItemClickListner() {
                        @Override
                        public void onClick(String str) {
                            System.out.println("scheme str = " + str);
                            //schemeId = str.split("_");
                            strSelectedSchemeId = str;
                            /*schemeId = str.split("/");
                            if(schemeId[0].equals(""))
                            {
                                selectedSchemeId = "0";
                            }
                            else {
                                //selectedSchemeId = selectedSchemeId +","+ schemeId[0];
                                selectedSchemeId = schemeId[0];
                            }
                            schemeAmount = schemeId[1];
                            schemeCategoryId = schemeId[2];

                            System.out.println("scheme amount " + schemeAmount);
                            System.out.println("scheme category Id " + schemeCategoryId);*/
                            // confirmOrderSchemeDetailsAdapter.setClickable(false);

                            mainSchemeId = strSelectedSchemeId.split("_");
                            System.out.println("mainSchemeId length " + mainSchemeId.length);
                            boolean found;

                            //schemeId = mainSchemeId[0].split("_");
                            schemeAmount = mainSchemeId[1];
                            schemeCategoryId = mainSchemeId[2];
                            selectedSchemeId = selectedSchemeId +","+ mainSchemeId[0];
                            //System.out.println("item category " + finalItemList.get(i).getCatId());
                            System.out.println("scheme category " + schemeCategoryId);
                            System.out.println("schemeDiscountList size " + schemeDiscountList.size());

                            for (int i = 0; i < schemeDiscountList.size(); i++) {
                                mainView = orderedItemRecyclerList.getChildAt(i);
                                TextView itemQuantity = mainView.findViewById(R.id.edtEditQuantity);
                                TextView itemPrice = mainView.findViewById(R.id.txtItemPrice);
                                TextView itemName = mainView.findViewById(R.id.txtItemName);
                                TextView itemCategoryId = mainView.findViewById(R.id.txtItemCategoryId);

                                strItemQuantity = itemQuantity.getText().toString();
                                strItemPrice = itemPrice.getText().toString();
                                strItemName = itemName.getText().toString();
                                strItemCategoryId = itemCategoryId.getText().toString();

                                System.out.println("on button click quantity " + strItemQuantity);
                                System.out.println("on button click price " + strItemPrice);
                                System.out.println("on button click name " + strItemName);
                                System.out.println("on button click cat id " + strItemCategoryId);

                                totalItems = totalItems +
                                        Integer.parseInt(itemQuantity.getText().toString());

                                strTotalAmount = strTotalAmount +
                                        Double.parseDouble(itemPrice.getText().toString());

                                //item category id and scheme category id are same
                                if(strItemCategoryId.equals(schemeCategoryId))
                                {
                                    System.out.println("calculate discount for "
                                            + strItemName);

                                    discountOnItemPrice =
                                            (Double.parseDouble(strItemPrice)
                                                    *
                                                    (Double.parseDouble(schemeAmount) / 100));

                                    System.out.println("discountOnItemPrice " + discountOnItemPrice);

                                    discountedAmount = Double.parseDouble(strItemPrice) -
                                            discountOnItemPrice;
                                    System.out.println("discountedAmount " + discountedAmount);

                                    for(AddItemsForOrder s : schemeDiscountList)
                                    {
                                        if(s.getCatId().equals(schemeCategoryId))
                                        {
                                            schemeDiscountList.get(i).setItemPrice(String.valueOf(discountedAmount));
                                            //s.setItemPrice(String.valueOf(discountedAmount));
                                        }
                                    }
                                    //schemeDiscount.setItemPrice(String.valueOf(discountedAmount));

                                    /*schemeDiscount = new AddItemsForOrder(
                                            tempItemList.get(i).getItemId(),
                                            strItemName,
                                            strItemQuantity,
                                            String.valueOf(discountedAmount),
                                            tempItemList.get(i).getCatId()
                                    );*/
                                    //schemeDiscountList.add(schemeDiscount);
                                }
                                /*else
                                {
                                    System.out.println("calculate no discount for "
                                            + strItemName);

                                    nonDiscountedAmount = nonDiscountedAmount
                                            + Double.parseDouble(strItemPrice);

                                    //nonDiscountAddition = nonDiscountAddition + nonDiscountedAmount;
                                    System.out.println("nonDiscountedAmount " + nonDiscountedAmount);
                                    System.out.println("nonDiscountAddition " + nonDiscountAddition);

                                    schemeNonDiscount = new AddItemsForOrder(
                                            tempItemList.get(i).getItemId(),
                                            strItemName,
                                            strItemQuantity,
                                            String.valueOf(strItemPrice),
                                            tempItemList.get(i).getCatId()
                                    );

                                    schemeNonDiscountList.add(schemeNonDiscount);
                                }*/
                                //finalItemOrderWithDiscountList.add(finalItemOrderWithDiscount);

                            }
                            isDiscount = true;
                        }
                    });

        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    private static List<Date> getDates(String dateString1, String dateString2)
    {
        ArrayList<Date> dates = new ArrayList<Date>();
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy", Locale.US);

        Date date1 = null;
        Date date2 = null;

        try {
            date1 = df1 .parse(dateString1);
            date2 = df1 .parse(dateString2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);


        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);

        while(!cal1.after(cal2))
        {
            dates.add(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
        }
        return dates;
    }

    // Function to remove duplicates from an ArrayList
    public static List<String> removeDuplicateSchemeId(List<String> list)
    {
        // Create a new ArrayList
        List<String> newList = new ArrayList<String>();

        // Traverse through the first list
        for (String element : list) {

            // If this element is not present in newList
            // then add it
            if (!newList.contains(element)) {
                newList.add(element);
            }
        }

        // return the new list
        return newList;
    }

    @TargetApi(24)
    int itemCount=0;
    String outpassJSONString;
    double itemPrice;
    public void generateJSON()
    {
        try {

            System.out.println("finalItemOrderWithDiscountList " + finalItemOrderWithDiscountList.size());
            for (int i=0; i<finalItemOrderWithDiscountList.size(); i++)
            {
                try {
                    reqObj = new JSONObject();
                    /*itemPrice = Integer.parseInt(finalItemList.get(i).getItemQuantity())
                            * Double.parseDouble(finalItemList.get(i).getItemPrice());*/

                    reqObj.put("itemId", finalItemOrderWithDiscountList.get(i).getItemId());
                    reqObj.put("itemName", finalItemOrderWithDiscountList.get(i).getItemName());
                    reqObj.put("itemQuantity", finalItemOrderWithDiscountList.get(i).getItemQuantity());
                    reqObj.put("itemPrice", finalItemOrderWithDiscountList.get(i).getItemPrice());
                    req.put(reqObj);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
            outpassJSONString = req.toString();
            System.out.println("created json = " + outpassJSONString);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    public ArrayList<AddItemsForOrder>  removeDuplicates(List<AddItemsForOrder> list){
        Set<AddItemsForOrder> set = new TreeSet(new Comparator<AddItemsForOrder>() {

            @Override
            public int compare(AddItemsForOrder o1, AddItemsForOrder o2) {
                if(o1.getItemId().equalsIgnoreCase(o2.getItemId())){
                    return 0;
                }
                return 1;
            }
        });
        set.addAll(list);

        final ArrayList newList = new ArrayList(set);
        return newList;
    }

    class DownloadSchemeAsync extends AsyncTask<String, Void, String>
    {
        String selectedDate;

        public DownloadSchemeAsync(String selectedDate)
        {
            this.selectedDate = selectedDate;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            schemeProgressBar.setVisibility(View.VISIBLE);
            schemeProgressBar.setProgress(5);
        }

        @Override
        protected String doInBackground(String... strings) {
            downloadScheme();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {

                if(serverConnection)
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Server or Internet is down. Please try after some time!", Toast.LENGTH_SHORT).show();
                }
                else {
                    serverConnection = false;

                    schemeProgressBar.setVisibility(View.GONE);

                    String strUnitResponse = responseScheme.getProperty("AppliedScheme_DetailsResult").toString();

                    JSONArray jarray = new JSONArray(strUnitResponse);

                    for (int i=0; i<jarray.length(); i++)
                    {
                        schemeDetails = new SchemeDetails(
                                jarray.getJSONObject(i).getString("CompanyId"),
                                jarray.getJSONObject(i).getString("SchemeId"),
                                jarray.getJSONObject(i).getString("CategoryId"),
                                jarray.getJSONObject(i).getString("Category"),
                                jarray.getJSONObject(i).getString("OutletId"),
                                jarray.getJSONObject(i).getString("OutletName"),
                                jarray.getJSONObject(i).getString("Discount"),
                                jarray.getJSONObject(i).getString("FromDate"),
                                jarray.getJSONObject(i).getString("ToDate"),
                                jarray.getJSONObject(i).getString("msg"));
                        schemeDetailsList.add(schemeDetails);
                    }

                    System.out.println("Download Activity scheme list size = "
                            + schemeDetailsList.size());

                    getSchemes(catIdList, selectedDate);
                }
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

    public void downloadScheme()
    {
        String METHOD_NAME = "AppliedScheme_Details";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"AppliedScheme_Details";

        System.out.println("Soap action = " + SOAP_ACTION);
        System.out.println("url = " + UrlStrings.URL);

        SoapObject request = new SoapObject(UrlStrings.NAMESPACE, METHOD_NAME);
        // Property which holds input parameters
        PropertyInfo companyIdPI = new PropertyInfo();
        PropertyInfo outletIdPI = new PropertyInfo();

        companyIdPI.setName("CompanyId");
        outletIdPI.setName("OutletId");

        companyIdPI.setValue(companyId);
        outletIdPI.setValue(outletId);

        companyIdPI.setType(String.class);
        outletIdPI.setType(String.class);

        request.addProperty(companyIdPI);
        request.addProperty(outletIdPI);

        System.out.println("request - " + companyIdPI + " = " + outletIdPI + " = ");

        System.out.println("requested parameters : " +request.toString());
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(UrlStrings.URL, 50000);

        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION, envelope);
            // Get the response

            if (envelope.bodyIn instanceof SoapFault)
            {
                String str= ((SoapFault) envelope.bodyIn).faultstring;
                Log.i("Soapfault", str);
                // response = response.getProperty("LoginDetailsResult").toString();
                //Log.d("response property" , response.getProperty("LoginDetailsResult").toString());
            }
            else {
                //SoapObject result = (SoapObject)envelope.getResponse();
                //SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
                serverConnection = false;
                responseScheme = (SoapObject) envelope.bodyIn;
                //Log.d("response property" , response.getProperty("LoginResult").toString());
                Log.d("response property" , responseScheme.getProperty("AppliedScheme_DetailsResult").toString());
                Log.d("WS", responseScheme.toString());
            }
        }
        catch (SoapFault e)
        {
            System.out.println("in catch = " + e.getMessage());
        }
        catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            System.out.println("connection error checking");
            serverConnection = true;
            e.printStackTrace();
        }
    }

    class ConfirmOrderAsync extends AsyncTask<String, Void, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            forgotPasswordProgressBar.setVisibility(View.VISIBLE);
            forgotPasswordProgressBar.setProgress(5);
        }

        @Override
        protected String doInBackground(String... strings) {

            sendOrder();
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                forgotPasswordProgressBar.setVisibility(View.GONE);
                String strResponse = response.getProperty("Order_DetailsResult").toString();

                JSONArray jarray = new JSONArray(strResponse);

                if(jarray.getJSONObject(0).getString("msg").equals("1"))
                {
                    edtGuestName.getText().clear();
                    edtGuestContactNo.getText().clear();
                    edtGuestAddress.getText().clear();
                    txtTotalAmount.setText("");
                    txtTotalQuantity.setText("");
                    addItemsForOrderList.clear();
                    schemeIdList.clear();
                    showSchemeList.clear();
                    schemeDiscountList.clear();
                    //confirmOrderSchemeDetailsAdapter.notifyDataSetChanged();
                    tempItemList.clear();
                    //confirmOrderRecyclerAdapter.notifyDataSetChanged();
                    finalOrderItemList.clear();
                    //paymentModeRadioGroup.clearCheck();

                    Toast.makeText(getActivity().getApplicationContext(),
                            "Order Saved Successfully", Toast.LENGTH_LONG).show();

                    //Fragment fragment = new HomeFragment();
                    Fragment fragment = new OrderDoneFragment();
                    //fragment.setArguments(bundle);
                    getFragmentManager().beginTransaction()
                            .addToBackStack("generateOrder")
                            .commit();
                    if (fragment != null) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                        fragmentTransaction.addToBackStack("generateOrder");
                        fragmentTransaction.commit();

                        // set the toolbar title
                        // getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                        //toolbar_title.setText(getResources().getString(R.string.app_name));
                    }
                }
                else
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Unable To Saved Order Details.", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                e.getMessage();
            }
        }
    }

    public void sendOrder()
    {
        String METHOD_NAME = "Order_Details";
        String SOAP_ACTION = UrlStrings.NAMESPACE+"Order_Details";

        System.out.println("Soap action = " + SOAP_ACTION);
        System.out.println("url = " + UrlStrings.URL);

        SoapObject request = new SoapObject(UrlStrings.NAMESPACE, METHOD_NAME);
        // Property which holds input parameters
        PropertyInfo outletIdPI = new PropertyInfo();
        PropertyInfo orderIdPI = new PropertyInfo();
        PropertyInfo orderDatePI = new PropertyInfo();
        PropertyInfo orderTimePI = new PropertyInfo();
        PropertyInfo guestNamePI = new PropertyInfo();
        PropertyInfo guestMobileNoPI = new PropertyInfo();
        PropertyInfo guestAddressPI = new PropertyInfo();
        PropertyInfo schemeIdPI = new PropertyInfo();
        PropertyInfo totalBillAmountPI = new PropertyInfo();
        PropertyInfo totalDiscountAmountPI = new PropertyInfo();
        PropertyInfo finalBillAmountPI = new PropertyInfo();
        PropertyInfo jsonPI = new PropertyInfo();
        PropertyInfo companyIdPI = new PropertyInfo();
        PropertyInfo paymentModePI = new PropertyInfo();

        outletIdPI.setName("OutletId");
        orderIdPI.setName("OrderId");
        orderDatePI.setName("OrderDate");
        orderTimePI.setName("OrderTime");
        guestNamePI.setName("GuestName");
        guestMobileNoPI.setName("GuestMobileNo");
        guestAddressPI.setName("GuestAddress");
        schemeIdPI.setName("SchemeId");
        totalBillAmountPI.setName("TotalBillAmount");
        totalDiscountAmountPI.setName("TotalDiscountAmount");
        finalBillAmountPI.setName("FinalBillAmount");
        jsonPI.setName("json");
        companyIdPI.setName("CompanyId");
        paymentModePI.setName("PaymentMode");

        outletIdPI.setValue(outletId);
        orderIdPI.setValue(String.valueOf(orderId));
        orderDatePI.setValue(txtDate.getText().toString());
        orderTimePI.setValue(txtTime.getText().toString());
        guestNamePI.setValue(edtGuestName.getText().toString());
        guestMobileNoPI.setValue(edtGuestContactNo.getText().toString());
        guestAddressPI.setValue(edtGuestAddress.getText().toString());
        schemeIdPI.setValue(selectedSchemeId.replace("0,", ""));
        totalBillAmountPI.setValue(String.valueOf(totalAmount));
        totalDiscountAmountPI.setValue(String.valueOf(discountAddition));
        finalBillAmountPI.setValue(String.valueOf(strTotalAmount));
        jsonPI.setValue(outpassJSONString);
        companyIdPI.setValue(companyId);
        paymentModePI.setValue(strPaymentMode);

        outletIdPI.setType(String.class);
        orderIdPI.setType(String.class);
        orderDatePI.setType(String.class);
        orderTimePI.setType(String.class);
        guestNamePI.setType(String.class);
        guestMobileNoPI.setType(String.class);
        guestAddressPI.setType(String.class);
        schemeIdPI.setType(String.class);
        totalBillAmountPI.setType(String.class);
        totalDiscountAmountPI.setType(String.class);
        finalBillAmountPI.setType(String.class);
        jsonPI.setType(String.class);
        companyIdPI.setType(String.class);
        paymentModePI.setType(String.class);

        request.addProperty(outletIdPI);
        request.addProperty(orderIdPI);
        request.addProperty(orderDatePI);
        request.addProperty(orderTimePI);
        request.addProperty(guestNamePI);
        request.addProperty(guestMobileNoPI);
        request.addProperty(guestAddressPI);
        request.addProperty(schemeIdPI);
        request.addProperty(totalBillAmountPI);
        request.addProperty(totalDiscountAmountPI);
        request.addProperty(finalBillAmountPI);
        request.addProperty(jsonPI);
        request.addProperty(companyIdPI);
        request.addProperty(paymentModePI);

        System.out.println("requested parameters : " +request.toString());
        // Create envelope
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope( SoapEnvelope.VER11);
        envelope.dotNet = true;
        // Set output SOAP object
        envelope.setOutputSoapObject(request);
        // Create HTTP call object
        HttpTransportSE androidHttpTransport = new HttpTransportSE(UrlStrings.URL, 50000);

        try {
            // Invoke web service
            androidHttpTransport.call(SOAP_ACTION, envelope);
            // Get the response

            if (envelope.bodyIn instanceof SoapFault)
            {
                String str= ((SoapFault) envelope.bodyIn).faultstring;
                Log.i("Soapfault", str);
                // response = response.getProperty("LoginDetailsResult").toString();
                //Log.d("response property" , response.getProperty("LoginDetailsResult").toString());
            }
            else {
                //SoapObject result = (SoapObject)envelope.getResponse();
                //SoapObject resultsRequestSOAP = (SoapObject) envelope.bodyIn;
                serverConnection = false;
                response = (SoapObject) envelope.bodyIn;
                //Log.d("response property" , response.getProperty("LoginResult").toString());
                Log.d("response property" , response.getProperty("Order_DetailsResult").toString());
                Log.d("WS", response.toString());
            }
        }
        catch (SoapFault e)
        {
            System.out.println("in catch = " + e.getMessage());
        }
        catch (Exception e) {
            //Assign Error Status true in static variable 'errored'
            System.out.println("connection error checking");
            serverConnection = true;
            e.printStackTrace();
        }
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
