package darkmessage.gwent.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import darkmessage.gwent.R;
import darkmessage.gwent.activities.MainActivity;
import darkmessage.gwent.database.FeedReaderDbHelper;
import darkmessage.gwent.enums.EnumFaction;
import darkmessage.gwent.enums.EnumLane;
import darkmessage.gwent.enums.EnumLoyalty;
import darkmessage.gwent.enums.EnumRarity;
import darkmessage.gwent.enums.EnumType;

/**
 * Created by Darkmessage on 03.11.2016.
 */

public final class JsonHandler {
    private final String urlCards = "https://api.gwentapi.com/v0/cards/";
    private final String urlSpecificCard = "https://api.gwentapi.com/v0/cards/:id";
    private int totalCards = 1000;
    private RequestQueue requestQueue;
    private FeedReaderDbHelper feedReaderDbHelper;

    private Context context;
    private Collection collection;
    private ArrayList<String> parents;
    private HashMap<String, ArrayList<Card>> children;
    private CustomExpListAdapter customExpListAdapter;

    public JsonHandler(Context context, Collection collection, ArrayList<String> parents, HashMap<String, ArrayList<Card>> children, CustomExpListAdapter customExpListAdapter){
        this.context = context;
        this.collection = collection;
        this.parents = parents;
        this.children = children;
        this.customExpListAdapter = customExpListAdapter;
        requestQueue = Volley.newRequestQueue(context);
        feedReaderDbHelper = new FeedReaderDbHelper(context);
        feedReaderDbHelper.deleteTable();
        feedReaderDbHelper.createTable();
    }

    public JsonHandler(){
        this(null, null, null, null , null);
    }

    private void getAllCards(String url) throws RuntimeException {
        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(final JSONObject response) {
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        generateCollection(response);
                    }
                }).start();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError e) {
                throw new RuntimeException(e.getMessage());
            }
        });

        requestQueue.add(arrayRequest);
    }

    public void getAllCards(){
        getAllCards(urlCards);
    }

    private void getSpecificCard(final String cardURL) throws RuntimeException {
        JsonObjectRequest arrayRequest = new JsonObjectRequest(Request.Method.GET, cardURL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(final JSONObject response) {
                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        processCard(response, collection);
                    }
                }).start();
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError e) {
                throw new RuntimeException(e.getMessage());
            }
        });

        requestQueue.add(arrayRequest);
    }

    private void generateCollection(JSONObject response) throws RuntimeException {
        try {
            totalCards = response.getInt("count");
            JSONArray array = response.getJSONArray("results");
            for(int i = 0; i < array.length();i++){
                JSONObject object = array.getJSONObject(i);
                final String cardURL = object.getString("href");

                new Thread(new Runnable(){
                    @Override
                    public void run(){
                        getSpecificCard(cardURL);
                    }
                }).start();
            }
            if(response.has("next")){
                getAllCards(response.getString("next"));
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void processCard(JSONObject response, final Collection collection){
        final String id;
        final EnumFaction faction;
        final EnumType type;
        final EnumRarity rarity;
        final EnumLane lane;
        final EnumLoyalty loyalty;
        final String name;
        final String text;
        final String imagePath;
        final Card card = new Card();

        try{
            card.setId(response.getString("id"));

            JSONObject object = response.getJSONObject("faction");
            card.setFaction(object.getString("name"));

            object = response.getJSONObject("type");
            card.setColor(object.getString("name"));

            object = response.getJSONObject("rarity");
            card.setRarity(object.getString("name"));

            card.setLane(null);
            card.setLoyalty(null);

            card.setName(response.getString("name"));
            if(response.has("text")) {
                card.setDescription(response.getString("text"));
            }
            card.setImagePath("https://api.gwentapi.com/media/" + card.getId() + "_small.png");
        } catch (Exception e){
            throw new RuntimeException(e.getMessage());
        }

        ImageRequest request = new ImageRequest(card.getImagePath(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        card.setImage(bitmap);
                        collection.addCard(card);

                        if(collection.getSize() == totalCards){
                            Thread thread = new Thread(new Runnable(){

                                @Override
                                public void run() {
                                    feedReaderDbHelper.insertCardsIntoDb(context, collection);
                                    collection.clear();
                                    feedReaderDbHelper.getCardsFromDb(context, collection);
                                    for(String s:collection.getFactions()){
                                        parents.add(s);
                                        children.put(s, collection.getFactionCards(s));
                                    }
                                    ((MainActivity) context).findViewById(R.id.loadingPanel).post(new Runnable(){
                                        @Override
                                        public void run() {
                                            customExpListAdapter.notifyDataSetChanged();
                                            ((MainActivity) context).findViewById(R.id.loadingPanel).setVisibility(View.GONE);
                                        }
                                    });
                                }
                            });
                            thread.start();
                        }
                    }
                }, 0, 0, null, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError e) {
                        throw new RuntimeException(e.getMessage());
                    }
                });
        requestQueue.add(request);
    }
}