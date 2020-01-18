package com.example.angel.networkingchat;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.angel.networkingchat.utilidades.MulticastPublisher;
import com.example.angel.networkingchat.utilidades.MutableStore;
import com.example.angel.networkingchat.utilidades.Pack;
import com.example.angel.networkingchat.utilidades.MyState;
import com.example.angel.networkingchat.utilidades.UtilFun;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btn;
    TextView txtUsrname;

    public static final String USERNAME = "com.example.angel.networkingchat.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WifiManager wifi = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null){
            WifiManager.MulticastLock lock = wifi.createMulticastLock("HelloAndroid");
            lock.acquire();
        }

        findViews();

        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Code here executes on main thread after user presses button
                onClickLogin(v);
            }
        });

    }

    public void findViews() {
        btn = findViewById(R.id.btn_login);
        txtUsrname = findViewById(R.id.txt_nick);
        txtUsrname.setText("usuario");
    }

    public void onClickLogin(View v) {
        String username = txtUsrname.getText().toString();
        Pack pack = new Pack(username, "Nuevo usuario logueado", MyState.LOG_IN);

        try {
            // it may not be received or catched by the server
            new MulticastPublisher(UtilFun.serialize (pack)).start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        MutableStore.setUsername(username);

        Intent intent = new Intent(this, ChatLobbyActivity.class);
        intent.putExtra(USERNAME, username);
        startActivity(intent);
    }

}
