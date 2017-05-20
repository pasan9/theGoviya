package com.example.saucecode.thegoviya;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


/**
 * Created by anupamchugh on 09/02/16.
 */
public class CustomAdapter extends ArrayAdapter<Products> implements View.OnClickListener{

    private ArrayList<Products> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView type;
        TextView farmerID;
        TextView quantity;
        TextView price;
        TextView moisture;
        TextView sellingMethod;

    }



    public CustomAdapter(ArrayList<Products> data, Context context) {
        super(context, R.layout.row_item, data);
        this.dataSet = data;
        this.mContext=context;

    }


    @Override
    public void onClick(View v) {


        int position=(Integer) v.getTag();
        Object object = getItem(position);
        Products dataModel=(Products) object;




        switch (v.getId())
        {

           /* case R.id.item_info:

                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();

                break;
*/
            case R.id.sellingmethod:

                Toast.makeText(mContext,"You selected "+dataModel.productID,Toast.LENGTH_SHORT);

                break;

        }


    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Products dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

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

            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;


        viewHolder.type.setText(dataModel.type);
        viewHolder.farmerID.setText(dataModel.farmerID);
        viewHolder.quantity.setText(dataModel.qty+"");
        viewHolder.price.setText(dataModel.price+"");
        viewHolder.moisture.setText(dataModel.mois+"");
        viewHolder.sellingMethod.setText(dataModel.sellingMethod);
        viewHolder.sellingMethod.setOnClickListener(this);
        viewHolder.sellingMethod.setTag(position);
        // Return the completed view to render on screen
        return convertView;
    }


}
