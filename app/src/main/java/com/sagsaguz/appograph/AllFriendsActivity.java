package com.sagsaguz.appograph;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.method.KeyListener;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.sagsaguz.appograph.adapter.MultiViewTypeAdapter;
import com.sagsaguz.appograph.utils.AlarmReceiver;
import com.sagsaguz.appograph.utils.AppRater;
import com.sagsaguz.appograph.utils.DatabaseHelper;
import com.sagsaguz.appograph.utils.Friends;
import com.sagsaguz.appograph.utils.HexagonMaskView;
import com.sagsaguz.appograph.utils.Model;
import com.soundcloud.android.crop.Crop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import tyrantgit.explosionfield.ExplosionField;

public class AllFriendsActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, NavigationView.OnNavigationItemSelectedListener{

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 4 ;

    public List<Friends> userList = new ArrayList<>();
    public List<Model> list = new ArrayList<>();
    public List<Bitmap> userImageList = new ArrayList<>();
    public static RecyclerView  rv;
    public HexagonMaskView hmv_main;
    public static AllFriendsActivity allFriendsActivity;
    public static final int NEW_ACTIVITY = 123;
    public int xpos, ypos, nx, ny;
    private MultiViewTypeAdapter adapter;
    private FloatingActionButton fb_addFriend;
    Dialog unFriendDialog, dialog;

    private ImageView nav_profile_image;
    private EditText nav_profile_name;
    private ImageView nav_header_edit, nav_header_save;
    private SharedPreferences userPreferences;
    private String userName, userImagePath;

    private AdView adView;

