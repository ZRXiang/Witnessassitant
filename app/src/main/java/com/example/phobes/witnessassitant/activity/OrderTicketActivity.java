package com.example.phobes.witnessassitant.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.phobes.witnessassitant.R;
import com.example.phobes.witnessassitant.util.ClientThread;
import com.example.phobes.witnessassitant.util.Configuration;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by phobes on 2016/6/23.
 */
public class OrderTicketActivity extends AppCompatActivity {
    public static final String ARG_QR_CONTENT = "content";

    Toolbar toolbar;
    Button btSave;
    String result;
    private TextView tvOrderId;
    SendTask sendTask = null;
    String host;
    String port;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_ticket);
        result = getIntent().getStringExtra(ARG_QR_CONTENT);
        Log.i("order:", result);
        findViewById();
        addEvent();
        initView();
        Configuration configuration = new Configuration(this);
        SharedPreferences settings = getSharedPreferences(SettingActivity.PREFS_NAME, 0);
        host = settings.getString(SettingActivity.TEST_HOST, null);
        port = settings.getString(SettingActivity.TEST_PORT, null);
        if (host == null) {
            host = configuration.getTestHost();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(SettingActivity.TEST_HOST, host);
            editor.commit();
        }
        if (port == null) {
            port = configuration.getTestPort();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString(SettingActivity.TEST_PORT, port);
            editor.commit();
        }
    }

    private void findViewById() {
        toolbar = (Toolbar) findViewById(R.id.common_toolbar);
        tvOrderId = (TextView) findViewById(R.id.order_id);
        btSave = (Button) findViewById(R.id.ok_button);
    }

    private void addEvent() {
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();

            }
        });
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendTask = new SendTask(host, Integer.valueOf(port), result);
                sendTask.execute((Void) null);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(OrderTicketActivity.this,LoginActivity.class));
        OrderTicketActivity.this.finish();
    }

    private void initView() {
        toolbar.setTitle("扫描内容");
        tvOrderId.setText(result);
    }

    public class SendTask extends AsyncTask<Void, Void, Boolean> {
        private String host;
        private int port;
        private String content;

        public SendTask(String host, int port, String content) {
            this.host = host;
            this.port = port;
            this.content = content;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                sendData(host, port, content);
            } catch (UnknownHostException e)
            {
                handleException(e, "unknown host exception: " + e.toString());
            } catch (IOException e)
            {
                handleException(e, "io exception: " + e.toString());
                return false;
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Snackbar.make(btSave, "发送成功", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                finish();
            } else {
                Snackbar.make(btSave, "发送失败\n请检查试验机端口是否开启", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }

    }

    public void toastText(String message) {
        Snackbar.make(btSave, message, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void handleException(Exception e, String prefix) {
        e.printStackTrace();
        toastText(prefix + e.toString());
    }

    private void sendData(String host, int port, String content) throws Exception ,UnknownHostException{
        Socket socket = null;
        socket = new Socket(host, port);
        PrintWriter os = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
        os.write(content + "\n");
        os.flush();
        os.close();
    }
}
