package com.techreturners;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.techreturners.model.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class Handler implements RequestHandler<Map<String, Object>, ApiGatewayResponse> {

	private static final Logger LOG = LogManager.getLogger(Handler.class);

	@Override
	public ApiGatewayResponse handleRequest(Map<String, Object> input, Context context) {
		LOG.info("received: {}", input);

		Task t1= new Task("abc1234", "Pick up the newspaper", false);
		Task t2= new Task("abc4567", "Enjoy Java", false);
		List<Task> tasks = new ArrayList<>();
		tasks.add(t1);
		tasks.add(t2);

		return ApiGatewayResponse.builder()
				.setStatusCode(200)
				.setObjectBody(tasks)
				.build();
		
	}
}
