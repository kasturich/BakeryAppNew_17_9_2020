package com.mi5.bakeryappnew.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.activity.NavigationDrawerActivity;
import com.mi5.bakeryappnew.adapter.ItemDatabaseDetailsAdapter;
import com.mi5.bakeryappnew.adapter.SimpleSpinnerArrayAdapter;
import com.mi5.bakeryappnew.database.MyDBHandler;
import com.mi5.bakeryappnew.model.AddItemsForOrder;
import com.mi5.bakeryappnew.model.CategoryDatabaseDetails;
import com.mi5.bakeryappnew.model.ItemDatabaseDetails;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenerateOrderFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenerateOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenerateOrderFragment extends Fragment
        implements AdapterView.OnItemSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    boolean serverConnection=false;
    SharedPreferences sharedpreferences;
    boolean hasLoggedIn, downloadData;
    MyDBHandler db;
    String companyId, jsonSharedPreference;
    String catPrimaryId, itemPrimaryId;

    SwipeRefreshLayout swipeToRefresh;
    Spinner categorySpinner;
    ListView itemListView;
    FloatingActionButton floatingActionButton;

    Bundle bundle = new Bundle();

    ItemDatabaseDetails itemDatabaseDetails;
    List<ItemDatabaseDetails> itemDatabaseDetailsList;
    ItemDatabaseDetailsAdapter itemDatabaseDetailsAdapter;

    CategoryDatabaseDetails categoryDatabaseDetails;
    List<CategoryDatabaseDetails> categoryDatabaseDetailsList;
    SimpleSpinnerArrayAdapter simpleSpinnerArrayAdapter;

    String itemName, strTotalItemQuantity, keyItem, valueItem, orderItemJSONString;
    int itemCount=0, totalQuantity=0;

    AddItemsForOrder addItemsForOrder, addItemsForOrderParcable, addItemsForBackOrder;
    List<AddItemsForOrder> addItemsForOrderList;

    ArrayList<AddItemsForOrder> addItemsForOrdersParcelableList
            = new ArrayList<AddItemsForOrder>();

    String valueItemSeparator[];

    Map<String, String> map = new TreeMap<String, String>();
    Set keys;
    Iterator ii;

    private OnFragmentInteractionListener mListener;

    public GenerateOrderFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GenerateOrderFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GenerateOrderFragment newInstance(String param1, String param2) {
        GenerateOrderFragment fragment = new GenerateOrderFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_order, container, false);

        ((NavigationDrawerActivity) getActivity()).getSupportActionBar()
                .setTitle(getResources().getString(R.string.nav_header_generate_order));

        //swipeToRefresh = view.findViewById(R.id.swipeToRefresh);
        categorySpinner = view.findViewById(R.id.categorySpinner);
        itemListView = view.findViewById(R.id.itemListView);
        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        swipeToRefresh.setColorSchemeResources(R.color.colorAccent);

        sharedpreferences = getActivity().getSharedPreferences("LoginDetails",
                Context.MODE_PRIVATE);

        hasLoggedIn = sharedpreferences.getBoolean("hasLoggedIn", false);
        downloadData = sharedpreferences.getBoolean("downloadData", false);

        jsonSharedPreference = sharedpreferences.getString("JSON", "");

        System.out.println("downloadData" + downloadData);

        if (jsonSharedPreference != null) {
            try {
                JSONArray jarray = new JSONArray(jsonSharedPreference);
                System.out.println("jarray length " + jarray.length());
                JSONObject json_obj = jarray.getJSONObject(0);
                for (int i=0; i<jarray.length(); i++)
                {
                    //System.out.println("personal info user_id " + user_id);
                    companyId = json_obj.getString("CompanyId");
                    System.out.println("company id " + companyId);
                }

            } catch (JSONException e) {

                System.out.println("sharedpreference error = " + e.getMessage());
            }
        }

        db = new MyDBHandler(getActivity().getApplicationContext());

        itemDatabaseDetailsList = new ArrayList<ItemDatabaseDetails>();
        categoryDatabaseDetailsList = new ArrayList<CategoryDatabaseDetails>();
        addItemsForOrderList = new ArrayList<AddItemsForOrder>();

        categorySpinner.setOnItemSelectedListener(this);

        insertCategoriesIntoList();

        itemListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Toast.makeText(getActivity().getApplicationContext(),
                        "Item is selected", Toast.LENGTH_SHORT).show();
                System.out.println("add button item id " + itemDatabaseDetailsList.get(i).getItemId());
                //itemCount = 0;

                //itemCount = itemCount + 1;

                addItemsForOrder = new AddItemsForOrder(
                        itemDatabaseDetailsList.get(i).getItemId(),
                        itemDatabaseDetailsList.get(i).getItemName(),
                        String.valueOf(itemCount),
                        itemDatabaseDetailsList.get(i).getAmount(),
                        itemDatabaseDetailsList.get(i).getCategoryId()
                );

                addItemsForOrderList.add(addItemsForOrder);

                System.out.println("itemCount " + itemCount);
                System.out.println("addItemsForOrderList size " + addItemsForOrderList.size());


            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                for(int i=0; i<addItemsForOrderList.size(); i++)
                {
                    System.out.println("i " + i);

                    itemCount = countOccurance(addItemsForOrderList,
                            addItemsForOrderList.size(),
                            Integer.parseInt(addItemsForOrderList.get(i).getItemId()));

                    addItemsForOrderParcable = new AddItemsForOrder(
                            addItemsForOrderList.get(i).getItemId(),
                            addItemsForOrderList.get(i).getItemName(),
                            String.valueOf(itemCount),
                            addItemsForOrderList.get(i).getItemPrice(),
                            addItemsForOrderList.get(i).getCatId()
                    );
                    addItemsForOrdersParcelableList.add(addItemsForOrderParcable);

                    System.out.println("addItemsForOrdersParcelableList id "
                    + addItemsForOrdersParcelableList.get(i).getItemId());

                    System.out.println("addItemsForOrdersParcelableList quantity "
                            + addItemsForOrdersParcelableList.get(i).getItemQuantity());
                }

                Bundle bundle = new Bundle();
                bundle.putParcelable("addOrderItem", addItemsForOrderParcable);
                bundle.putParcelableArrayList("addItemsForOrdersParcelableList", addItemsForOrdersParcelableList);
                //bundle.putParcelableArrayList("addOrderItemList", addItemsForOrderList);
                //args.putString(ARG_PARAM2, param2);
                //Navigation.findNavController(view).navigate(R.id.nav_confirm_order, bundle);

                Fragment fragment = new ConfirmOrderFragment();
                fragment.setArguments(bundle);
                getFragmentManager().beginTransaction()
                        .addToBackStack(null)
                        .commit();
                if (fragment != null) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();

                    // set the toolbar title
                    // getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                    //toolbar_title.setText(getResources().getString(R.string.app_name));
                }
            }
        });

        return view;
    }

    public int countOccurance(List<AddItemsForOrder> addItemsForOrdersParcelableList,
                              int listSize, int searchId)
    {
        int res =0;
        for(int i=0; i<listSize; i++)
        {
            if(searchId == Integer.parseInt(
                    addItemsForOrdersParcelableList.get(i).getItemId()))
            {
                res++;
            }
        }
        return res;
    }

    public void insertCategoriesIntoList()
    {
        try {
            Cursor res = db.getAllCategoryData();
            if(res.getCount() == 0) {
                // show message
                Toast.makeText(getActivity().getApplicationContext(),
                        "No Categories Available", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {

                System.out.println("list id id  :"+ res.getString(0)+"\n");
                System.out.println("list company id  :"+ res.getString(1)+"\n");
                System.out.println("list scheme id  :"+ res.getString(2)+"\n");

                categoryDatabaseDetails = new CategoryDatabaseDetails(
                        res.getString(0),
                        res.getString(1),
                        res.getString(2),
                        res.getString(3));
                categoryDatabaseDetailsList.add(categoryDatabaseDetails);

                /*simpleSpinnerArrayAdapter = new SimpleSpinnerArrayAdapter
                        (getActivity().getApplicationContext(), categoryDatabaseDetailsList);
                simpleSpinnerArrayAdapter.notifyDataSetChanged();
                categorySpinner.setAdapter(simpleSpinnerArrayAdapter);*/
            }
            System.out.println("schemeDetailsList size on save = " + categoryDatabaseDetailsList.size());
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    public void insertCategoryItemIntoList(String catPrimaryId)
    {
        try {
            Cursor res = db.getAllCategoryItemData(catPrimaryId);
            if(res.getCount() == 0) {
                // show message

                itemDatabaseDetails = new ItemDatabaseDetails(
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0",
                        "0"
                );
                itemDatabaseDetailsList.add(itemDatabaseDetails);

                itemDatabaseDetailsAdapter = new ItemDatabaseDetailsAdapter
                        (getActivity().getApplicationContext(), itemDatabaseDetailsList);
                itemDatabaseDetailsAdapter.notifyDataSetChanged();
                itemListView.setAdapter(itemDatabaseDetailsAdapter);

                Toast.makeText(getActivity().getApplicationContext(),
                        "No Items Available", Toast.LENGTH_SHORT).show();
                return;
            }

            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()) {

                System.out.println("list id id  :"+ res.getString(0)+"\n");
                System.out.println("list item id  :"+ res.getString(1)+"\n");
                System.out.println("list item name  :"+ res.getString(2)+"\n");

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

                itemDatabaseDetailsAdapter = new ItemDatabaseDetailsAdapter
                        (getActivity().getApplicationContext(), itemDatabaseDetailsList);
                itemDatabaseDetailsAdapter.notifyDataSetChanged();
                itemListView.setAdapter(itemDatabaseDetailsAdapter);
            }
            System.out.println("Item List size = " + itemDatabaseDetailsList.size());
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
    {
        try {
            itemDatabaseDetailsList.clear();
            catPrimaryId =categoryDatabaseDetailsList.get(i).getId();
            Log.e("Category Database", catPrimaryId);
            insertCategoryItemIntoList(catPrimaryId);
        }
        catch (Exception e)
        {
            e.getMessage();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
