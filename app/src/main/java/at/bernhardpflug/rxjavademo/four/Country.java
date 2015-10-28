package at.bernhardpflug.rxjavademo.four;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Bernhard Pflug on 26.10.15.
 */
public class Country {

    @SerializedName("name")
    private String name;

    @SerializedName("capital")
    private String capital;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    @Override
    public String toString() {
        return name;
    }
}
