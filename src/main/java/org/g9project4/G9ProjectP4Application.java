package org.g9project4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;



@EnableDiscoveryClient
@SpringBootApplication
public class G9ProjectP4Application {

	public static void main(String[] args) {
		SpringApplication.run(G9ProjectP4Application.class, args);
	}

}
