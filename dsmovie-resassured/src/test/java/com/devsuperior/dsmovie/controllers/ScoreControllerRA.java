package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ScoreControllerRA {
	
	private Long existingId, nonExistingId;
	private String clientUsername, clientPassword, adminUsername, adminPassword;
	private String adminToken, clientToken, invalidToken;
	private Map<String,Object> postScoreInstance;
	
	@BeforeEach
	public void setUp() throws Exception {
		baseURI = "http://localhost:8080";
		
		adminUsername = "maria@gmail.com";
		clientPassword = "123456";
		clientUsername = "alex@gmail.com";
		adminPassword = "123456";
				
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = adminToken + "xpto";
		
		postScoreInstance = new HashMap<>();
		postScoreInstance.put("movieId", "1");
		postScoreInstance.put("score", 3.9);
		
		
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		postScoreInstance.put("movieId", "999");
		JSONObject newMovie = new JSONObject(postScoreInstance);
		
		given()			
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(404);
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		postScoreInstance.remove("movieId");
		JSONObject newMovie = new JSONObject(postScoreInstance);
		
		given()			
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422)
			.body("errors.message[0]", equalTo("Campo requerido"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {	
		postScoreInstance.put("score", "-1.0");
		JSONObject newMovie = new JSONObject(postScoreInstance);		
		given()			
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newMovie)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
		.when()
			.put("/scores")
		.then()
			.statusCode(422)
			.body("errors.message[0]", equalTo("Valor mínimo 0"));
	}
}
