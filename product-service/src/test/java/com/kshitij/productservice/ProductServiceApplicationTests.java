package com.kshitij.productservice;

import com.kshitij.productservice.dto.ProductResponse;
import com.kshitij.productservice.model.Product;
import com.kshitij.productservice.repository.ProductRepository;
import com.kshitij.productservice.service.ProductService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers//to make  junit understand we ae using testcontainer for  running this test
@AutoConfigureMockMvc
class ProductServiceApplicationTests {
	@Container//to make junit 5 understand that its a mongodb container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");//manually specifying version of mongodb to use,so we provided docker image name  as mongo and version
    //added static declaration as we want to statically access this particular mongodb container
	// and fetch the url of mongodb database uri

	//we also need to set uri of test conatiner in test properties
	@DynamicPropertySource//to add property dynamically at time of running of test
	static void setPropertyRegistry(DynamicPropertyRegistry dynamicPropertyRegistry){
		dynamicPropertyRegistry.add("spring.data.mongodb.uri",mongoDBContainer::getReplicaSetUrl);
	}

	//At time of starting the integration test first test will start mongodb container by downloading mongo 4.4.2 image and the after starting the
	// container it will get replica set url and add it to the spring.data.mongodb.uri property dynamically at the time of creating the test,as we are not using local database for test


	@Autowired//mockMVC provide mocked servlet enviornment where we can call product our controller
	private MockMvc mockMvc;

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private ProductService productService;

	private ProductResponse productResponse;
	@Test
	void shouldCreateProduct() throws Exception {
      mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
			  .contentType(MediaType.APPLICATION_JSON)
			  .content("{\n" +
					  "\t\"name\":\"iphone13\",\n" +
					  "\t\"description\":\"Apple iphone13\",\n" +
					  "\t\"price\":\"1200\"\n" +
					  "}")).andExpect(status().isCreated());

        Assertions.assertEquals(1, productRepository.findAll().size());

	}

	@Test
	void shouldListProduct() throws Exception {

		ProductResponse productResponse1 = ProductResponse.builder()
				.name("iphone13")
				.description("Apple iphone 13")
				.price(BigDecimal.valueOf(1200))
				.build();

		mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\n" +
						"\t\"name\":\"iphone13\",\n" +
						"\t\"description\":\"Apple iphone13\",\n" +
						"\t\"price\":\"1200\"\n" +
						"}"));

		mockMvc.perform(MockMvcRequestBuilders.get("/api/product")).andExpect(status().isOk());
		List<Product> responseList = productRepository.findAll();
		System.out.println(responseList);
		System.out.println(productResponse1);
		Assertions.assertEquals(productService.getAllProducts().size(),Arrays.asList(productResponse1).size());
	}


}
