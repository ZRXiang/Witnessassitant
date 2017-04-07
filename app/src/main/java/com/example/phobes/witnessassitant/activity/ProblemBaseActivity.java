package com.example.phobes.witnessassitant.activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.ProblemBaseListFragment;
import com.example.phobes.witnessassitant.model.CommData;
import com.example.phobes.witnessassitant.model.RiskCat;
import com.example.phobes.witnessassitant.service.CommService;
import com.example.phobes.witnessassitant.util.ParaseData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by YLS on 2016-10-25.
 */
public class ProblemBaseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Spinner spRiskCatName;
    private String result;
    List<RiskCat> riskCatList=new ArrayList<RiskCat>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_problem_base);
        findView();
        addEvent();
    }
    private void findView(){
        toolbar= (Toolbar) findViewById(R.id.common_toolbar);
        spRiskCatName= (Spinner) findViewById(R.id.spRiskCatName);
        toolbar.setTitle("问题库");

        //showProblemBaseList(1);

        if(new CommService(ProblemBaseActivity.this).isNetConnected()){
            GetRiskCat getRiskCat=new GetRiskCat();
            getRiskCat.execute((Void)null);
        }else{
            Snackbar.make(spRiskCatName,"网络异常！",Snackbar.LENGTH_LONG).show();
        }
    }

    private void addEvent(){
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        spRiskCatName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showProblemBaseList(((RiskCat)spRiskCatName.getSelectedItem()).getRiskCatId());
                //showProblemBaseList(2);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    class GetRiskCat extends AsyncTask<Void,Void,Boolean>{

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                result= CommData.dbWeb.getRiskCat();
                if(result!=null && !result.equals("")){
                    return true;
                }else{
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success){
                riskCatList= ParaseData.toRiskCat(result);
                ArrayAdapter<RiskCat> riskCatArrayAdapter=
                        new ArrayAdapter<RiskCat>(ProblemBaseActivity.this,android.R.layout.simple_list_item_1,riskCatList);
                spRiskCatName.setAdapter(riskCatArrayAdapter);

            }else{
                Snackbar.make(spRiskCatName,"问题分类没有数据！",Snackbar.LENGTH_LONG).show();
            }
        }
    }

    private void showProblemBaseList(int riskCatId){
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        ProblemBaseListFragment  problemBaseListFragment=new ProblemBaseListFragment();
        Bundle arguments=new Bundle();
        arguments.putInt("risk_cat_id",riskCatId);
        problemBaseListFragment.setArguments(arguments);
        fragmentTransaction.replace(R.id.problem_base_list_frameLayout,problemBaseListFragment);
        fragmentTransaction.commit();
    }
}
