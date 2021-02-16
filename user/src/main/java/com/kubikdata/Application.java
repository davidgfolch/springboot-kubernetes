package com.kubikdata;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
//@EnableDiscoveryClient
@EnableSwagger2
@EnableAspectJAutoProxy
@OpenAPIDefinition(info =
	@Info(title = "User API", version = "1.0", description = "Documentation User API v1.0")
)
public class Application {

	static {
		System.setProperty("https.protocols", "TLSv1.2");  //fix error communicating with mysql service "TLSv1,TLSv1.1,TLSv1.2"
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
