package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;


public class MovieControllerRA {
	
	private Long existingId, nonExistingId;
	private String movieName;
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String adminToken, clientToken, invalidToken;
	private Map<String,Object> postMovieInstance;
	
	@BeforeEach
	public void setUp() throws Exception {
		baseURI = "http://localhost:8080";
		
		movieName = "Aves";
		
		adminUsername = "maria@gmail.com";
		clientPassword = "123456";
		clientUsername = "alex@gmail.com";
		adminPassword = "123456";
				
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = adminToken + "xpto";
		
		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "My Movie");
		postMovieInstance.put("score", 4.7);
		postMovieInstance.put("count", 3);
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
		
		
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		given() 
			.get("/movies")
		.then()
			.statusCode(200)
			.body("content.title", hasItems("Guerra Mundial Z", "Aves de Rapina: Arlequina e sua Emancipação Fantabulosa"));
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {		
		given() 
		.get("/movies?title={movieName}", movieName)
	.then()
		.statusCode(200)
		.body("content.title", hasItems("Aves de Rapina: Arlequina e sua Emancipação Fantabulosa"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {		
		existingId = 1L;
		given() 
			.get("/movies/{id}", existingId)
		.then()
			.statusCode(200)
			.body("id", is(1))
			.body("title", equalTo("The Witcher"))
			.body("score",is(4.5F))
			.body("count", is(2))
			.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {	
		nonExistingId = 999L;
		given() 
			.get("/movies/{id}", nonExistingId)
		.then()
			.statusCode(404);
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {	
		JSONObject newMovie = new JSONObject(postMovieInstance);
		
		given()			
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(201)
			.body("title", equalTo("My Movie"))
			.body("score", is(6.9F))
			.body("count", is(3))
			.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));		
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		JSONObject newMovie = new JSONObject(postMovieInstance);
		
		given()			
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		JSONObject newMovie = new JSONObject(postMovieInstance);
		
		given()			
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.post("/movies")
		.then()
			.statusCode(401);
	}
}
