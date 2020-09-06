package com.mi5.bakeryappnew.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.mi5.bakeryappnew.R;
import com.mi5.bakeryappnew.fragment.GenerateOrderFragment;
import com.mi5.bakeryappnew.fragment.GenerateOrderNewFragment;
import com.mi5.bakeryappnew.fragment.SchemeDetailsFragment;
import com.mi5.bakeryappnew.fragment.SellReportFragment;
import com.mi5.bakeryappnew.model.GridModel;
import com.mi5.bakeryappnew.model.SchemeDetails;

import java.util.List;


/**
 * Created by User on 22-08-2018.
 */

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.MyViewHolder>  {

    private Context mContext;
    private List<GridModel> albumList;
    AppCompatActivity activity;

    public class MyViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{
        public TextView title, count;
        public ImageView thumbnail, overflow;
        public CardView card_view;
        public RelativeLayout mainCardRelative, thumbnailRelative;

        public MyViewHolder(View view) {
            super(view);

            mContext = view.getContext();
            activity = (AppCompatActivity) view.getContext();

            title = (TextView) view.findViewById(R.id.title);
            //count = (TextView) view.findViewById(R.id.count);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            //overflow = (ImageView) view.findViewById(R.id.overflow);
            card_view = view.findViewById(R.id.card_view);
            mainCardRelative = view.findViewById(R.id.mainCardRelative);
            thumbnailRelative = view.findViewById(R.id.thumbnailRelative);
        }

        @Override
        public void onClick(View v) {

            final Intent intent;

            System.out.println("item count " + getItemCount());
        }
    }


    public HomeAdapter(Context mContext, List<GridModel> albumList) {
        this.mContext = mContext;
        this.albumList = albumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_grid_item1, parent, false);


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        GridModel album = albumList.get(position);

        holder.title.setTextColor(album.getCircleColorCode());
        holder.title.setText(album.getMainTitle());
        //holder.count.setText(Integer.toString(album.getSubTitle()));

        //holder.thumbnailRelative.setBackgroundColor(album.getColorCode());
            holder.thumbnail.setImageResource(album.getThumbnail());

        ((GradientDrawable)holder.thumbnailRelative.getBackground()).setColor(album.getCircleColorCode());

        holder.card_view.setBackgroundColor(album.getColorCode());
        holder.mainCardRelative.setBackgroundColor(album.getColorCode());

        /*holder.overflow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.overflow);
            }
        });*/

        int containerId = holder.mainCardRelative.getId();// Get container id
        FragmentManager fragmentManager1 = activity.getSupportFragmentManager();
        Fragment oldFragment = fragmentManager1.findFragmentById(containerId);
        if(oldFragment != null) {
            fragmentManager1.beginTransaction().remove(oldFragment).commit();
        }

        int newContainerId = View.generateViewId();// Generate unique container id
        holder.mainCardRelative.setId(newContainerId);// Set container id

        holder.mainCardRelative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showPopupMenu(holder.thumbnail);
                Intent intent=null;
                Fragment fragment=null;

                switch (position) {

                    case 0:
                        fragment = new SchemeDetailsFragment();
                        activity.getFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .commit();
                        if (fragment != null) {
                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                            // set the toolbar title
                            // getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                            //toolbar_title.setText(getResources().getString(R.string.app_name));
                        }
                        break;

                    case 1:
                        //intent =  new Intent(mContext, SecondActivity.class);
                        //fragment = new GenerateOrderFragment();
                        fragment = new GenerateOrderNewFragment();
                        activity.getFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .commit();
                        if (fragment != null) {
                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                            // set the toolbar title
                            // getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                            //toolbar_title.setText(getResources().getString(R.string.app_name));
                        }
                        break;

                    case 2:
                        //intent =  new Intent(mContext, SecondActivity.class);
                        fragment = new SellReportFragment();
                        activity.getFragmentManager().beginTransaction()
                                .addToBackStack(null)
                                .commit();
                        if (fragment != null) {
                            FragmentManager fragmentManager = activity.getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.nav_host_fragment, fragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                            // set the toolbar title
                            // getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
                            //toolbar_title.setText(getResources().getString(R.string.app_name));
                        }
                        break;

                    default:
                        //intent =  new Intent(mContext, DefaultActivity.class);
                        break;
                }


            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        /*PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();*/

        Toast.makeText(mContext, "Add your action", Toast.LENGTH_SHORT).show();
    }

    /**
     * Click listener for popup menu items
     */
    /*class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }*/

    @Override
    public int getItemCount() {
        return albumList.size();
    }

}
