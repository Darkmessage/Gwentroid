package darkmessage.gwent.model;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;

/**
 * Created by Darkmessage on 03.11.2016.
 */

public class Card {
    private String id;
    private String faction;
    private String color;
    private String rarity;
    private String lane;
    private String loyalty;
    private String name;
    private String description;
    private Bitmap image;
    private Bitmap imageGray;
    private String imagePath;
    private int count;
    private boolean complete;

    public String getFaction() {
        return faction;
    }

    public void setFaction(String faction) {
        this.faction = faction;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRarity() {
        return rarity;
    }

    public void setRarity(String rarity) {
        this.rarity = rarity;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public String getLoyalty() {
        return loyalty;
    }

    public void setLoyalty(String loyalty) {
        this.loyalty = loyalty;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
        setImageGray();
    }

    public Card(String id, String faction, String color, String rarity, String lane, String loyalty, String name, String description, Bitmap image, String imagePath, int count) {
        this.id = id;
        this.faction = faction;
        this.color = color;
        this.rarity = rarity;
        this.lane = lane;
        this.loyalty = loyalty;
        this.name = name;
        this.description = description;
        this.image = image;
        this.imagePath = imagePath;
        this.setCount(count);

        if(color != null){
            this.complete = (color.equals("bronze") && count == 3) || (!color.equals("bronze") && count == 1);
        }  else{
            this.complete = false;
        }
    }

    public Card(){
        this(null, null, null, null, null, null, null, null, null, null, 0);
    }

    public int getCount() {
        return count;
    }

    public void increaseCount() {
        if(!complete){
            setCount(count + 1);
            complete = (color.equals("bronze") && count == 3) || (!color.equals("bronze") && count == 1);
        }
    }

    public void decreaseCount() {
        if (count > 0){
            setCount(count - 1);
            complete = (color.equals("bronze") && count == 3) || (!color.equals("bronze") && count == 1);
        }
    }

    public boolean isComplete() {
        return complete;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void convertImageToGrey(){
        int width, height;
        height = image.getHeight();
        width = image.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(image, 0, 0, paint);

        image = bmpGrayscale;
    }

    private void setImageGray(){
        int width, height;
        height = image.getHeight();
        width = image.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(image, 0, 0, paint);

        imageGray = bmpGrayscale;
    }

    public Bitmap getImageGray() {
        return imageGray;
    }

    public void setCount(int count) {
        this.count = count;

        if(color != null){
            complete = (color.equals("bronze") && count == 3) || (!color.equals("bronze") && count == 1);
        }  else{
            complete = false;
        }
    }
}
