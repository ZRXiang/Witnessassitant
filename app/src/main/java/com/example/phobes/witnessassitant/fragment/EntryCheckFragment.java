package com.example.phobes.witnessassitant.fragment;

import android.Manifest;
import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.EntryCheckData;
import com.example.phobes.witnessassitant.util.Configuration;
import com.example.phobes.witnessassitant.util.DatabaseHelper;
import com.example.phobes.witnessassitant.util.DateUtil;


import java.util.Date;

/**
 * Created by phobes on 2016/6/27.
 */
public class EntryCheckFragment extends Fragment {

    public static final String ARG_ENTRY_ID = "entry_id";
    FloatingActionButton btSave;
    public EditText edObjectNameView;
    public EditText edProductNameView;
    public EditText edBatchIdView;
    public EditText edQuantityView;
    public EditText edStrengthView;
    public EditText edOutputView;
    public EditText edPersonNameView;
    public EditText edEntryDateView;
    public EditText edSampleSpecView;
    Spinner spinnerComment;
    Spinner spinnerWitness;
    TextView tvWitness;
    private View rootView;
    String sComment;
    String sWitness;
    private int entryId;


    private EntryCheckData mEntryCheckData;

    public EntryCheckFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ENTRY_ID)) {
            entryId = getArguments().getInt(ARG_ENTRY_ID);
            Log.i("entryId ", entryId + "");
            Activity activity = this.getActivity();
            DatabaseHelper databaseHelper = new DatabaseHelper(activity, Configuration.DB_NAME, Configuration.DB_VERSION);
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            mEntryCheckData = CommData.dbSqlite.getEntryCheck(entryId);
            db.close();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.entry_check, container, false);
        findViewById();
        initView();
        addEvent();
        return rootView;
    }

    private void findViewById() {
        btSave = (FloatingActionButton) getActivity().findViewById(R.id.save);
        edObjectNameView = (EditText) rootView.findViewById(R.id.object_name_value);
        edProductNameView = (EditText) rootView.findViewById(R.id.product_name);
        edBatchIdView = (EditText) rootView.findViewById(R.id.batch_id);
        edStrengthView = (EditText) rootView.findViewById(R.id.strength);
        edQuantityView = (EditText) rootView.findViewById(R.id.quantity);
        edOutputView = (EditText) rootView.findViewById(R.id.output_date);
        edEntryDateView = (EditText) rootView.findViewById(R.id.entry_date);
        edSampleSpecView = (EditText) rootView.findViewById(R.id.sample_spec);
        edPersonNameView = (EditText) rootView.findViewById(R.id.person_name);
        spinnerComment = (Spinner)rootView.findViewById(R.id.entry_check_comment);
        spinnerWitness = (Spinner)rootView.findViewById(R.id.entry_check_witness);
        tvWitness=(TextView)rootView.findViewById(R.id.tv_witness);
    }

    private void initView() {
        String objectName = CommData.dbSqlite.getObjectName(mEntryCheckData.getObjectId());
        edObjectNameView.setText(objectName);
        edProductNameView.setText(mEntryCheckData.getProductName());
        edQuantityView.setText(String.valueOf(mEntryCheckData.getQuantity()));
        edOutputView.setText(mEntryCheckData.getOutputDate());
        edBatchIdView.setText(mEntryCheckData.getBatchId());
        edSampleSpecView.setText(mEntryCheckData.getSampleSpec());
        edStrengthView.setText(mEntryCheckData.getStrength());
        edEntryDateView.setText(mEntryCheckData.getEntryDate());
        edPersonNameView.setText(CommData.username);
        String[] commentList = {"验收合格", "验收不合格"};
        ArrayAdapter<String> curring_Adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, commentList);
        //绑定 Adapter到控件
        spinnerComment.setAdapter(curring_Adapter);
        String[] witnessList = {"见证", "不见证"};
        ArrayAdapter<String> witness_Adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, witnessList);
        //绑定 Adapter到控件
        spinnerWitness.setAdapter(witness_Adapter);
        if (CommData.orgType.equals("监理试验室")) {
            spinnerWitness.setVisibility(View.GONE);
            tvWitness.setVisibility(View.GONE);
        }
    }

    private void addEvent() {
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptSave(v);
            }
        });
        spinnerComment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                sComment = (String) spinnerComment.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
        spinnerWitness.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                sWitness = (String) spinnerWitness.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });
    }

    private void attemptSave(View v) {
        boolean cancel = false;
        View focusView = null;

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            save(v);
        }
    }

    private void save(View v) {

        if (CommData.orgType.equals("监理试验室")) {
            mEntryCheckData.setSuperCheckDate(DateUtil.DateTimeToString(new Date()));
            mEntryCheckData.setSuperComment(sComment);
            if(sComment.equals("验收合格")){
                mEntryCheckData.setAccepted(1);
            }
            else {
                mEntryCheckData.setAccepted(0);
            }
        } else{
            mEntryCheckData.setWitness(sWitness);
            mEntryCheckData.setLabCheckDate(DateUtil.DateTimeToString(new Date()));
            mEntryCheckData.setLabComment(sComment);
        }
        mEntryCheckData.setEntryId(entryId);
        CommData.dbSqlite.saveEntryCheck(mEntryCheckData);
        Snackbar.make(v, "保存成功", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
