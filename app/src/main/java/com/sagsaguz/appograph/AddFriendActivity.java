package com.sagsaguz.appograph;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.sagsaguz.appograph.utils.DatabaseHelper;
import com.sagsaguz.appograph.utils.Friends;
import com.sagsaguz.appograph.utils.HexagonMaskView;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddFriendActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_READ_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 4 ;
    private HexagonMaskView user_pic;
    private EditText user_name, user_dob, user_phone, user_email, user_whats_app, user_hobbies;
    private Button btnAddFriend;
    private View user_view;
    private ProgressBar pb_user;
    Dialog chooserDialog;
    Calendar myCalendar;
    private Dialog dialog;
    private String profile_pic_path, cover_pic_path;

    private DatabaseHelper databaseHelper;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_friend_layout);

        user_pic = (HexagonMaskView) findViewById(R.id.user_pic);
        user_pic.setOnClickListener(this);

        user_name = (EditText) findViewById(R.id.user_name);
        user_dob = (EditText) findViewById(R.id.user_dob);
        user_dob.setKeyListener(null);
        user_phone = (EditText) findViewById(R.id.user_phone);
        user_email = (EditText) findViewById(R.id.user_email);
        user_whats_app = (EditText) findViewById(R.id.user_whats_app);
        user_hobbies = (EditText) findViewById(R.id.user_hobbies);

        btnAddFriend = (Button) findViewById(R.id.btnAddFriend);
        btnAddFriend.setOnClickListener(this);

        user_view = findViewById(R.id.user_view);
        pb_user = (ProgressBar) findViewById(R.id.pb_user);

        myCalendar = Calendar.getInstance();

        //user_dob.setOnClickListener(this);
        user_dob.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_UP) {
                    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear,
                                              int dayOfMonth) {
                            myCalendar.set(Calendar.YEAR, year);
                            myCalendar.set(Calendar.MONTH, monthOfYear);
                            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            updateLabel();
                        }
                    };
                    new DatePickerDialog(AddFriendActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                    return true;
                }
                return false;
            }
        });

    }

    private void validateDetails(){
        user_view.setVisibility(View.VISIBLE);
        pb_user.setVisibility(View.VISIBLE);
        if(TextUtils.isEmpty(user_name.getText().toString())){
            user_name.setError("Enter your friend name");
        } else if(TextUtils.isEmpty(user_dob.getText().toString())){
            user_dob.setError("Enter your friend's DOB");
        } else if(TextUtils.isEmpty(user_phone.getText().toString())){
            user_phone.setError("Enter your friend's active contact number");
        } else if(TextUtils.isEmpty(user_email.getText().toString())){
            user_email.setError("Enter your friend's active contact email");
        } else if(TextUtils.isEmpty(user_whats_app.getText().toString())){
            user_whats_app.setError("Enter your friend's Whats App number");
        } else if(TextUtils.isEmpty(user_hobbies.getText().toString())){
            user_hobbies.setError("Enter atLeast one of your friend's hobby");
        } else {
            imageStorage();

            Friends friends = new Friends(" "+user_name.getText().toString()+" ", user_dob.getText().toString(), user_phone.getText().toString(), " ",
                    user_email.getText().toString(), user_whats_app.getText().toString(), user_hobbies.getText().toString(), " ",
                    profile_pic_path, cover_pic_path);

            databaseHelper = new DatabaseHelper(AddFriendActivity.this);
            String user = databaseHelper.checkingUserAlreadyExistsOrNot(user_phone.getText().toString());
            if(user.equals("new")) {
                databaseHelper.addUser(friends);
                Toast.makeText(this, user_name.getText().toString()+" details are successfully added to your appograph", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddFriendActivity.this, AllFriendsActivity.class));
                finish();
            } else {
                Toast.makeText(this, user+" is already exists with "+user_phone.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        }
        user_view.setVisibility(View.GONE);
        pb_user.setVisibility(View.GONE);
    }

    public void ImageCropFunction(Uri selectedImageUri) {
        try {
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(selectedImageUri, "image");
            cropIntent.putExtra("crop", "true");
            cropIntent.putExtra("outputX", 180);
            cropIntent.putExtra("outputY", 180);
            cropIntent.putExtra("aspectX", 100);
            cropIntent.putExtra("aspectY", 100);
            cropIntent.putExtra("scaleUpIfNeeded", true);
            cropIntent.putExtra("return-data", true);
            startActivityForResult(cropIntent, 2);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Error decoding image", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkStoragePermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(AddFriendActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck == 0) {
            validateDetails();
        } else {
            dialog = new Dialog(AddFriendActivity.this);
            dialog.setContentView(R.layout.permission_dialog);

            TextView tvPermissionTitle = dialog.findViewById(R.id.tvPermissionTitle);
            TextView tvPermissionDesc = dialog.findViewById(R.id.tvPermissionDesc);
            TextView tvCancel = dialog.findViewById(R.id.tvCancel);
            TextView tvSettings = dialog.findViewById(R.id.tvSettings);
            TextView tvOk = dialog.findViewById(R.id.tvOk);

            tvPermissionTitle.setText("STORAGE Permission");
            tvPermissionDesc.setText(R.string.storage_permission);
            tvCancel.setOnClickListener(this);
            tvSettings.setOnClickListener(this);
            tvOk.setOnClickListener(this);

            dialog.show();
        }
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

    private void imageStorage(){
        int permissionCheck = ContextCompat.checkSelfPermission(AddFriendActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck == 0) {
            profileImageStoragePath();
            coverImageStoragePath();
        } else {
            final Dialog dialog2 = new Dialog(AddFriendActivity.this);
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
                    ActivityCompat.requestPermissions(AddFriendActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                    ActivityCompat.requestPermissions(AddFriendActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_EXTERNAL_STORAGE);
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
                    validateDetails();
                }
                break;
            }
            case REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    validateDetails();
                }
                break;
            }
        }
    }

    private String profileImageStoragePath() {
        Bitmap bitmap = ((BitmapDrawable) user_pic.getDrawable()).getBitmap();
        //path to store image
        File path = Environment.getExternalStorageDirectory();
        File dirProfile = new File(path + "/Appograph/Images/ProfilePics/");
        dirProfile.mkdirs();
        File imageFile = new File(dirProfile, user_name.getText().toString() + "-" + user_phone.getText().toString() + ".jpg"); //file name
        if (imageFile.exists())
            imageFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            profile_pic_path = imageFile.toString();
            return imageFile.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
        return "null";
    }

    private String coverImageStoragePath() {
        Bitmap bitmap = ((BitmapDrawable) user_pic.getDrawable()).getBitmap();
        //path to store image
        File path = Environment.getExternalStorageDirectory();
        File dirCover = new File(path + "/Appograph/Images/CoverPics/");
        dirCover.mkdirs();
        File imageFile = new File(dirCover, user_name.getText().toString() + "-" + user_phone.getText().toString() + ".jpg"); //file name
        if (imageFile.exists())
            imageFile.delete();
        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            cover_pic_path = imageFile.toString();
            return imageFile.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "empty", Toast.LENGTH_LONG).show();
        return "null";
    }

    private void handle(int code, Intent result){
        if(code == RESULT_OK){
            user_pic.setImageURI(Crop.getOutput(result));
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        if(resultCode == RESULT_OK){
            if(requestCode == Crop.REQUEST_PICK){
                Uri uri = imageReturnedIntent.getData();
                Uri dest = Uri.fromFile(new File(getCacheDir(), "cropped"));
                Crop.of(uri, dest).asSquare().start(this);
                user_pic.setImageURI(Crop.getOutput(imageReturnedIntent));
            } else if(requestCode == Crop.REQUEST_CROP){
                handle(resultCode, imageReturnedIntent);
            }
        }
        switch(requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    Bundle extras = imageReturnedIntent.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    user_pic.setImageBitmap(imageBitmap);
                }
                break;
            /*case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    ImageCropFunction(selectedImage);
                }
                break;
            case 2:
                if(resultCode == RESULT_OK) {
                    Bundle bundle = imageReturnedIntent.getExtras();
                    Bitmap bitmap = bundle.getParcelable("data");
                    user_pic.setImageBitmap(bitmap);
                    *//*Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    user_pic.setImageBitmap(BitmapFactory.decodeFile(picturePath));*//*
                }
                break;*/
        }
    }

    private void updateLabel() {
        String myFormat = "MMM dd, yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        user_dob.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            /*case R.id.user_dob:
                DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear,
                                          int dayOfMonth) {
                        myCalendar.set(Calendar.YEAR, year);
                        myCalendar.set(Calendar.MONTH, monthOfYear);
                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        updateLabel();
                    }
                };
                new DatePickerDialog(AddFriendActivity.this,date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

                break;*/
            case R.id.user_pic:
                imageDialog();
                break;
            case R.id.btnAddFriend:
                checkStoragePermission();
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
            case R.id.tvCancel:
                dialog.dismiss();
                break;
            case R.id.tvSettings:
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.tvOk:
                dialog.dismiss();
                ActivityCompat.requestPermissions(AddFriendActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_EXTERNAL_STORAGE);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(AddFriendActivity.this, AllFriendsActivity.class));
        finish();
    }
}
