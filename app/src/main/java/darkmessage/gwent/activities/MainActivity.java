package darkmessage.gwent.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;

import darkmessage.gwent.R;
import darkmessage.gwent.database.FeedReaderDbHelper;
import darkmessage.gwent.model.Card;
import darkmessage.gwent.model.Collection;
import darkmessage.gwent.model.CustomExpListAdapter;
import darkmessage.gwent.model.JsonHandler;

public class MainActivity extends AppCompatActivity {
    private ExpandableListView expListView;
    private ArrayList<String> parents = new ArrayList<>();
    private HashMap<String, ArrayList<Card>> children = new HashMap<>();
    private CustomExpListAdapter customExpListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        expListView = (ExpandableListView) findViewById(R.id.lvExp);
        customExpListAdapter = new CustomExpListAdapter(this, parents, children);

        // setting list adapter
        expListView.setAdapter(customExpListAdapter);

        final Collection collection = new Collection();

        final FeedReaderDbHelper feedReaderDbHelper = new FeedReaderDbHelper(this);
        feedReaderDbHelper.getCardsFromDb(this, collection);

        if(collection.getSize() == 0){
            final JsonHandler jsonHandler = new JsonHandler(this, collection, parents, children, customExpListAdapter);
            jsonHandler.getAllCards();
        } else {
            for(String s:collection.getFactions()){
                parents.add(s);
                children.put(s, collection.getFactionCards(s));
            }
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
        }


        expListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {

            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                children.get(parents.get(groupPosition)).get(childPosition).increaseCount();
                feedReaderDbHelper.updateCount(children.get(parents.get(groupPosition)).get(childPosition).getId(), children.get(parents.get(groupPosition)).get(childPosition).getCount());
                customExpListAdapter.notifyDataSetChanged();

                return true;
            }
        });
    }
}
