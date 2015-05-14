package escalada.talhakosen.com.escaladaassignment.models;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;


// why parcable ? http://prolificinteractive.com/blog/2014/07/18/why-we-love-parcelable
public class ProductDetail extends Base implements Parcelable {
    private int product_id;
    private String name;
    private double price;
    private String image;
    private String description;

    public ProductDetail(JSONObject productDetail) throws JSONException {
        this.product_id = getJsonInt(productDetail, "product_id");
        this.name = getJsonString(productDetail, "name");
        this.price = getJsonDouble(productDetail, "price");
        this.image = getJsonString(productDetail, "image");
        this.description = getJsonString(productDetail, "description");
    }

    public ProductDetail(Parcel in) {
        this.product_id = in.readInt();
        this.name = in.readString();
        this.price = in.readDouble();
        this.image = in.readString();
        this.description = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(product_id);
        dest.writeString(name);
        dest.writeDouble(price);
        dest.writeString(image);
        dest.writeString(description);

    }

    public static final Creator<ProductDetail> CREATOR = new Creator<ProductDetail>() {
        public ProductDetail createFromParcel(final Parcel in) {
            return new ProductDetail(in);
        }

        public ProductDetail[] newArray(int size) {
            return new ProductDetail[size];
        }
    };

    public String getJsonString(JSONObject object, String key) {
        try {
            return object.getString(key);
        } catch (JSONException e) {
            return "";
        }
    }

    public int getJsonInt(JSONObject object, String key) {
        try {
            return object.getInt(key);
        } catch (JSONException e) {
            return 0;
        }
    }

    public Double getJsonDouble(JSONObject object, String key) {
        try {
            return object.getDouble(key);
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

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFormattedPrice(){
        return String.format("%.2f",price/100);
    }
}
