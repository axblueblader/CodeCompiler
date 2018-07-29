package com.kvapps.codecompiler;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity implements CompilingActivity.SendMessage {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new CompilingActivity(), "Code");
        adapter.addFragment(new OutputFragment(), "Output");
        //adapter.addFragment(new Tab3Fragment(), "Tab 3");
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


       /* toolBar = findViewById(R.id.toolbar);
        //toolBar.setTitle(null);
        //toolBar.setSubtitle(null);
        setSupportActionBar(toolBar);
        if(getSupportActionBar() !=null) getSupportActionBar().setDisplayShowTitleEnabled(false);
        //getSupportActionBar().setTitle("");*/
    }

    @Override
    public void sendData(String message) {
        //String tag = "android:switcher:" + R.id.viewPager + ":" + 1;
        OutputFragment f = (OutputFragment)adapter.getItem(1);
        f.retrieveAPI(message);
        setCurrentItem(1,true);
    }

    public void setCurrentItem (int item, boolean smoothScroll) {
        viewPager.setCurrentItem(item, smoothScroll);
    }
}