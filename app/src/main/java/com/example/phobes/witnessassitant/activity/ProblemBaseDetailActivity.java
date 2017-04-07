package com.example.phobes.witnessassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import com.example.phobes.witnessassitant.R;

/**
 * Created by YLS on 2016-10-25.
 */
public class ProblemBaseDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText checkDate;
    private EditText lab;
    private EditText tender;
    private EditText riskSoure;
    private EditText risks;
    private EditText riskCatName;
    private EditText credit;
    private EditText chargeDepartment;
    private EditText chargePerson;
    private EditText improveDate;
    private EditText improveMethod;
    private EditText deadLine;
    private EditText improvement;
    private EditText superDepartment;
    private EditText supervisor;
    private EditText inspectResult;
    private EditText memo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.problem_base_item_detail);
        findView();
        addEvent();
    }

    private void  findView(){
      checkDate= (EditText) findViewById(R.id.etCheckDate);
      lab=(EditText) findViewById(R.id.etLab);
      tender=(EditText) findViewById(R.id.etTender);
      riskSoure=(EditText) findViewById(R.id.etRiskSoure);
      risks=(EditText) findViewById(R.id.etRisks);
      riskCatName=(EditText) findViewById(R.id.etRiskCatName);
      credit=(EditText) findViewById(R.id.etCredit);
      chargeDepartment=(EditText) findViewById(R.id.etChargeDepartment);
      chargePerson=(EditText) findViewById(R.id.etChargePerson);
      improveDate=(EditText) findViewById(R.id.etImproveDate);
      improveMethod=(EditText) findViewById(R.id.etImproveMethod);
      deadLine=(EditText) findViewById(R.id.etDeadline);
      improvement=(EditText) findViewById(R.id.etImproveMent);
      superDepartment=(EditText) findViewById(R.id.etSuperDepartment);
      supervisor=(EditText) findViewById(R.id.etSupervisor);
      inspectResult=(EditText) findViewById(R.id.etInspectResult);
      memo=(EditText) findViewById(R.id.etMemo);
      toolbar= (Toolbar) findViewById(R.id.common_toolbar);
      toolbar.setTitle("问题详情");
        try {
            Intent intent = getIntent();
            Bundle bundle = intent.getBundleExtra("bundle");
            checkDate.setText(bundle.getString("checkDate"));
            lab.setText(bundle.getString("lab"));
            tender.setText(bundle.getString("tender"));
            riskSoure.setText(bundle.getString("riskSource"));
            risks.setText(bundle.getString("risks"));
            riskCatName.setText(bundle.getString("riskCatName"));
            credit.setText(bundle.getString("credit"));
            chargeDepartment.setText(bundle.getString("chargeDepartment"));
            chargePerson.setText(bundle.getString("chargePerson"));
            improveDate.setText(bundle.getString("improveDate"));
            improveMethod.setText(bundle.getString("improveMethod"));
            deadLine.setText(bundle.getString("deadLine"));
            improvement.setText(bundle.getString("improvement"));
            superDepartment.setText(bundle.getString("superDepartment"));
            supervisor.setText(bundle.getString("supervisor"));
            inspectResult.setText(bundle.getString("inspectResult"));
            memo.setText(bundle.getString("memo"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void addEvent(){
       toolbar.setNavigationOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               onBackPressed();
           }
       });
    }
}
