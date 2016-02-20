package com.ryg.ffan_learn;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.ryg.accordion.AccordionLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private AccordionLayout al_test;
    private AccordionAdapter accordionAdapter;

    private ArrayList<AccordionItem> items;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        al_test = (AccordionLayout)findViewById(R.id.al_test);
        items = new ArrayList<>();
        accordionAdapter = new AccordionAdapter(this,items);
        al_test.setAdapter(accordionAdapter);


        AccordionItem accordionItem = new AccordionItem();
        AccordionItem accordionItem1 = new AccordionItem();

        accordionItem.icon = BitmapFactory.decodeResource(getResources(),R.drawable.chexiao);
        accordionItem.name = "CheXiao";

        accordionItem1.icon = BitmapFactory.decodeResource(getResources(),R.drawable.chexiao1);
        accordionItem1.name = "CheXiao1";

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        items.add(accordionItem);
        items.add(accordionItem1);
        items.add(accordionItem1);
        items.add(accordionItem1);
        accordionAdapter.refreshItems(items);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
