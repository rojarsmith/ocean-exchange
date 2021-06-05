package io.aext.core.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.aext.core.base.payload.MessageResponse;

public class MessageResponseTest {
	@Test
	public void commonTest() {
		MessageResponse mr1 = new MessageResponse("OK");
		String json1 = mr1.toString();
		assertEquals("{\"code\":0,\"message\":\"OK\",\"data\":null,\"totalPage\":0,\"totalElement\":0}", json1);
	}
}