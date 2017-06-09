package com.example.saucecode.thegoviya;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by buwaneka on 6/8/2017.
 */

public class CustomAdapterFarmerBidders extends ArrayAdapter<BidderInfo> {

    private ArrayList<BidderInfo> dataSet;
    private Context mContext;
    private ViewHolder viewHolder;
    private Person buyerInfo;
    private String buyerID;

    // View lookup cache
    private static class ViewHolder {
        TextView bidderName;
        TextView bidAmount;
        Button contactBidder;
    }

    public CustomAdapterFarmerBidders(ArrayList<BidderInfo> data, Context context) {
        super(context, R.layout.bidders_list, data);
        this.dataSet = data;
        this.mContext=context;

    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        final BidderInfo dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
         // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.bidders_list, parent, false);
            viewHolder.bidAmount = (TextView) convertView.findViewById(R.id.biddingAmount);
            viewHolder.bidderName = (TextView) convertView.findViewById(R.id.bidderName);
            viewHolder.contactBidder = (Button) convertView.findViewById(R.id.contactBuyer);
            viewHolder.contactBidder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:"+dataModel.getMobileNumber()));
                    mContext.startActivity(intent);

                }
            });

            result=convertView;

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        result.startAnimation(animation);
        lastPosition = position;

        buyerID = dataModel.getBuyerID();

        /*GetBuyerInfo info = new GetBuyerInfo();
        info.execute();*/

        viewHolder.bidAmount.setText("Rs "+dataModel.getBidAmount());
        viewHolder.bidderName.setText(dataModel.getfName());

        return convertView;
    }

}
