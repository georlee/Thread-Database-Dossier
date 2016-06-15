package com.george.assignment4threaddatabase.ViewPager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.george.assignment4threaddatabase.Database.Person;
import com.george.assignment4threaddatabase.Database.sqlDatabase;
import com.george.assignment4threaddatabase.R;

import java.io.File;
import java.util.List;

/**
 * Created by George on 2016-02-09.
 */

public class ViewPagerAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;


    TextView personName;
    ImageView personPicture;
    TextView personDescription;
    Person currentPerson;
    sqlDatabase db;
    List<Person> list;




    public ViewPagerAdapter(Context context, sqlDatabase db) {
        this.context = context;
        this.db = db;

    }

    @Override
    public int getCount() {

        list = db.getAllPerson();
        return list.size();

    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {



        currentPerson = db.readPerson(position);

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.viewpager_item, container,
                false);

        personPicture = (ImageView) itemView.findViewById(R.id.VP_picture);
        personName = (TextView) itemView.findViewById(R.id.VP_name);
        personDescription = (TextView) itemView.findViewById(R.id.VP_description);


        personName.setText(currentPerson.getName());
        personDescription.setText(currentPerson.getDescription());



        Bitmap bmp = BitmapFactory.decodeFile(currentPerson.getPicture());
        personPicture.setImageBitmap(bmp);


        ((ViewPager) container).addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        ((ViewPager) container).removeView((LinearLayout) object);

    }
}

