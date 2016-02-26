package com.yingnanwang.cs211webview;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class WebViewActivity extends AppCompatActivity {

    public static void actionStart(Context context, String url)
    {
        Intent intent=new Intent(context,WebViewActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    private WebView mWebView;
    private ScreenStateReceiver mScreenStateReceiver=new ScreenStateReceiver();
    private BatteryChangeReceiver batteryChangeReceiver=new BatteryChangeReceiver();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        String url;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                url= null;
            } else {
                url= extras.getString("url");
            }
        } else {
            url= (String) savedInstanceState.getSerializable("url");
        }

        mWebView=(WebView)findViewById(R.id.webView);
        WebViewInit();
        energyEfficientSetting();



        mWebView.loadUrl(url);
    }

    private void energyEfficientSetting(){
        ScreenOffBroadcast();
        BatteryLifeMonitor();
    }

    private void ScreenOffBroadcast()
    {
        IntentFilter screenStateFilter = new IntentFilter();
        screenStateFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStateReceiver, screenStateFilter);
    }

    private void BatteryLifeMonitor(){
        registerReceiver(batteryChangeReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    private void WebViewInit()
    {
        mWebView.clearCache(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setFitsSystemWindows(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setDisplayZoomControls(true);
        mWebView.getSettings().setLoadWithOverviewMode(true);
        mWebView.getSettings().setUseWideViewPort(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.setWebViewClient(new mWebViewClient());
    }

    private void callHiddenWebViewMethod(String name){
        if( mWebView != null ){
            try {
                Method method = WebView.class.getMethod(name);
                method.invoke(mWebView);
                Log.d(name+" Works","fine");
            } catch (NoSuchMethodException e) {
                Log.e("No such method: " + name, e.toString());
            } catch (IllegalAccessException e) {
                Log.e("Illegal Access: " + name, e.toString());
            } catch (InvocationTargetException e) {
                Log.e("Invocation Target: " + name, e.toString());
            }
        }
    }

    private void NightModeSwitch(int flag){
        if(flag==0){
            // day
        }else{
            //night
        }
    }

    private boolean isAppInstalled(String packageName) {
        PackageManager pm = getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }











    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.webview_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.night:
                //night mode
                Toast.makeText(WebViewActivity.this, "Night Mode", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.day:
                //day mode
                Toast.makeText(WebViewActivity.this, "Day Mode", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mScreenStateReceiver);
        unregisterReceiver(batteryChangeReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.callHiddenWebViewMethod("onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.callHiddenWebViewMethod("onResume");
    }

    private class mWebViewClient extends WebViewClient{
        @Override
        public void onPageFinished(WebView view, String url) {
            Toast.makeText(WebViewActivity.this, "Open: "+url, Toast.LENGTH_SHORT).show();
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if( url.indexOf("facebook")>0 | url.indexOf("facebook")>0 ){
                if(isAppInstalled("com.facebook.katana")){
                    // open facebook
                    try {
                        PackageManager packageManager = getPackageManager();
                        Intent intentApp=new Intent();
                        intentApp = packageManager.getLaunchIntentForPackage("com.facebook.katana");
                        startActivity(intentApp);
                        callHiddenWebViewMethod("onPause");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if( url.indexOf("youtube")>0 | url.indexOf("Youtube")>0 ){
                if(isAppInstalled("com.google.android.youtube")){
                    // open youtube
                    try {
                        PackageManager packageManager = getPackageManager();
                        Intent intentApp=new Intent();
                        intentApp = packageManager.getLaunchIntentForPackage("com.google.android.youtube");
                        startActivity(intentApp);
                        callHiddenWebViewMethod("onPause");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            return true;
        }
    }

    private class ScreenStateReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            // stop webview
            callHiddenWebViewMethod("onPause");
        }
    }

    public class BatteryChangeReceiver extends BroadcastReceiver {

        int scale = -1;
        int level = -1;

        @Override
        public void onReceive(Context context, Intent intent) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = level / (float)scale;
            android.util.Log.d("Battery: ", ""+batteryPct);
            if(batteryPct<0.15){
                // enter battery save mode (night mode)

            }
        }
    }
}