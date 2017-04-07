package com.example.phobes.witnessassitant.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.fragment.MixTaskListFragment;
import com.example.phobes.witnessassitant.model.CommData;

/**
 * Created by YLS on 2016/9/25.
 */
public class MixTaskActivity  extends AppCompatActivity {

    Toolbar toolbar;
    private Button btnAddTask;
    private MixTaskListFragment mixTaskListFragment;
    private final String DUTY = "施工队长";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_task);
        init();
        addEvent();
    }

    private void init(){
        toolbar= (Toolbar) findViewById(R.id.common_toolbar);
        btnAddTask= (Button) findViewById(R.id.button_add_task);
        if (!CommData.duty.equals(DUTY)) {
            btnAddTask.setVisibility(View.GONE);
        }
        toolbar.setTitle("拌合任务");
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        mixTaskListFragment=new MixTaskListFragment();
        fragmentTransaction.replace(R.id.mix_task_list_frameLayout,mixTaskListFragment);
        fragmentTransaction.commit();
    }

    private void addEvent(){
        btnAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(MixTaskActivity.this, NewTaskActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("option","newTask");
                    intent.putExtra("bundle",bundle);
                    startActivity(intent);

            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

   /* private boolean checkDuty(String permission) {
        if (CommData.duty.equals(permission)) {
            return true;
        } else {
            Snackbar.make(btnAddTask, "你没有权限执行操作", Snackbar.LENGTH_SHORT).show();
            return false;
        }
    }*/
}
