package darkmessage.gwent.model;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import darkmessage.gwent.R;
import darkmessage.gwent.database.FeedReaderDbHelper;

import static android.R.attr.label;

/**
 * Created by Darkmessage on 03.11.2016.
 */

public class CustomExpListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private HashMap<String, ArrayList<Card>> cardsToFaction;
    private ArrayList<String> factions;

    public CustomExpListAdapter(Context context, ArrayList<String> factions, HashMap<String,ArrayList<Card>> cardsToFaction) {
        this.context = context;
        this.factions = factions;
        this.cardsToFaction = cardsToFaction;
    }

    @Override
    public int getGroupCount() {
        return factions.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        //return cardsToFaction.get(factions.get(groupPosition)).size();
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return factions.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return cardsToFaction.get(factions.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.factions, null);
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.faction_text);
        lblListHeader.setText(headerTitle);

        int total = 0;
        int possessed = 0;
        for(Card c:cardsToFaction.get(factions.get(groupPosition))){
            if(c.getColor().equalsIgnoreCase("bronze")){
                total += 3;
            } else{
                total+= 1;
            }

            possessed += c.getCount();
        }

        TextView textView = (TextView) convertView.findViewById(R.id.faction_counter);
        textView.setText(String.valueOf(possessed));
        if(possessed == 0){
            textView.setTextColor(Color.RED);
        } else if(possessed == total){
            textView.setTextColor(Color.rgb(0, 173, 31));
        } else{
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView) convertView.findViewById(R.id.faction_total);
        textView.setText(String.valueOf(total));
        if(possessed == total){
            textView.setTextColor(Color.rgb(0, 173, 31));
        } else{
            textView.setTextColor(Color.BLACK);
        }

        textView = (TextView) convertView.findViewById(R.id.faction_slash);
        textView.setText("/");
        if(possessed == total){
            textView.setTextColor(Color.rgb(0, 173, 31));
        } else{
            textView.setTextColor(Color.BLACK);
        }

        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ViewGroup item = getViewGroupChild(convertView, parent);

        GridView grid = (GridView) item.findViewById(R.id.pictureViewCollection);
        final CustomGridViewAdapter adapter = new CustomGridViewAdapter(parent.getContext(), R.layout.card_layout , cardsToFaction.get(factions.get(groupPosition)));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
                cardsToFaction.get(factions.get(groupPosition)).get(position).increaseCount();

                FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(context);
                feedReaderDbHelper.updateCount(cardsToFaction.get(factions.get(groupPosition)).get(position).getId(), cardsToFaction.get(factions.get(groupPosition)).get(position).getCount());
                adapter.notifyDataSetChanged();
                notifyDataSetChanged();
            }
        });

        grid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                cardsToFaction.get(factions.get(groupPosition)).get(position).decreaseCount();

                FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(context);
                feedReaderDbHelper.updateCount(cardsToFaction.get(factions.get(groupPosition)).get(position).getId(), cardsToFaction.get(factions.get(groupPosition)).get(position).getCount());
                adapter.notifyDataSetChanged();
                notifyDataSetChanged();
                return true;
            }
        });

        grid.setAdapter(adapter);

        // initialize the following variables (i've done it based on your layout
        // note: rowHeightDp is based on my grid_cell.xml, that is the height i've
        //    assigned to the items in the grid.
        final int spacingDp = 0;
        final int colWidthDp = 150;
        final int rowHeightDp = 220;

        // convert the dp values to pixels
        final float COL_WIDTH = context.getResources().getDisplayMetrics().density * colWidthDp;
        final float ROW_HEIGHT = context.getResources().getDisplayMetrics().density * rowHeightDp;
        final float SPACING = context.getResources().getDisplayMetrics().density * spacingDp;

        // calculate the column and row counts based on your display
        final int colCount = (int)Math.floor((parent.getWidth() - (2 * SPACING)) / (COL_WIDTH + SPACING));
        final int rowCount = (int)Math.ceil((cardsToFaction.get(factions.get(groupPosition)).size() + 0d) / colCount);

        // calculate the height for the current grid
        final int GRID_HEIGHT = Math.round(rowCount * (ROW_HEIGHT + SPACING));

        // set the height of the current grid
        grid.getLayoutParams().height = GRID_HEIGHT;

        return item;
    }

    private ViewGroup getViewGroupChild(View convertView, ViewGroup parent)
    {
        // The parent will be our ListView from the ListActivity
        if (convertView instanceof  ViewGroup)
        {
            return (ViewGroup) convertView;
        }
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup item = (ViewGroup) inflater.inflate(R.layout.faction_cards, null);

        return item;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
