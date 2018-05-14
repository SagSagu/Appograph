package com.sagsaguz.appograph.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sagsaguz.appograph.R;

import static com.sagsaguz.appograph.FriendDetailsActivity.databaseHelper;
import static com.sagsaguz.appograph.FriendDetailsActivity.details;

public class ContactFragment extends Fragment implements View.OnClickListener{

    private static final int REQUEST_CALL_PHONE = 1 ;
    private ImageView ivPhone, ivAlternatePhone, ivWhatsApp, ivEmail, ivEdit, ivSave;
    private EditText etPhone, etAlternatePhone, etWhatsApp, etEmail;
    private Dialog dialog;
    private String phoneType = "phone";

    public ContactFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.contact_fragment_item, container, false);

        etPhone = view.findViewById(R.id.etPhone);
        etAlternatePhone = view.findViewById(R.id.etAlternatePhone);
        etWhatsApp = view.findViewById(R.id.etWhatsApp);
        etEmail = view.findViewById(R.id.etEmail);

        ivEdit = view.findViewById(R.id.ivEdit);
        ivSave = view.findViewById(R.id.ivSave);
        ivPhone = view.findViewById(R.id.ivPhone);
        ivAlternatePhone = view.findViewById(R.id.ivAlternatePhone);
        ivWhatsApp = view.findViewById(R.id.ivWhatsApp);
        ivEmail = view.findViewById(R.id.ivEmail);

        ivEdit.setOnClickListener(this);
        ivSave.setOnClickListener(this);
        ivPhone.setOnClickListener(this);
        ivAlternatePhone.setOnClickListener(this);
        ivWhatsApp.setOnClickListener(this);
        ivEmail.setOnClickListener(this);

        etPhone.setText(details.get(0).getPhone());
        if(details.get(0).getAlternate_phone().equals("null")) {
            etAlternatePhone.setHint("Alternate phone number");
        } else {
            etAlternatePhone.setText(details.get(0).getAlternate_phone());
        }
        etWhatsApp.setText(details.get(0).getWhatsapp());
        etEmail.setText(details.get(0).getEmail());

        etPhone.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_IN);
        etPhone.setKeyListener(null);
        etAlternatePhone.setTag(etAlternatePhone.getKeyListener());
        etWhatsApp.setTag(etWhatsApp.getKeyListener());
        etEmail.setTag(etEmail.getKeyListener());
        nonEditable();

        return view;
    }

    private void nonEditable(){
        ivSave.setVisibility(View.GONE);
        etAlternatePhone.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_IN);
        etAlternatePhone.setKeyListener(null);
        etWhatsApp.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_IN);
        etWhatsApp.setKeyListener(null);
        etEmail.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_IN);
        etEmail.setKeyListener(null);
        ivEdit.setVisibility(View.VISIBLE);
    }

    private void editable(){
        ivEdit.setVisibility(View.GONE);
        etAlternatePhone.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        etAlternatePhone.setKeyListener((KeyListener) etAlternatePhone.getTag());
        etWhatsApp.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        etWhatsApp.setKeyListener((KeyListener) etWhatsApp.getTag());
        etEmail.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        etEmail.setKeyListener((KeyListener) etEmail.getTag());
        ivSave.setVisibility(View.VISIBLE);
    }

    private void checkTelephonePermission(){
        int permissionCheck = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE);
        if(permissionCheck == 0) {
            checkPhoneType();
        } else {
            dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.permission_dialog);

            TextView tvPermissionTitle = dialog.findViewById(R.id.tvPermissionTitle);
            TextView tvPermissionDesc = dialog.findViewById(R.id.tvPermissionDesc);
            TextView tvCancel = dialog.findViewById(R.id.tvCancel);
            TextView tvSettings = dialog.findViewById(R.id.tvSettings);
            TextView tvOk = dialog.findViewById(R.id.tvOk);

            tvPermissionTitle.setText("TELEPHONE Permission");
            tvPermissionDesc.setText(R.string.telephone_permission);
            tvCancel.setOnClickListener(this);
            tvSettings.setOnClickListener(this);
            tvOk.setOnClickListener(this);

            dialog.show();
        }
    }

    private void checkPhoneType(){
        if(phoneType.equals("phone"))
            callFriend(etPhone.getText().toString());
        else
            callFriend(etAlternatePhone.getText().toString());
    }

    private void callFriend(String phone){
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phone));
        startActivity(callIntent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CALL_PHONE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkPhoneType();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ivEdit:
                editable();
                break;
            case R.id.ivSave:
                if (TextUtils.isEmpty(etAlternatePhone.getText().toString()) ||
                        TextUtils.isEmpty(etWhatsApp.getText().toString()) ||
                        TextUtils.isEmpty(etEmail.getText().toString())){
                    Toast.makeText(getContext(), "Please enter all details", Toast.LENGTH_SHORT).show();
                } else {
                    databaseHelper.updateContact(details.get(0).getPhone(), etAlternatePhone.getText().toString(), etWhatsApp.getText().toString(), etEmail.getText().toString());
                    Toast.makeText(getContext(), "Details are updated", Toast.LENGTH_SHORT).show();
                    nonEditable();
                }
                break;
            case R.id.ivPhone:
                if(!TextUtils.isEmpty(etAlternatePhone.getText().toString())) {
                    phoneType = "phone";
                    checkTelephonePermission();
                }
                break;
            case R.id.ivAlternatePhone:
                if(!TextUtils.isEmpty(etAlternatePhone.getText().toString())){
                    phoneType = "alternate";
                    checkTelephonePermission();
                }
                break;
            case R.id.ivWhatsApp:
                /*Uri uri = Uri.parse("smsto:"+etWhatsApp.getText().toString());
                Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO, uri);
                whatsappIntent.setPackage("com.whatsapp");
                whatsappIntent.putExtra(Intent.EXTRA_TEXT, " ");
                startActivity(Intent.createChooser(whatsappIntent, ""));*/
                PackageManager pm = getContext().getPackageManager();
                Intent appStartIntent = pm.getLaunchIntentForPackage("com.whatsapp");
                if (null != appStartIntent) {
                    //getContext().startActivity(appStartIntent);
                    Uri uri = Uri.parse("smsto:"+etWhatsApp.getText().toString());
                    Intent whatsappIntent = new Intent(Intent.ACTION_SENDTO, uri);
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, " ");
                    startActivity(Intent.createChooser(whatsappIntent, ""));
                } else {
                    //Toast.makeText(getContext(), "Install whatsapp to text your friend", Toast.LENGTH_SHORT).show();
                    Intent goToMarket = new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse("https://play.google.com/store/apps/details?id=com.whatsapp&hl=en"));
                    startActivity(goToMarket);
                }
                break;
            case R.id.ivEmail:
                /*Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("message/rfc822");
                emailIntent.putExtra(Intent.EXTRA_EMAIL  , new String[]{etEmail.getText().toString()});
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }*/
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setType("message/rfc822");
                emailIntent.setData(Uri.parse("mailto:"+etEmail.getText().toString()));
                startActivity(emailIntent);
                break;
            case R.id.tvCancel:
                dialog.dismiss();
                break;
            case R.id.tvSettings:
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getActivity().getPackageName(), null));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.tvOk:
                dialog.dismiss();
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL_PHONE);
                break;
        }
    }
}
