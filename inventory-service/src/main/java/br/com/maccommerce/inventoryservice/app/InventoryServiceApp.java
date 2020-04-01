package br.com.maccommerce.inventoryservice.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = { "br.com.maccommerce.inventoryservice" })
public class InventoryServiceApp {

	public static void main(String[] args) {
		SpringApplication.run(InventoryServiceApp.class, args);
	}

}
