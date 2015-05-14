package escalada.talhakosen.com.escaladaassignment.models;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;


/**
 * Created by talha on 14/05/2015.
 */

// why parcable ? http://prolificinteractive.com/blog/2014/07/18/why-we-love-parcelable
public class Product extends Base implements  Parcelable {
    private int product_id;
    private String name;
    private double price;
    private String image;

    public Product(JSONObject product) throws JSONException {
        this.product_id = getJsonInt(product, "product_id");
        this.name = getJsonString(product, "name");
        this.price = getJsonDouble(product, "price");
        this.image = getJsonString(product, "image");
    }

    public Product(Parcel in) {
        this.name = in.readString();
        this.product_id = in.readInt();
        this.price = in.readDouble();
        this.image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(image);
        dest.writeInt(product_id);
        dest.writeDouble(price);

    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        public Product createFromParcel(final Parcel in) {
            return new Product(in);
        }

        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    public String getJsonString(JSONObject country, String key) {
        try {
            return country.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

    public int getJsonInt(JSONObject country, String key) {
        try {
            return country.getInt(key);
        } catch (JSONException e) {
            return 0;
        }
    }

    public Double getJsonDouble(JSONObject country, String key) {
        try {
            return country.getDouble(key);
        } catch (JSONException e) {
            return 0.0;
        }
    }


    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public String getFormattedPrice(){
        return String.format("%.2f",price/100);
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
