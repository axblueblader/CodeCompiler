package com.kvapps.codecompiler;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class MainActivity extends AppCompatActivity {

    private TabAdapter adapter;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private Toolbar toolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        adapter = new TabAdapter(getSupportFragmentManager());
        adapter.addFragment(new CompilingActivity(), "Code");
        adapter.addFragment(new CompilingActivity(), "Output");
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
}