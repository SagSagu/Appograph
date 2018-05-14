package com.sagsaguz.appograph;

import android.Manifest;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sagsaguz.appograph.adapter.ViewPagerAdapter;
import com.sagsaguz.appograph.fragments.ContactFragment;
import com.sagsaguz.appograph.fragments.DOBFragment;
import com.sagsaguz.appograph.fragments.HobbiesFragment;
import com.sagsaguz.appograph.fragments.NameFragment;
import com.sagsaguz.appograph.fragments.OthersFragment;
import com.sagsaguz.appograph.utils.CookieThumperSample;
import com.sagsaguz.appograph.utils.DatabaseHelper;
import com.sagsaguz.appograph.utils.Friends;
import com.sagsaguz.appograph.utils.HexagonMaskView;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import su.levenetc.android.textsurface.TextSurface;

import static com.sagsaguz.appograph.AllFriendsActivity.allFriendsActivity;

public class FriendDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 4 ;
    private RelativeLayout add_new_friend;
    private HexagonMaskView hmv_profile;
    public static DatabaseHelper databaseHelper;
    private TextSurface textSurface;
    private TextView tvName;
    private ImageView iv_cover_pic;
    private RelativeLayout rl_details;
    public static List<Friends> details = new ArrayList<>();
    public static String userPhone;
    private Dialog chooserDialog;
    private SharedPreferences phonePreferneces;
    private ImageView selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_details_layout);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        iv_cover_pic = (ImageView) findViewById(R.id.iv_cover_pic);
        iv_cover_pic.setVisibility(View.GONE);
        rl_details = (RelativeLayout) findViewById(R.id.rl_details);
        rl_details.setVisibility(View.GONE);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setPageMargin(0);
        setupViewPager(viewPager);
        viewPager.setVisibility(View.GONE);

        viewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
                    @Override
                    public void transformPage(@NonNull View page, float position) {
                        RelativeLayout relativeLayout = page.findViewById(R.id.relative_layout);
                        float scale = 1.0f;
                        float alpha = 1.0f;
                        if (position > 0) {
                            scale = scale - position * 0.3f;
                            if (scale < 0.8f) scale = 0.8f;
                            alpha = alpha - position * 0.8f;
                            if (alpha < 0.4f) alpha = 0.4f;
                        } else {
                            scale = scale + position * 0.3f;
                            if (scale < 0.8f) scale = 0.8f;
                            alpha = alpha + position * 0.8f;
                            if (alpha < 0.4f) alpha = 0.4f;
                        }
                        if (scale < 0) scale = 0;
                        relativeLayout.setAlpha(alpha);
                        relativeLayout.setScaleY(scale);
                        relativeLayout.setScaleX(scale);
                    }
                });

        add_new_friend = (RelativeLayout) findViewById(R.id.add_new_friend_layout);
        int[] pos = new int[2];
        add_new_friend.getLocationOnScreen(pos);

        tvName = (TextView) findViewById(R.id.tvName);

        hmv_profile = (HexagonMaskView) findViewById(R.id.hmv_profile_pic);
        //runZoomOutAnimation();
        iv_cover_pic.setVisibility(View.VISIBLE);
        TranslateAnimation translate1 = new TranslateAnimation( 0, 0 , -1000, 0 );
        translate1.setDuration(500);
        translate1.setFillAfter( true );
        iv_cover_pic.startAnimation(translate1);
        rl_details.setVisibility(View.VISIBLE);
        TranslateAnimation translate2 = new TranslateAnimation( 0, 0 , 1000, 0 );
        translate2.setDuration(500);
        translate2.setFillAfter( true );
        rl_details.startAnimation(translate2);

        hmv_profile.setX(allFriendsActivity.nx);
        hmv_profile.setY(allFriendsActivity.ny);

        databaseHelper = new DatabaseHelper(FriendDetailsActivity.this);
        //details = databaseHelper.getUser("7259980952");
        phonePreferneces = getSharedPreferences("USER", MODE_PRIVATE);
        userPhone = phonePreferneces.getString("PHONE", "3336669990");
        details = databaseHelper.getUser(userPhone);

        iv_cover_pic.setImageBitmap(BitmapFactory.decodeFile(details.get(0).getCoverImage()));

        if (details.get(0).getImage() != null) {
            File file = new File(details.get(0).getImage());
            if (file.exists()) {
                hmv_profile.setImageBitmap(BitmapFactory.decodeFile(details.get(0).getImage()));
            } else {
                hmv_profile.setImageResource(R.drawable.default_image);
            }
        }

        Typeface font = Typeface.createFromAsset(this.getAssets(), "signature2_font.ttf");
        tvName.setText(details.get(0).getName());
        tvName.setTypeface(font);

        textSurface = (TextSurface) findViewById(R.id.text_surface);
        textSurface.postDelayed(new Runnable() {
            @Override public void run() {
                show();
            }
        }, 1000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
               viewPager.setVisibility(View.VISIBLE);
            }
        },11200);

        hmv_profile.setOnClickListener(this);
        iv_cover_pic.setOnClickListener(this);

        textSurface.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textSurface.setVisibility(View.GONE);
                viewPager.setVisibility(View.VISIBLE);
            }
        });
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics =getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new NameFragment(), "1");
        adapter.addFrag(new DOBFragment(), "2");
        adapter.addFrag(new ContactFragment(), "3");
        adapter.addFrag(new HobbiesFragment(), "4");
        adapter.addFrag(new OthersFragment(), "5");
        viewPager.setAdapter(adapter);
    }

    private void show() {
        textSurface.reset();
        CookieThumperSample.play(textSurface, getAssets(), details.get(0).getName(), this);
    }

    private void runZoomOutAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.zoomout);
        a.reset();
        add_new_friend.clearAnimation();
        add_new_friend.startAnimation(a);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                iv_cover_pic.setVisibility(View.VISIBLE);
                TranslateAnimation translate1 = new TranslateAnimation( 0, 0 , -1000, 0 );
                translate1.setDuration(500);
                translate1.setFillAfter( true );
                iv_cover_pic.startAnimation(translate1);
                rl_details.setVisibility(View.VISIBLE);
                TranslateAnimation translate2 = new TranslateAnimation( 0, 0 , 1000, 0 );
                translate2.setDuration(500);
                translate2.setFillAfter( true );
                rl_details.startAnimation(translate2);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void runZoomInAnimation() {
        Animation a = AnimationUtils.loadAnimation(this, R.anim.zoomin);
        a.reset();
        add_new_friend.clearAnimation();
        add_new_friend.startAnimation(a);
        a.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                allFriendsActivity.hmv_main.setImageDrawable(hmv_profile.getDrawable());
                allFriendsActivity.onQueryTextChange("");
                finish();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void translateUP(View view){
        TranslateAnimation translate = new TranslateAnimation( 0, 0 , 0, -1500 );
        translate.setDuration(500);
        translate.setFillAfter( true );
        view.startAnimation(translate);
        translate.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                allFriendsActivity.hmv_main.setImageDrawable(hmv_profile.getDrawable());
                allFriendsActivity.onQueryTextChange("");
                //finish();
                //runZoomInAnimation();
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void translateDown(View view){
        TranslateAnimation translate = new TranslateAnimation( 0, 0 , 0, 1500 );
        translate.setDuration(500);
        translate.setFillAfter( true );
        view.startAnimation(translate);
    }

    private void imageDialog(){
        chooserDialog = new Dialog(this);
        chooserDialog.setContentView(R.layout.image_chooser_dialog);
        chooserDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        LinearLayout ll_camera = chooserDialog.findViewById(R.id.ll_camera);
        LinearLayout ll_gallery = chooserDialog.findViewById(R.id.ll_gallery);
        TextView tvClose = chooserDialog.findViewById(R.id.tvClose);

        ll_camera.setOnClickListener(this);
        ll_gallery.setOnClickListener(this);
        tvClose.setOnClickListener(this);

        chooserDialog.show();
    }

    public void ImageCropFunction(Uri selectedImageUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(selectedImageUri, "image");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 300);
            cropIntent.putExtra("aspectX", 4);
            cropIntent.putExtra("aspectY", 4);
            cropIntent.putExtra("scaleUpIfNeeded", true);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, 2);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Error decoding image", Toast.LENGTH_SHORT).show();
        }
    }

    private void imageStorage(){
        int permissionCheck = ContextCompat.checkSelfPermission(FriendDetailsActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck == 0) {
            if(selectedImage == hmv_profile){
                profileImageStoragePath();
            } else {
                coverImageStoragePath();
            }
        } else {
            final Dialog dialog2 = new Dialog(FriendDetailsActivity.this);
            dialog2.setContentView(R.layout.permission_dialog);

            TextView dialog_Title = (TextView) dialog2.findViewById(R.id.tvPermissionTitle);
            dialog_Title.setText(R.string.storage_permission);
            TextView dialog_message = (TextView) dialog2.findViewById(R.id.tvPermissionDesc);
            dialog_message.setText("This app needs storage permission to save your friends photos. You can allow permissions manually by clicking on settings below.");
            TextView pCancel = (TextView) dialog2.findViewById(R.id.tvCancel);
            TextView pSettings = (TextView) dialog2.findViewById(R.id.tvSettings);
            TextView pOk = (TextView) dialog2.findViewById(R.id.tvOk);
            pCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.dismiss();
                }
            });
            pSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.dismiss();
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
            pOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog2.dismiss();
                    ActivityCompat.requestPermissions(FriendDetailsActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                    ActivityCompat.requestPermissions(FriendDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
                }
            });
            dialog2.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(selectedImage == hmv_profile){
                        profileImageStoragePath();
                    } else {
                        coverImageStoragePath();
                    }
                }
                break;
            }
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(selectedImage == hmv_profile){
                        profileImageStoragePath();
                    } else {
                        coverImageStoragePath();
                    }
                }
                break;
            }
        }
    }

    private String profileImageStoragePath() {
        Bitmap bitmap = ((BitmapDrawable) hmv_profile.getDrawable()).getBitmap();
        //path to store image
        File path = Environment.getExternalStorageDirectory();
        File dirProfile = new File(path + "/Appograph/Images/ProfilePics/");
        dirProfile.mkdirs();
        File imageFile = new File(dirProfile, details.get(0).getName() + "-" + details.get(0).getPhone() + ".jpg"); //file name
        if (imageFile.exists())
            imageFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            databaseHelper.updateProfilePic(userPhone, imageFile.toString());
            //Toast.makeText(this, ""+imageFile.toString(), Toast.LENGTH_LONG).show();
            return imageFile.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
        return "null";
    }

    private String coverImageStoragePath() {
        Bitmap bitmap = ((BitmapDrawable) iv_cover_pic.getDrawable()).getBitmap();
        //path to store image
        File path = Environment.getExternalStorageDirectory();
        File dirCover = new File(path + "/Appograph/Images/CoverPics/");
        dirCover.mkdirs();
        File imageFile = new File(dirCover, details.get(0).getName() + "-" + details.get(0).getPhone() + ".jpg"); //file name
        if (imageFile.exists())
            imageFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            databaseHelper.updateCoverPic(userPhone, imageFile.toString());
            //Toast.makeText(this, ""+imageFile.toString(), Toast.LENGTH_LONG).show();
            return imageFile.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
        return "null";
    }

    private void handle(int code, Intent result){
        if(code == RESULT_OK){
            if(selectedImage == hmv_profile){
                hmv_profile.setImageURI(Crop.getOutput(result));
            } else {
                iv_cover_pic.setImageURI(Crop.getOutput(result));
            }
            imageStorage();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode == RESULT_OK){
            if(requestCode == Crop.REQUEST_PICK){
                Uri uri = imageReturnedIntent.getData();
                Uri dest = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(uri, dest).asSquare().start(this);
                if(selectedImage == hmv_profile){
                    hmv_profile.setImageURI(Crop.getOutput(imageReturnedIntent));
                } else {
                    iv_cover_pic.setImageURI(Crop.getOutput(imageReturnedIntent));
                }
            } else if(requestCode == Crop.REQUEST_CROP){
                handle(resultCode, imageReturnedIntent);
            }
        }
        switch(requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    selectedImage.setImageBitmap(imageBitmap);
                    imageStorage();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.hmv_profile_pic:
                selectedImage = hmv_profile;
                imageDialog();
                break;
            case R.id.iv_cover_pic:
                selectedImage = iv_cover_pic;
                imageDialog();
                break;
            case R.id.ll_camera:
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePicture.resolveActivity(getPackageManager()) != null) {
                    chooserDialog.dismiss();
                    startActivityForResult(takePicture, 0);
                }
                break;
            case R.id.ll_gallery:
                chooserDialog.dismiss();
                Crop.pickImage(this);
                break;
            case R.id.tvClose:
                chooserDialog.dismiss();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        translateUP(iv_cover_pic);
        translateDown(rl_details);
        super.onBackPressed();
    }

}
