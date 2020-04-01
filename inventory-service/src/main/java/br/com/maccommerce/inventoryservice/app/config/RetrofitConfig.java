package br.com.maccommerce.inventoryservice.app.config;

import br.com.maccommerce.inventoryservice.resources.product.retrofit.ProductApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public @Configuration class RetrofitConfig {

    @Value("${product-service.url}")
    private String productServiceUrl;

    public @Bean Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(productServiceUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public @Bean ProductApi provideProductApi(Retrofit retrofit) {
        return retrofit.create(ProductApi.class);
    }

}
