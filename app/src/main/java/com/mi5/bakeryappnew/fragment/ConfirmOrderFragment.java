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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.activity.NavigationDrawerActivity;
import com.mi5.bakeryappnew.adapter.ConfirmOrderRecyclerAdapter;
import com.mi5.bakeryappnew.adapter.ConfirmOrderSchemeRecyclerAdapter;
import com.mi5.bakeryappnew.database.MyDBHandler;
import com.mi5.bakeryappnew.model.AddItemsForOrder;
import com.mi5.bakeryappnew.model.ItemDatabaseDetails;
import com.mi5.bakeryappnew.model.SchemeDetails;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ConfirmOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConfirmOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmOrderFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    Bundle bundle = new Bundle();
    AddItemsForOrder addItemsForOrder, tempItemOrder, tempFinalItemOrder, finalItemOrder;
    List<AddItemsForOrder> addItemsForOrderList;
    ArrayList<AddItemsForOrder> addItemsForOrdersParcelableList;
    List<AddItemsForOrder> tempItemList;
    List<AddItemsForOrder> finalItemList;
    List<AddItemsForOrder> tempFinalItemOrderList;
    ItemDatabaseDetails itemDatabaseDetails;
    List<ItemDatabaseDetails> itemDatabaseDetailsList;
    List<String> catIdList;

    JSONArray req = new JSONArray();
    JSONObject reqObj = null;

    MyDBHandler db;
    SharedPreferences sharedpreferences;
    String jsonSharedPreference, companyId, outletId;
    boolean hasLoggedIn, downloadData;
    String strGuestName, strGuestContactNo, strGuestAddress;

    TextView txtDate, txtTime, btnConfirm, txtTotalAmount, txtTotalQuantity, btnTempBill;
    EditText edtGuestName, edtGuestContactNo, edtGuestAddress;
    RecyclerView orderedItemRecyclerList;
    RecyclerView schemeListView;
    ProgressBar forgotPasswordProgressBar;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;
    RadioGroup paymentModeRadioGroup;
    RadioButton paymentModeCash, paymentModeCard, paymentModeOnline, radioButton;

    int totalItems =0 ;
    double strTotalAmount =0.0, discountOnItemPrice=0.0, totalAmount=0.0,
            discountedAmount, nonDiscountedAmount, discountAddition=0.0, nonDiscountAddition=0.0;

    String schemeAmount="0.0", schemeCategoryId, strPaymentMode;
    String[] schemeId;
    String selectedSchemeId="0", selectedSchemeDiscountAmount, finalDiscountedAmount;

    ConfirmOrderRecyclerAdapter confirmOrderRecyclerAdapter;

    String strSchemeId, strCatId;
    List<String> tempSchemeIdList = new ArrayList<String>();
    List<String> schemeIdList = new ArrayList<String>();
    SchemeDetails schemeDetails;
    List<SchemeDetails> showSchemeList = new ArrayList<SchemeDetails>();
    ConfirmOrderSchemeRecyclerAdapter confirmOrderSchemeDetailsAdapter;

    int orderId = 0;
    SoapObject response;

    boolean serverConnection=false;

    public ConfirmOrderFragment() {
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
    public static ConfirmOrderFragment newInstance(String param1, String param2) {
        ConfirmOrderFragment fragment = new ConfirmOrderFragment();
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

    View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_confirm_order, container, false);

        ((NavigationDrawerActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.nav_header_confirm_order));

        txtDate = view.findViewById(R.id.txtDate);
        txtTime = view.findViewById(R.id.txtTime);
        edtGuestName = view.findViewById(R.id.edtGuestName);
        edtGuestContactNo = view.findViewById(R.id.edtGuestContactNo);
        edtGuestAddress = view.findViewById(R.id.edtGuestAddress);
        orderedItemRecyclerList = view.findViewById(R.id.orderedItemRecyclerList);
        btnConfirm = view.findViewById(R.id.btnConfirm);
        btnTempBill = view.findViewById(R.id.btnTempBill);
        txtTotalQuantity = view.findViewById(R.id.txtTotalQuantity);
        txtTotalAmount = view.findViewById(R.id.txtTotalAmount);
        schemeListView = view.findViewById(R.id.schemeListView);
        paymentModeRadioGroup = view.findViewById(R.id.paymentModeRadioGroup);
        paymentModeCash = view.findViewById(R.id.paymentModeCash);
        paymentModeCard = view.findViewById(R.id.paymentModeCard);
        paymentModeOnline = view.findViewById(R.id.paymentModeOnline);

        bundle = this.getArguments();
        if (bundle != null) {
            addItemsForOrder = (AddItemsForOrder) bundle.getParcelable("addOrderItem");
            addItemsForOrdersParcelableList = bundle.getParcelableArrayList(
                    "addItemsForOrdersParcelableList");
        }
        System.out.println("addItemsForOrder " + addItemsForOrder);

        addItemsForOrderList = new ArrayList<AddItemsForOrder>();
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

        db = new MyDBHandler(getActivity().getApplicationContext());

        btnConfirm.setEnabled(false);

        try {
            Cursor res = db.getAllItemData();
            if(res.getCount() == 0) {
                // show message
                Toast.makeText(getActivity().getApplicationContext(),
                        "No Items Available", Toast.LENGTH_SHORT).show();
                //return;
            }

            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {

                itemDatabaseDetails = new ItemDatabaseDetails(
                        res.getString(0),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3),
                        res.getString(4),
                        res.getString(5),
                        res.getString(6),
                        res.getString(7),
                        res.getString(8)
                );
                itemDatabaseDetailsList.add(itemDatabaseDetails);
            }
            //System.out.println("Item List size = " + itemDatabaseDetailsList.size());
        }
        catch (Exception e)
        {
            e.getMessage();
        }

        try {
            tempItemList = removeDuplicates(addItemsForOrdersParcelableList);

            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);

            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat timeDf = new SimpleDateFormat("HH:ss a", Locale.getDefault());
            String formattedDate = df.format(c);
            txtDate.setText(formattedDate);

            Calendar cal = Calendar.getInstance();
            Date currentLocalTime = cal.getTime();
            DateFormat date = new SimpleDateFormat("hh:mm a", Locale.US);
            // you can get seconds by adding  "...:ss" to it
            //date.setTimeZone(TimeZone.getTimeZone("GMT+1:00"));

            String localTime = date.format(currentLocalTime);
            txtTime.setText(localTime);

            GridLayoutManager mGrid = new GridLayoutManager(getActivity().
                    getApplicationContext(), 1);
            orderedItemRecyclerList.setLayoutManager(mGrid);
            orderedItemRecyclerList.setHasFixedSize(true);

            GridLayoutManager mGrid1 = new GridLayoutManager(getActivity().
                    getApplicationContext(), 1);
            schemeListView.setLayoutManager(mGrid1);
            schemeListView.setHasFixedSize(true);

            confirmOrderRecyclerAdapter = new ConfirmOrderRecyclerAdapter(getActivity().
                    getApplicationContext(), tempItemList, txtTotalQuantity, txtTotalAmount);
            confirmOrderRecyclerAdapter.notifyDataSetChanged();
            orderedItemRecyclerList.setAdapter(confirmOrderRecyclerAdapter);

            //generateJSON();
            for(int i=0; i<tempItemList.size(); i++)
            {
                catIdList.add(tempItemList.get(i).getCatId());
            }

            getSchemes(catIdList);

            confirmOrderSchemeDetailsAdapter.setOnItemClickListner
                    (new ConfirmOrderSchemeRecyclerAdapter.onItemClickListner() {
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

                   // confirmOrderSchemeDetailsAdapter.setClickable(false);
                }
            });


            btnTempBill.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    System.out.println("big else go to web service");
                    //schemeListView.setEnabled(false);

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
                            .getColor(R.color.light_grey));
                    btnTempBill.setTextColor(getResources().getColor(R.color.dark_brown));

                    for (int i = 0; i < tempItemList.size(); i++) {
                        view = orderedItemRecyclerList.getChildAt(i);
                        EditText itemQuantity = view.findViewById(R.id.edtEditQuantity);
                        TextView itemPrice = view.findViewById(R.id.txtItemPrice);
                        TextView itemName = view.findViewById(R.id.txtItemName);
                        TextView itemCategoryId = view.findViewById(R.id.txtItemCategoryId);

                        String strItemQuantity = itemQuantity.getText().toString();
                        String strItemPrice = itemPrice.getText().toString();
                        String strItemName = itemName.getText().toString();
                        String strItemCategoryId = itemCategoryId.getText().toString();

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

                    System.out.println("finalItemList size " + finalItemList.size());
                    for(int i=0; i<finalItemList.size(); i++)
                    {
                        System.out.println("schemeAmount for loop " + schemeAmount);
                        System.out.println("schemeCategoryId for loop " + schemeCategoryId);
                        System.out.println("finalItemList id for loop "
                                + finalItemList.get(i).getCatId());

                        totalAmount = totalAmount + Double.parseDouble(finalItemList.get(i).getItemPrice());

                        if(finalItemList.get(i).getCatId().equals(schemeCategoryId))
                        {
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
                        }
                        else
                        {
                            nonDiscountedAmount = nonDiscountedAmount
                                    + Double.parseDouble(finalItemList.get(i).getItemPrice());

                            //nonDiscountAddition = nonDiscountAddition + nonDiscountedAmount;
                            System.out.println("nonDiscountedAmount " + nonDiscountedAmount);
                            System.out.println("nonDiscountAddition " + nonDiscountAddition);
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
                paymentModeRadioGroup.setOnCheckedChangeListener
                    (new RadioGroup.OnCheckedChangeListener()
                    {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup,int i)
                        {
                            int selectedId = radioGroup.getCheckedRadioButtonId();

                            radioButton = (RadioButton) view.findViewById(selectedId);

                            strPaymentMode = radioButton.getText().toString();
                        }
                    });
            }

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
                                insertOrderIntoDB();
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
        return view;
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

    public void getSchemes(List<String> catIdList)
    {
        try {
            Cursor res = db.getAllSchemeData();
            if(res.getCount() == 0) {
                // show message
                Toast.makeText(getActivity().getApplicationContext(),
                        "No Schemes Are Available", Toast.LENGTH_SHORT).show();
                return;
            }

            while (res.moveToNext()) {

                strCatId = res.getString(3);

                for(int i=0 ; i<catIdList.size();i++)
                {
                    if(catIdList.get(i).equals(strCatId))
                    {
                        tempSchemeIdList.add(res.getString(2));
                    }
                }
            }
            System.out.println("temp Scheme Id list size " + tempSchemeIdList.size());

            schemeIdList = removeDuplicateSchemeId(tempSchemeIdList);
            System.out.println("Scheme Id list " + schemeIdList.size());

            for(int i=0; i<schemeIdList.size();i++)
            {
                System.out.println("schemeIdList id " + schemeIdList.get(i));
                Cursor schemeRes = db.getAllSchemeDataFromSchemeId(schemeIdList.get(i));
                if(schemeRes.getCount() == 0) {
                    // show message
                    Toast.makeText(getActivity().getApplicationContext(),
                            "No Scheme Available", Toast.LENGTH_SHORT).show();
                    return;
                }

                while(schemeRes.moveToNext())
                {
                    String strEndDate = schemeRes.getString(9);

                    System.out.println("strEndDate " + strEndDate);
                    try {
                        SimpleDateFormat dateFormat1 = new SimpleDateFormat(
                                "dd/MM/yyyy", Locale.US);
                        String currentDate = dateFormat1.format(new Date());
                        Date endDate = dateFormat1.parse(strEndDate);
                        System.out.println(dateFormat1.format(endDate));
                        if (dateFormat1.parse(currentDate).after
                                (dateFormat1.parse(strEndDate))) {

                            System.out.println("currentDate in if " + currentDate);
                            System.out.println("strEndDate in if " + strEndDate);
                        }
                        else
                        {
                            schemeDetails = new SchemeDetails(
                                    schemeRes.getString(1),
                                    schemeRes.getString(2),
                                    schemeRes.getString(3),
                                    schemeRes.getString(4),
                                    schemeRes.getString(5),
                                    schemeRes.getString(6),
                                    schemeRes.getString(7),
                                    schemeRes.getString(8),
                                    schemeRes.getString(9),
                                    ""
                            );
                            showSchemeList.add(schemeDetails);
                        }
                    }
                    catch (Exception e)
                    {
                        e.getMessage();
                    }
                }
            }

            System.out.println("showSchemeList size " + showSchemeList.size());

            Collections.sort(showSchemeList, new Comparator<SchemeDetails>() {

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
            });

            confirmOrderSchemeDetailsAdapter = new ConfirmOrderSchemeRecyclerAdapter(
                    getActivity().getApplicationContext(), showSchemeList);
            confirmOrderSchemeDetailsAdapter.notifyDataSetChanged();
            schemeListView.setAdapter(confirmOrderSchemeDetailsAdapter);

        }
        catch (Exception e)
        {
            e.getMessage();
        }
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

            for (int i=0; i<finalItemList.size(); i++)
            {
                try {
                    reqObj = new JSONObject();
                    /*itemPrice = Integer.parseInt(finalItemList.get(i).getItemQuantity())
                            * Double.parseDouble(finalItemList.get(i).getItemPrice());*/

                    reqObj.put("itemId", finalItemList.get(i).getItemId());
                    reqObj.put("itemName", finalItemList.get(i).getItemName());
                    reqObj.put("itemQuantity", finalItemList.get(i).getItemQuantity());
                    reqObj.put("itemPrice", finalItemList.get(i).getItemPrice());
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

    public void insertOrderIntoDB()
    {
        System.out.println("in db " + outletId);
        System.out.println("in db " + companyId);
        System.out.println("in db " + orderId);
        System.out.println("in db " + txtDate.getText().toString());
        System.out.println("in db " + txtTime.getText().toString());
        System.out.println("in db " + edtGuestName.getText().toString());
        System.out.println("in db " + edtGuestContactNo.getText().toString());
        System.out.println("in db " + edtGuestAddress.getText().toString());
        System.out.println("in db " + outpassJSONString);
        System.out.println("in db " + txtTotalQuantity.getText().toString());
        System.out.println("in db " + strTotalAmount);
        System.out.println("in db " + selectedSchemeId);
        System.out.println("in db " + schemeAmount);
        System.out.println("in db " + discountAddition);
        System.out.println("in db " + strPaymentMode);
        try {
            boolean isInserted = db.insertFinalOrderData(
                    outletId,
                    companyId,
                    String.valueOf(orderId),
                    txtDate.getText().toString(),
                    txtTime.getText().toString(),
                    edtGuestName.getText().toString(),
                    edtGuestContactNo.getText().toString(),
                    edtGuestAddress.getText().toString(),
                    outpassJSONString,
                    txtTotalQuantity.getText().toString(),
                    String.valueOf(totalAmount),
                    selectedSchemeId,
                    String.valueOf(discountAddition),
                    String.valueOf(strTotalAmount),
                    strPaymentMode,
                    ""
            );

            if(isInserted)
            {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Order Inserted into DB", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(getActivity().getApplicationContext(),
                        "Something went wrong.", Toast.LENGTH_SHORT).show();
            }

            if(isNetworkAvailable())
            {
                ConfirmOrderAsync confirmOrderAsync = new ConfirmOrderAsync();
                confirmOrderAsync.execute();
            }
        }
        catch (Exception e)
        {
            e.getMessage();
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

                if(strResponse.equals("Unable To Saved Order Details."))
                {
                    Toast.makeText(getActivity().getApplicationContext(),
                            strResponse, Toast.LENGTH_SHORT).show();
                }
                else
                {
                    edtGuestName.getText().clear();
                    edtGuestContactNo.getText().clear();
                    edtGuestAddress.getText().clear();
                    txtTotalAmount.setText("");
                    txtTotalQuantity.setText("");
                    addItemsForOrderList.clear();
                    schemeIdList.clear();
                    showSchemeList.clear();
                    confirmOrderSchemeDetailsAdapter.notifyDataSetChanged();
                    tempItemList.clear();
                    confirmOrderRecyclerAdapter.notifyDataSetChanged();
                    //paymentModeRadioGroup.clearCheck();

                    Toast.makeText(getActivity().getApplicationContext(),
                            strResponse, Toast.LENGTH_LONG).show();

                    Fragment fragment = new GenerateOrderFragment();
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

        outletIdPI.setValue(outletId);
        orderIdPI.setValue(String.valueOf(orderId));
        orderDatePI.setValue(txtDate.getText().toString());
        orderTimePI.setValue(txtTime.getText().toString());
        guestNamePI.setValue(edtGuestName.getText().toString());
        guestMobileNoPI.setValue(edtGuestContactNo.getText().toString());
        guestAddressPI.setValue(edtGuestAddress.getText().toString());
        schemeIdPI.setValue(selectedSchemeId);
        totalBillAmountPI.setValue(String.valueOf(totalAmount));
        totalDiscountAmountPI.setValue(String.valueOf(discountAddition));
        finalBillAmountPI.setValue(String.valueOf(strTotalAmount));
        jsonPI.setValue(outpassJSONString);
        companyIdPI.setValue(companyId);

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