    private String storageType = "null";
    private EditText etSearchView;
    private DatabaseHelper databaseHelper;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_friends_layout);

        allFriendsActivity = this;

        Typeface font = Typeface.createFromAsset(this.getAssets(), "signature2_font.ttf");
        Toolbar toolbar = (Toolbar) findViewById(R.id.tbCustom);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView tvCustom = (TextView) findViewById(R.id.tvCustom);
        tvCustom.setText("Appograph");
        tvCustom.setTypeface(font);

        hmv_main = (HexagonMaskView) findViewById(R.id.hmv_main);
        hmv_main.setVisibility(View.GONE);

        displayFriends();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer,toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View navigationHeader = navigationView.inflateHeaderView(R.layout.navigation_header);

        userPreferences = getSharedPreferences("USER_DETAILS", MODE_PRIVATE);
        userImagePath = userPreferences.getString("USER_IMAGE_PATH", "/storage/emulated/0/Appograph/Images/appograph-user.jpg");
        userName = userPreferences.getString("USER_NAME", "Appograph ");

        nav_profile_image = navigationHeader.findViewById(R.id.nav_profile_image);
        if(checkReadExternalStoragePermission()) {
            File file = new File(userImagePath);
            if (file.exists()) {
                nav_profile_image.setImageBitmap(BitmapFactory.decodeFile(userImagePath));
            } else {
                nav_profile_image.setImageResource(R.drawable.default_image);
            }
        } else {
            nav_profile_image.setImageResource(R.drawable.default_image);
        }
        nav_profile_name = navigationHeader.findViewById(R.id.nav_profile_name);
        nav_profile_name.setText(userName);
        nav_profile_name.setTypeface(font);
        nav_profile_name.setTag(nav_profile_name.getKeyListener());
        nav_header_edit = navigationHeader.findViewById(R.id.nav_header_edit);
        nav_header_save = navigationHeader.findViewById(R.id.nav_header_save);
        notEditable();

        fb_addFriend = (FloatingActionButton) findViewById(R.id.fb_addFriend);
        fb_addFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllFriendsActivity.this, AddFriendActivity.class));
                finish();
            }
        });

        launchingService();

        AppRater.app_launched(AllFriendsActivity.this);

        nav_profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(nav_header_save.getVisibility() == View.VISIBLE){
                    storageType = "userImage";
                    Crop.pickImage(AllFriendsActivity.this);
                }
            }
        });

        nav_header_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                View v1 = getCurrentFocus();
                if (v != null) {
                    if (imm != null) {
                        imm.showSoftInput(v1, 0);
                    }
                }
                editable();
            }
        });

        nav_header_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                notEditable();
                SharedPreferences.Editor editor = userPreferences.edit();
                editor.putString("USER_NAME", " "+nav_profile_name.getText().toString()+" ");
                editor.apply();
            }
        });

        adView = (AdView) findViewById(R.id.adView);
        //adView.setAdSize(AdSize.BANNER);
        //adView.setAdUnitId(getString(R.string.banner_home_footer));

        /*@SuppressLint("HardwareIds")
        String android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String deviceId = md5(android_id).toUpperCase();*/

        //.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        //.addTestDevice(deviceId)

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                /*new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AllFriendsActivity.this, "Working", Toast.LENGTH_SHORT).show();
                        adView.setVisibility(View.GONE);
                    }
                }, 5000);*/
            }

            @Override
            public void onAdClosed() {
                //Toast.makeText(getApplicationContext(), "Ad is closed!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                //Toast.makeText(getApplicationContext(), "Ad failed to load! error code: " + errorCode, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLeftApplication() {
                //Toast.makeText(getApplicationContext(), "Ad left application!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdOpened() {
                super.onAdOpened();
            }
        });

        mHandler = new Handler();
        startRepeatingTask();

    }

    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            if (isOnline()){
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                adView.loadAd(adRequest);
                adView.setVisibility(View.VISIBLE);
            } else {
                adView.setVisibility(View.GONE);
            }
            mHandler.postDelayed(mStatusChecker, 1000);
        }
    };

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }

    public Boolean isOnline() {
        try {
            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = p1.waitFor();
            return (returnVal==0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String md5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            Log.d("DEVICEID", "device id exception");
        }
        return null;
    }

    private void notEditable(){
        nav_header_save.setVisibility(View.GONE);
        nav_profile_name.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_IN);
        nav_profile_name.setKeyListener(null);
        nav_header_edit.setVisibility(View.VISIBLE);
    }

    private void editable(){
        nav_header_edit.setVisibility(View.GONE);
        nav_profile_name.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        nav_profile_name.setKeyListener((KeyListener) nav_profile_name.getTag());
        nav_header_save.setVisibility(View.VISIBLE);
    }

    private void displayFriends(){
        userList.clear();
        userImageList.clear();
        list.clear();
        databaseHelper = new DatabaseHelper(AllFriendsActivity.this);
        try {
            userList = databaseHelper.getAllUsers();
            if(userList.size()==0)
                Toast.makeText(allFriendsActivity, "You don't have any friends in Appograph", Toast.LENGTH_SHORT).show();
        } catch (NullPointerException e){
            Toast.makeText(allFriendsActivity, "No friends have been added", Toast.LENGTH_SHORT).show();
        }
        for (int i=0; i<userList.size(); i++){
            userImageList.add(BitmapFactory.decodeFile(userList.get(i).getImage()));
        }

        for(int i=0;i<userList.size();i++){
            if((i+1)%3 == 0)
                list.add(new Model(Model.IMAGE_TYPE2,userImageList.get(i)));
            else if((i+1)%3 == 1){
                if(i==userImageList.size()-1){
                    list.add(new Model(Model.IMAGE_TYPE3,userImageList.get(i)));
                } else {
                    list.add(new Model(Model.IMAGE_TYPE1,userImageList.get(i),userImageList.get(i+1)));
                }
            }
        }

        adapter = new MultiViewTypeAdapter(list,this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        rv = mRecyclerView;
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
    }

    public void callNewActivity(){
        startActivityForResult(new Intent(getBaseContext(), FriendDetailsActivity.class), NEW_ACTIVITY);
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_ACTIVITY) {
            TranslateAnimation translate = new TranslateAnimation( xpos, 0 , ypos, 0 );
            translate.setDuration(300);
            translate.setFillAfter( true );
            hmv_main.startAnimation(translate);
            translate.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {
                    hmv_main.clearAnimation();
                    hmv_main.setVisibility(View.GONE);
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
        } else if(resultCode == RESULT_OK){
            if(requestCode == Crop.REQUEST_PICK){
                Uri uri = data.getData();
                Uri dest = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(uri, dest).asSquare().start(this);
                nav_profile_image.setImageURI(Crop.getOutput(data));
            } else if(requestCode == Crop.REQUEST_CROP){
                handle(resultCode, data);
            }
        }
    }*/

    public void unFriendDialog(final int pos, final View view){
        unFriendDialog = new Dialog(this);
        unFriendDialog.setContentView(R.layout.un_friend_dialog);
        unFriendDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView message = unFriendDialog.findViewById(R.id.un_friend_message);
        Button btnYes = unFriendDialog.findViewById(R.id.btnYes);
        Button btnNo = unFriendDialog.findViewById(R.id.btnNo);

        message.setText("Are you sure, you want to un friend \""+userList.get(pos).getName()+"\" from your Appograph...");

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if((databaseHelper.deleteUser(allFriendsActivity.userList.get(pos).getPhone()))>0) {
                    deleteFile(pos);
                    allFriendsActivity.userList.remove(pos);
                    allFriendsActivity.userImageList.remove(pos);
                    Toast.makeText(getBaseContext(), "Friends list updated", Toast.LENGTH_SHORT).show();
                    unFriendDialog.dismiss();
                    ExplosionField explosionField = new ExplosionField(getBaseContext());
                    explosionField.explode(view);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            displayFriends();
                        }
                    }, 500);
                } else {
                    Toast.makeText(AllFriendsActivity.this, "Error in deleting your friend details...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unFriendDialog.dismiss();
            }
        });

        unFriendDialog.show();
    }

    public void deleteFile(int pos){
        File cover = new File(userList.get(pos).getCoverImage());
        boolean coverDeleted = cover.delete();
        File profile = new File(userList.get(pos).getImage());
        boolean profileDeleted = profile.delete();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setMaxWidth(Integer.MAX_VALUE);
        searchView.setQueryHint("Search...");

        View searchplate = (View)searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchplate.setBackgroundResource(R.color.emerald_green);

        etSearchView = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        etSearchView.setHintTextColor(getResources().getColor(R.color.white));
        etSearchView.setTextColor(getResources().getColor(R.color.white));

        ImageView searchClose = searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);
        searchClose.setImageResource(R.drawable.icon_close);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        //newText = newText.toLowerCase();
        newText = etSearchView.getText().toString().toLowerCase();
        List<Friends> newList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(AllFriendsActivity.this);
        userList.clear();
        userImageList.clear();
        try {
            newList = databaseHelper.getAllUsers();
        } catch (NullPointerException e){
            Toast.makeText(allFriendsActivity, "No friends have been added", Toast.LENGTH_SHORT).show();
        }
        for (Friends friends : newList){
            String name = friends.getName().toLowerCase();
            if(name.contains(newText)) {
                //newList.add(friends);
                userList.add(friends);
                userImageList.add(BitmapFactory.decodeFile(friends.getImage()));
            }
        }

        list.clear();
        for(int i=0;i<userList.size();i++){
            if((i+1)%3 == 0)
                list.add(new Model(Model.IMAGE_TYPE2,userImageList.get(i)));
            else if((i+1)%3 == 1){
                if(i==userImageList.size()-1){
                    list.add(new Model(Model.IMAGE_TYPE3,userImageList.get(i)));
                } else {
                    list.add(new Model(Model.IMAGE_TYPE1,userImageList.get(i),userImageList.get(i+1)));
                }
            }
        }

        adapter.setFilter(list);

        return true;
    }

    private void launchingService(){
        Intent alarm = new Intent(AllFriendsActivity.this, AlarmReceiver.class);
        Boolean alarmRunning = (PendingIntent.getBroadcast(AllFriendsActivity.this, 0, alarm, PendingIntent.FLAG_NO_CREATE) != null);
        if(!alarmRunning) {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AllFriendsActivity.this, 0, alarm, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime(), 3600*1000*6, pendingIntent);
            }
        }
    }

    private void saveDBDialog(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.un_friend_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView subject = dialog.findViewById(R.id.un_friend);
        TextView message = dialog.findViewById(R.id.un_friend_message);
        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);

        subject.setText("Saving details");
        message.setText("Do you want to overwrite the existing file?");
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDBToFile();
                dialog.dismiss();
            }
        });
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void saveDBToFile(){
        try {
            File path = Environment.getExternalStorageDirectory();
            File dir = new File(path + "/Appograph/");
            dir.mkdirs();
            File file = new File(dir, "appograph_db.txt");
            if (file.exists())
                file.delete();
            //file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            for (int i=0; i<userList.size(); i++){
                myOutWriter.append(userList.get(i).getName()).append("$")
                            .append(userList.get(i).getDob()).append("$")
                            .append(userList.get(i).getPhone()).append("$")
                            .append(userList.get(i).getAlternate_phone()).append("$")
                            .append(userList.get(i).getEmail()).append("$")
                            .append(userList.get(i).getWhatsapp()).append("$")
                            .append(userList.get(i).getHobbies()).append("$")
                            .append(userList.get(i).getOthers()).append("$")
                            .append(userList.get(i).getImage()).append("$")
                            .append(userList.get(i).getCoverImage()).append("\n\n");
            }
            myOutWriter.close();
            Toast.makeText(allFriendsActivity, "Successfully Backed up your friends details", Toast.LENGTH_SHORT).show();
            fOut.close();
        } catch (Exception e) {
            Toast.makeText(getBaseContext(), e.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void readFileToDB(){
        File path = Environment.getExternalStorageDirectory();
        File dir = new File(path + "/Appograph/");
        File fl = new File(dir, "appograph_db.txt");
        if(!fl.exists()) {
            Toast.makeText(allFriendsActivity, "File not found.\nPlease put your file in Appograph folder.", Toast.LENGTH_LONG).show();
        } else {
            FileInputStream fin = null;
            try {
                fin = new FileInputStream(fl);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            String ret = null;
            try {
                ret = convertStreamToString(fin);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String[] individualUsers = new String[0];
            if (ret != null) {
                individualUsers = ret.split("\n\n");
            }
            //Toast.makeText(allFriendsActivity, ""+ individualUsers.length, Toast.LENGTH_LONG).show();

            for (String individualUserData : individualUsers) {
                StringTokenizer tokens = new StringTokenizer(individualUserData, "$");
                //Toast.makeText(allFriendsActivity, "" + first + "\n" + second, Toast.LENGTH_LONG).show();
                Friends friends = new Friends(tokens.nextToken(), tokens.nextToken(), tokens.nextToken(), tokens.nextToken(),
                        tokens.nextToken(), tokens.nextToken(), tokens.nextToken(), tokens.nextToken(),
                        tokens.nextToken(), tokens.nextToken());
                databaseHelper = new DatabaseHelper(AllFriendsActivity.this);
                String user = databaseHelper.checkingUserAlreadyExistsOrNot(friends.getPhone());
                if (user.equals("new")) {
                    databaseHelper.addUser(friends);
                }
            }
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(allFriendsActivity, "Successfully imported your friends details", Toast.LENGTH_SHORT).show();
            displayFriends();
        }
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + BuildConfig.APPLICATION_ID)));
        } else if (id == R.id.nav_help_feedback) {
            Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://play.google.com/store/apps/details?id=com.sagsaguz.appograph&hl=en"));
            startActivity(goToMarket);
        } else if (id == R.id.nav_export) {
            storageType = "export";
            checkStoragePermission();
        } else if (id == R.id.nav_import){
            storageType = "import";
            checkStoragePermission();
        }/* else if (id == R.id.nav_about){
            showAboutApp();
        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void checkStoragePermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(AllFriendsActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck == 0) {
            checkStorageType();
        } else {
            dialog = new Dialog(AllFriendsActivity.this);
            dialog.setContentView(R.layout.permission_dialog);

            TextView tvPermissionTitle = dialog.findViewById(R.id.tvPermissionTitle);
            TextView tvPermissionDesc = dialog.findViewById(R.id.tvPermissionDesc);
            TextView tvCancel = dialog.findViewById(R.id.tvCancel);
            TextView tvSettings = dialog.findViewById(R.id.tvSettings);
            TextView tvOk = dialog.findViewById(R.id.tvOk);

            tvPermissionTitle.setText("STORAGE Permission");
            tvPermissionDesc.setText(R.string.storage_permission);
            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            tvSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            tvOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    ActivityCompat.requestPermissions(AllFriendsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                    ActivityCompat.requestPermissions(AllFriendsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            });

            dialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkStorageType();
                }
                break;
            }
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkStorageType();
                }
                break;
            }
        }
    }

    private void checkStorageType(){
        if(storageType.equals("userImage")){
            userImageStoragePath();
        } else if(storageType.equals("export")){
            File file = new File("/storage/emulated/0/Appograph/appograph_db.txt");
            if(file.exists()) {
                saveDBDialog();
            } else {
                saveDBToFile();
            }
        } else {
            readFileToDB();
        }
    }

    private String userImageStoragePath() {
        Bitmap bitmap = ((BitmapDrawable) nav_profile_image.getDrawable()).getBitmap();
        //path to store image
        File path = Environment.getExternalStorageDirectory();
        File dirProfile = new File(path + "/Appograph/Images/");
        dirProfile.mkdirs();
        File imageFile = new File(dirProfile, "appograph-user" + ".jpg");
        if (imageFile.exists())
            imageFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return imageFile.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
        return "null";
    }

    private void handle(int code, Intent result){
        if(code == RESULT_OK){
            nav_profile_image.setImageURI(Crop.getOutput(result));
            checkStoragePermission();
        }
    }

    private void showAboutApp(){
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.un_friend_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        TextView subject = dialog.findViewById(R.id.un_friend);
        TextView message = dialog.findViewById(R.id.un_friend_message);
        Button btnYes = dialog.findViewById(R.id.btnYes);
        btnYes.setVisibility(View.GONE);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        btnNo.setText("Cancel");

        subject.setText("About Appograph");
        message.setText("Do you want to overwrite the existing file?");

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean checkReadExternalStoragePermission()
    {
        int result = ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        return (result == PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onPause() {
        if (adView != null) {
            adView.pause();
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adView != null) {
            adView.resume();
        }
    }

    @Override
    public void onDestroy() {
        if (adView != null) {
            adView.destroy();
        }
        stopRepeatingTask();
        super.onDestroy();
    }

}
