package com.george.assignment4threaddatabase.ViewPager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.george.assignment4threaddatabase.Database.Person;
import com.george.assignment4threaddatabase.Database.sqlDatabase;
import com.george.assignment4threaddatabase.R;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by George on 2016-02-09.
 */

public class ViewActivity extends Activity {

    ViewPager viewPager;
    PagerAdapter adapter;
    Person currentPerson;
    private List<Person> local_person_list = new LinkedList<Person>();

    sqlDatabase db = new sqlDatabase(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewpager_activity);


        Button button1 = (Button) findViewById(R.id.button_DeleteDossier);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("databser", "" + viewPager.getCurrentItem());
                currentPerson = db.readPerson(viewPager.getCurrentItem());



                deletePicture(currentPerson);
                db.deletePerson(currentPerson);
                viewPager.setAdapter(adapter);



               local_person_list = db.getAllPerson();
                if( local_person_list.size() == 0){
                    finish();
                }

            }

        });
        Button button2 = (Button) findViewById(R.id.button_moreInfo);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                currentPerson = db.readPerson(viewPager.getCurrentItem());

                String[] splitWords = currentPerson.getName().split(" ");
                String firstnameurl = "";

                for(int i=0; i<splitWords.length; i++){
                    firstnameurl = firstnameurl +"+" + splitWords[i];
                }


                String url = "http://www.google.ca/search?q=" + firstnameurl;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });


        viewPager = (ViewPager) findViewById(R.id.pager);
        adapter = new ViewPagerAdapter(ViewActivity.this, db);
        viewPager.setAdapter(adapter);




    }

    private void deletePicture(Person deletePerson){

        Intent i = getIntent();


        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File(sdCard.getAbsolutePath() + "/george998579943DossierPic");

        String filename = deletePerson.getName() + ".jpg";
        File myFile = new File(dir, filename);

        myFile.delete();
        refreshGallery(myFile);

    }


    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }
}