package darkmessage.gwent.model;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import darkmessage.gwent.R;

/**
 * Created by Darkmessage on 03.11.2016.
 */

public class CustomGridViewAdapter extends ArrayAdapter<Card> {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Card> data = new ArrayList<Card>();

    public CustomGridViewAdapter(Context context, int layoutResourceId, ArrayList<Card> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        RecordHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new RecordHolder();
            holder.imageItem = (ImageView) row.findViewById(R.id.card_image);
            holder.textItem = (TextView) row.findViewById(R.id.card_text);
            holder.counterItem = (TextView) row.findViewById(R.id.card_counter);
            row.setTag(holder);
        } else {
            holder = (RecordHolder) row.getTag();
        }

        Card card = data.get(position);

        holder.textItem.setText(card.getName());
        holder.counterItem.setText(( String.valueOf(card.getCount())));
        holder.imageItem.setImageBitmap(card.getImage());

        if(card.getCount() == 0){
            holder.imageItem.setImageBitmap(card.getImageGray());
            holder.textItem.setTextColor(Color.RED);
            holder.counterItem.setTextColor(Color.RED);
        } else if((card.getColor().equals("bronze") && card.getCount() == 3) || (!card.getColor().equals("bronze") && card.getCount() == 1)){
            holder.textItem.setTextColor(Color.rgb(0, 173, 31));
            holder.counterItem.setTextColor(Color.rgb(0, 173, 31));
        } else {
            holder.textItem.setTextColor(Color.BLACK);
            holder.counterItem.setTextColor(Color.BLACK);
        }

        return row;
    }

    static class RecordHolder {
        ImageView imageItem;
        TextView textItem;
        TextView counterItem;
    }
}
