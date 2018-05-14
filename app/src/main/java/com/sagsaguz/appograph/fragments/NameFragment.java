package com.sagsaguz.appograph.fragments;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class NameFragment extends Fragment implements View.OnClickListener{

    private TextView tvTitle;
    private EditText etDesc;
    private ImageView ivEdit, ivSave;

    public NameFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.card_item, container, false);

        tvTitle = view.findViewById(R.id.tvTitle);
        etDesc = view.findViewById(R.id.etDesc);
        ivEdit = view.findViewById(R.id.iv_edit);
        ivSave = view.findViewById(R.id.iv_save);

        ivEdit.setOnClickListener(this);
        ivSave.setOnClickListener(this);

        tvTitle.setText("NAME");
        etDesc.setText(details.get(0).getName());

        etDesc.setTag(etDesc.getKeyListener());
        nonEditable();

        return view;
    }

    private void nonEditable(){
        ivSave.setVisibility(View.GONE);
        etDesc.getBackground().setColorFilter(getResources().getColor(android.R.color.transparent), PorterDuff.Mode.SRC_IN);
        etDesc.setKeyListener(null);
        ivEdit.setVisibility(View.VISIBLE);
    }

    private void editable(){
        ivEdit.setVisibility(View.GONE);
        etDesc.getBackground().setColorFilter(getResources().getColor(android.R.color.white), PorterDuff.Mode.SRC_IN);
        etDesc.setKeyListener((KeyListener) etDesc.getTag());
        ivSave.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_edit:
                editable();
                break;
            case R.id.iv_save:
                if (TextUtils.isEmpty(etDesc.getText().toString())){
                    Toast.makeText(getContext(), "Enter your friend name", Toast.LENGTH_SHORT).show();
                } else {
                    databaseHelper.updateName(details.get(0).getPhone(), etDesc.getText().toString());
                    Toast.makeText(getContext(), "Details are updated", Toast.LENGTH_SHORT).show();
                    nonEditable();
                }
                break;
        }
    }
}
