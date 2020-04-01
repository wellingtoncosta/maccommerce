package br.com.maccommerce.inventoryservice.resources.product.retrofit;

import br.com.maccommerce.inventoryservice.domain.entity.Product;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ProductApi {

    @GET("/products/{id}")
    Call<Product> findById(@Path("id") String id);

}
