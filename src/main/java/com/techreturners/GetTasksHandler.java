package com.techreturners;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.techreturners.model.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class GetTasksHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

	private static final Logger LOG = LogManager.getLogger(GetTasksHandler.class);
	private Connection connection = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	@Override
	public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
		LOG.info("received the request");

		String userId = request.getPathParameters().get("userId");

		List<Task> tasks = new ArrayList<>();
		/*if(userId.equals("abcd123")){
			Task t1= new Task("abc1234", "Pick up the newspaper", false);
			tasks.add(t1);
		}
		else{
			Task t2= new Task("abc4567", "Enjoy Java", false);
			tasks.add(t2);
		}*/
		try {
			Class.forName("com.mysql.jdbc.Driver");

			connection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s","hwn-db.ceqavay8hucm.eu-west-2.rds.amazonaws.com",
					"hwn" //+ "?allowPublicKeyRetrieval=true&useSSL=false"
					,"root", "qwerty123"));

			/*
			System.getenv("DB_HOST"),
					System.getenv("DB_NAME"),
					System.getenv("DB_USER"),
					System.getenv("DB_PASSWORD")));
			 */

			preparedStatement = connection.prepareStatement("SELECT * FROM tasks_task WHERE volunteer_id = ?");
			preparedStatement.setString(1, userId);
			//preparedStatement.setString(2, anothervariable);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				Task task = new Task(resultSet.getString("id"),
						resultSet.getString("description"),
						resultSet.getBoolean("dbs_required"));

				tasks.add(task);
			}
		}
		catch (Exception e) {
			LOG.error(String.format("Unable to query database for tasks for user %s", userId), e);
		}
		finally {
			closeConnection();
		}

		APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
		response.setStatusCode(200);
		ObjectMapper objectMapper = new ObjectMapper();
		try{
			String responseBody = objectMapper.writeValueAsString(tasks);
			response.setBody(responseBody);
		}
		catch(JsonProcessingException e){
			LOG.error("unable to marshall task array", e);
		}
		return response;
	}
	private void closeConnection() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (connection != null) {
				connection.close();
			}
		}
		catch (Exception e) {
			LOG.error("Unable to close connections to MySQL - {}", e.getMessage());
		}
	}
}
