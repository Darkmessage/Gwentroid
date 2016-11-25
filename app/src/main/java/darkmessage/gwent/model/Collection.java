package darkmessage.gwent.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.ListIterator;

import darkmessage.gwent.enums.EnumFaction;

/**
 * Created by Darkmessage on 04.11.2016.
 */

public class Collection {
    private ArrayList<Card> cards = new ArrayList<>();
    private ArrayList<String> factions = new ArrayList<>();

    public void addCard(Card c){
        cards.add(c);
        if(!factions.contains(c.getFaction())){
            factions.add(c.getFaction());
        }
    }

    public ArrayList<Card> getCards(){
        ArrayList<Card> returnList = new ArrayList<>();
        returnList.addAll(cards);
        return returnList;
    }

    public int getSize(){
        return cards.size();
    }

    public ArrayList<Card> getFactionCards(String faction){
        ArrayList<Card> selectedFaction = new ArrayList<>();
        ListIterator<Card> it = cards.listIterator();

        while(it.hasNext()){
            Card c = it.next();
            if(c.getFaction().equals(faction)) selectedFaction.add(c);
        }

        return selectedFaction;
    }

    public ArrayList<String> getFactions(){
        ArrayList<String> sorted = new ArrayList<>();
        boolean added;

        for(String s:factions){
            added = false;

            for(int i = 0; i<sorted.size(); i++){
                if(sorted.get(i).compareTo(s) > 0){
                    sorted.add(i, s);
                    added = true;
                    break;
                }
            }

            if(!added) sorted.add(s);
        }
        return sorted;
    }

    public void clear(){
        cards.clear();
    }
}
