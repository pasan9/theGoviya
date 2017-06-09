package com.example.saucecode.thegoviya;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by buwaneka on 21/05/2017.
 */

public class CustomAdapter extends ArrayAdapter<Products>{

    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;

    private ArrayList<Products> dataSet;
    Context mContext;
    CustomAdapter.ViewHolder viewHolder;

    // View lookup cache
    private static class ViewHolder {
        TextView type;
        TextView farmerID;
        TextView quantity;
        TextView price;
        TextView moisture;
        TextView sellingMethod;
        TextView bidDuration;
        CountDownTimer timer;
    }

    public CustomAdapter(ArrayList<Products> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Products dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
         // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_item, parent, false);
            viewHolder.type = (TextView) convertView.findViewById(R.id.type);
            viewHolder.farmerID = (TextView) convertView.findViewById(R.id.farmerID);
            viewHolder.quantity = (TextView) convertView.findViewById(R.id.quantity);
            viewHolder.price = (TextView) convertView.findViewById(R.id.price);
            viewHolder.moisture = (TextView) convertView.findViewById(R.id.mositure);
            viewHolder.sellingMethod = (TextView) convertView.findViewById(R.id.sellingmethod);
            viewHolder.bidDuration = (TextView) convertView.findViewById(R.id.bidDue);

            result=convertView;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        viewHolder.type.setText("Type of Crop : "+dataModel.type);
        viewHolder.quantity.setText("Quantity : "+dataModel.qty+"");
        viewHolder.price.setText("Price : "+dataModel.price+"");
        viewHolder.moisture.setText("Moisture : "+dataModel.mois+"");
        viewHolder.sellingMethod.setTag(position);

        if(dataModel.sellingMethod.equalsIgnoreCase("Auction")){

            System.out.println(dataModel.bidDuration);
            String[] array = dataModel.bidDuration.split("T");
            String date = array[0];
            String time = array[1];

            SimpleDateFormat dateTimeFor = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Calendar cal = Calendar.getInstance();

            try {
                cal.setTime(dateTimeFor.parse(date+" "+time));
                year = cal.get(Calendar.YEAR);
                month = cal.get(Calendar.MONTH);
                day = cal.get(Calendar.DAY_OF_MONTH);
                hour = cal.get(Calendar.HOUR);
                minute = cal.get(Calendar.MINUTE);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar now = Calendar.getInstance();

            long mil = cal.getTimeInMillis() - now.getTimeInMillis();

            if(mil > 0 ) {
                cal.setTimeInMillis(mil);
                System.out.println(dateTimeFor.format(cal.getTime()));

                new CountDownTimer(mil, 1000) {

                    public void onTick(long millisUntilFinished) {
                        long remain = 0;
                        long dayDiff = 0;
                        long houDiff = 0;
                        long minDiff = 0;
                        long secDiff = 0;

                        remain = millisUntilFinished / 1000;
                        secDiff = remain % 60;
                        remain /= 60;
                        minDiff = remain % 60;
                        remain /= 60;
                        houDiff = remain % 24;
                        remain /= 24;
                        dayDiff = remain;

                        viewHolder.sellingMethod.setText("Auction : "+dayDiff + " days " + houDiff + ":" + minDiff + ":" + secDiff + " left");
                    }

                    public void onFinish() {
                        viewHolder.sellingMethod.setText("Auction closed");
                    }

                }.start();

            } else {
                viewHolder.sellingMethod.setText("Auction closed");
            }
        } else {

            viewHolder.sellingMethod.setText(dataModel.sellingMethod);
        }
        return convertView;
    }


}
