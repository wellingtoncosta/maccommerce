package br.com.maccommerce.inventoryservice.config;

import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.concurrent.TimeUnit;

public @Configuration class RetrofitTestConfig {

    @Value("${product-service.url}")
    private String productServiceUrl;

    public @Primary @Bean Retrofit provideRetrofit() {
        OkHttpClient client = new OkHttpClient.Builder()
                .callTimeout(500, TimeUnit.MILLISECONDS)
                .build();

        return new Retrofit.Builder()
                .baseUrl(productServiceUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .client(client)
                .build();
    }

}
