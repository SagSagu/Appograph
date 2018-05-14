package com.sagsaguz.appograph.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.sagsaguz.appograph.AllFriendsActivity;
import com.sagsaguz.appograph.FriendDetailsActivity;
import com.sagsaguz.appograph.R;
import com.sagsaguz.appograph.utils.Model;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;
import static com.sagsaguz.appograph.AllFriendsActivity.allFriendsActivity;
import static com.sagsaguz.appograph.AllFriendsActivity.rv;

public class MultiViewTypeAdapter extends RecyclerView.Adapter implements View.OnClickListener, View.OnLongClickListener{

    List<Model> dataSet = new ArrayList<>();
    Context mContext;
    int total_types;
    Model object;
    private SharedPreferences phonePreferences;

    private static ImageView i1, i2, i3, i4;

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return true;
    }

    public static class ImageTypeViewHolder1 extends RecyclerView.ViewHolder {

        ImageView image1, image2;

        public ImageTypeViewHolder1(View itemView) {
            super(itemView);
            this.image1 = itemView.findViewById(R.id.hmv1);
            this.image2 = itemView.findViewById(R.id.hmv2);
            i1 = image1;
            i2 = image2;
        }
    }

    public static class ImageTypeViewHolder2 extends RecyclerView.ViewHolder {

        ImageView image;

        public ImageTypeViewHolder2(View itemView) {
            super(itemView);
            this.image = itemView.findViewById(R.id.hmv3);
            i3 = image;
        }
    }

    public static class ImageTypeViewHolder3 extends RecyclerView.ViewHolder {

        ImageView image3;

        public ImageTypeViewHolder3(View itemView) {
            super(itemView);
            this.image3 = itemView.findViewById(R.id.hmv4);
            i4 = image3;
        }
    }

    public MultiViewTypeAdapter(List<Model> data, Context context) {
        this.dataSet = data;
        this.mContext = context;
        total_types = dataSet.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        switch (viewType) {
            case Model.IMAGE_TYPE1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.left_list_item, parent, false);
                return new ImageTypeViewHolder1(view);
            case Model.IMAGE_TYPE2:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.right_list_item, parent, false);
                return new ImageTypeViewHolder2(view);
            case Model.IMAGE_TYPE3:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.only_left_list_item, parent, false);
                return new ImageTypeViewHolder3(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {

        switch (position%2) {
            case 0:
                //return Model.IMAGE_TYPE1;
                if(position == getItemCount()-1){
                    if((allFriendsActivity.userImageList.size())%3 == 1) {
                        return Model.IMAGE_TYPE3;
                    } else {
                        return Model.IMAGE_TYPE1;
                    }
                }else {
                    return Model.IMAGE_TYPE1;
                }
            case 1:
                if(allFriendsActivity.userImageList.size()==1){
                    return Model.IMAGE_TYPE3;
                } else {
                    return Model.IMAGE_TYPE2;
                }
            default:
                return -1;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int listPosition) {

        final int pos = listPosition;
        object = (Model) dataSet.get(listPosition);
        if (object != null) {
            switch (object.type) {
                case Model.IMAGE_TYPE1:
                    if(object.data != null){
                        ((ImageTypeViewHolder1) holder).image1.setImageBitmap(object.data);
                    } else {
                        ((ImageTypeViewHolder1) holder).image1.setImageResource(R.drawable.default_image);
                    }
                    if (object.image2 != null) {
                        ((ImageTypeViewHolder1) holder).image2.setImageBitmap(object.image2);
                    } else {
                        ((ImageTypeViewHolder1) holder).image2.setImageResource(R.drawable.default_image);
                    }
                    ((ImageTypeViewHolder1) holder).image1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                            //Toast.makeText(mContext, ""+(pos + (pos / 2)), Toast.LENGTH_SHORT).show();
                            if(object.data != null){
                                allFriendsActivity.hmv_main.setImageDrawable(((ImageTypeViewHolder1) holder).image1.getDrawable());
                            } else {
                                allFriendsActivity.hmv_main.setImageResource(R.drawable.default_image);
                            }
                            callFriend(i1);
                            //moveViewToScreenCenter(((ImageTypeViewHolder1) holder).image1);
                            phonePreferences = mContext.getSharedPreferences("USER", MODE_PRIVATE);
                            SharedPreferences.Editor editor = phonePreferences.edit();
                            editor.putString("PHONE", allFriendsActivity.userList.get(pos + (pos / 2)).getPhone());
                            editor.apply();
                            /*((ImageTypeViewHolder1) holder).image1.animate()
                                    .translationX((rv.getWidth() - ((ImageTypeViewHolder1) holder).image1.getWidth()) / 2)
                                    .translationY((rv.getHeight() - ((ImageTypeViewHolder1) holder).image1.getHeight()) / 2)
                                    .setInterpolator(new AccelerateInterpolator())
                                    .setDuration(500);*/
                        }
                    });
                    ((ImageTypeViewHolder1) holder).image1.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            allFriendsActivity.unFriendDialog((pos + (pos / 2)), ((ImageTypeViewHolder1) holder).image1);
                            return true;
                        }
                    });
                    ((ImageTypeViewHolder1) holder).image2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                            allFriendsActivity.hmv_main.setImageDrawable(((ImageTypeViewHolder1) holder).image2.getDrawable());
                            //Toast.makeText(mContext, ""+(pos + (pos / 2) + 1), Toast.LENGTH_SHORT).show();
                            /*if(object.image2 != null){
                                allFriendsActivity.hmv_main.setImageDrawable(((ImageTypeViewHolder1) holder).image2.getDrawable());
                            } else {
                                allFriendsActivity.hmv_main.setImageResource(R.drawable.default_image);
                            }*/
                            callFriend(i2);
                            //moveViewToScreenCenter(((ImageTypeViewHolder1) holder).image2);
                            phonePreferences = mContext.getSharedPreferences("USER", MODE_PRIVATE);
                            SharedPreferences.Editor editor = phonePreferences.edit();
                            editor.putString("PHONE", allFriendsActivity.userList.get(pos + (pos / 2) + 1).getPhone());
                            editor.apply();
                        }
                    });
                    ((ImageTypeViewHolder1) holder).image2.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            if((pos+1)<allFriendsActivity.userList.size()) {
                                allFriendsActivity.unFriendDialog((pos + (pos / 2) + 1), ((ImageTypeViewHolder1) holder).image2);
                            }
                            return true;

                        }
                    });
                    break;
                case Model.IMAGE_TYPE2:
                    if (object.data != null) {
                        ((ImageTypeViewHolder2) holder).image.setImageBitmap(object.data);
                    } else {
                        ((ImageTypeViewHolder2) holder).image.setImageResource(R.drawable.default_image);
                    }
                    ((ImageTypeViewHolder2) holder).image.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                            //Toast.makeText(mContext, ""+(pos + (pos / 2) + 1), Toast.LENGTH_SHORT).show();
                            if(object.data != null){
                                allFriendsActivity.hmv_main.setImageDrawable(((ImageTypeViewHolder2) holder).image.getDrawable());
                            } else {
                                allFriendsActivity.hmv_main.setImageResource(R.drawable.default_image);
                            }
                            callFriend(i3);
                            //moveViewToScreenCenter(((ImageTypeViewHolder2) holder).image);
                            phonePreferences = mContext.getSharedPreferences("USER", MODE_PRIVATE);
                            SharedPreferences.Editor editor = phonePreferences.edit();
                            editor.putString("PHONE", allFriendsActivity.userList.get(pos + (pos / 2) + 1).getPhone());
                            editor.apply();
                        }
                    });
                    ((ImageTypeViewHolder2) holder).image.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            allFriendsActivity.unFriendDialog((pos + (pos / 2) + 1), ((ImageTypeViewHolder2) holder).image);
                            return true;
                        }
                    });
                    break;
                case Model.IMAGE_TYPE3:
                    if (object.data != null) {
                        ((ImageTypeViewHolder3) holder).image3.setImageBitmap(object.data);
                    } else {
                        ((ImageTypeViewHolder3) holder).image3.setImageResource(R.drawable.default_image);
                    }
                    ((ImageTypeViewHolder3) holder).image3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputMethodManager imm = (InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                            if (imm != null) {
                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                            }
                            //Toast.makeText(mContext, ""+(pos + (pos / 2) + 1), Toast.LENGTH_SHORT).show();
                            if(object.data != null){
                                allFriendsActivity.hmv_main.setImageDrawable(((ImageTypeViewHolder3) holder).image3.getDrawable());
                            } else {
                                allFriendsActivity.hmv_main.setImageResource(R.drawable.default_image);
                            }
                            callFriend(i4);
                            //moveViewToScreenCenter(((ImageTypeViewHolder3) holder).image3);
                            phonePreferences = mContext.getSharedPreferences("USER", MODE_PRIVATE);
                            SharedPreferences.Editor editor = phonePreferences.edit();
                            editor.putString("PHONE", allFriendsActivity.userList.get(pos + (pos / 2)).getPhone());
                            editor.apply();
                        }
                    });
                    ((ImageTypeViewHolder3) holder).image3.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            allFriendsActivity.unFriendDialog((pos + (pos / 2)), ((ImageTypeViewHolder3) holder).image3);
                            return true;
                        }
                    });
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }

    private void moveViewToScreenCenter( View view )
    {

        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
        int statusBarOffset = dm.heightPixels - rv.getMeasuredHeight();

        allFriendsActivity.hmv_main.setVisibility(View.VISIBLE);
        int originalPos[] = new int[2];
        view.getLocationOnScreen( originalPos );
        allFriendsActivity.hmv_main.setX(originalPos[0]);
        //allFriendsActivity.hmv_main.setY(originalPos[1]-70);
        allFriendsActivity.hmv_main.setY(originalPos[1] - statusBarOffset + 50);

        int xDest = dm.widthPixels/2;
        xDest -= (view.getMeasuredWidth()/2);
        int yDest = dm.heightPixels/2 - (view.getMeasuredHeight()/2);

        allFriendsActivity.nx = xDest;
        allFriendsActivity.ny = yDest;

        allFriendsActivity.xpos = xDest - originalPos[0];
        //allFriendsActivity.ypos = yDest + 70 - originalPos[1];
        allFriendsActivity.ypos = yDest - originalPos[1] + 50;

        //TranslateAnimation translate = new TranslateAnimation( 0, xDest - originalPos[0] , 0, yDest + 70 - originalPos[1] );
        TranslateAnimation translate = new TranslateAnimation( 0, xDest - originalPos[0] , 0, yDest + 50 - originalPos[1] );
        translate.setDuration(500);
        translate.setFillAfter( true );
        allFriendsActivity.hmv_main.startAnimation(translate);
        translate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                allFriendsActivity.callNewActivity();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    public void setFilter(List<Model> newList){
        dataSet = new ArrayList<>();
        dataSet.addAll(newList);
        notifyDataSetChanged();
    }

    public void callFriend(ImageView imageView){
        Intent sharedIntent = new Intent(allFriendsActivity, FriendDetailsActivity.class);

        Pair pair = new Pair<View, String>(imageView, "profile");
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(allFriendsActivity, pair);

        allFriendsActivity.startActivity(sharedIntent, options.toBundle());
    }

}
