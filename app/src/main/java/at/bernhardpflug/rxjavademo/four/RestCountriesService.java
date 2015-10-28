package at.bernhardpflug.rxjavademo.four;

import java.util.List;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import rx.Observable;

/**
 * Created by Bernhard Pflug on 26.10.15.
 */
public interface RestCountriesService {

    @GET("/rest/v1/name/{name}")
    Observable<List<Country>> getCountries(@Path("name") String name);
}
