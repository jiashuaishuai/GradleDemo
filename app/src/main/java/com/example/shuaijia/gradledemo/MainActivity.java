package com.example.shuaijia.gradledemo;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {

    TextView tv_co, tv_mo, tv_vl, tv_url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_co = findViewById(R.id.tv_co);
        tv_mo = findViewById(R.id.tv_mo);
        tv_vl = findViewById(R.id.tv_vl);
        tv_url = findViewById(R.id.tv_url);

        tv_co.setText(Cooperator.coop);
        tv_mo.setText(Model.mo);
        tv_url.setText(BuildConfig.URL);

        try {
            ApplicationInfo applicationInfo = getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String val = applicationInfo.metaData.getString("UMENG_CHANNEL");
            tv_vl.setText(val);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
