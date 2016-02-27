package com.yingnanwang.cs211webview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText mUrl;
    private Button mBtnGo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUrl=(EditText)findViewById(R.id.edittext_url);
        mBtnGo=(Button)findViewById(R.id.btn_go);


        mBtnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mUrl.getText().toString().matches("")){
                    Toast.makeText(MainActivity.this, "Please input url", Toast.LENGTH_SHORT).show();
                }else{
                    WebViewActivity.actionStart(MainActivity.this, "https://" + mUrl.getText().toString());
                }
            }
        });
    }
}
