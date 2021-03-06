/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.elsibay.vertx;

import io.vertx.core.AsyncResult;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tarek El-Sibay
 */
@RunWith(io.vertx.ext.unit.junit.VertxUnitRunner.class)
public class DefaultCodecTest {
	
	static final Vertx vertx = Vertx.vertx();
		
	Logger logger  = LoggerFactory.getLogger(DefaultCodecTest.class);

	@Test
	public void testDefaultCodec(TestContext context) {
		
		MessageCodec extJsonObjectCodec = new ExtendedJsonObjectCodec();
		vertx.eventBus().registerDefaultCodec(ExtendedJsonObject.class, extJsonObjectCodec);

		vertx.eventBus().consumer("consumer",(Message<ExtendedJsonObject> msg) -> {
			logger.info("received ExtendedJsonObject");
			context.assertEquals(msg.body().getClass().getName(), ExtendedJsonObject.class.getName());
			msg.reply(new ExtendedJsonObject().put("status", "OK")); 
		});
		
		Async replyAsync = context.async();
		ExtendedJsonObject request = new ExtendedJsonObject();
		DeliveryOptions dop = new DeliveryOptions().setSendTimeout(2000);
		vertx.eventBus().send("consumer",request, dop,(AsyncResult<Message<ExtendedJsonObject>> asyncResult ) -> {
			logger.info("received reply");
			context.assertTrue(asyncResult.succeeded(),"reply should be successfull");
			if ( asyncResult.succeeded() ) { 
				ExtendedJsonObject extJsonObj = asyncResult.result().body();
				context.assertNotNull(extJsonObj,"reply message body should be not null");
				context.assertEquals(extJsonObj.getStatus(),"OK");
			}
			replyAsync.complete();
		});

	} 
}
