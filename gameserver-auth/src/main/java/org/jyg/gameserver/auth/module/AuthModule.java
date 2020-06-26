package org.jyg.gameserver.auth.module;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.jyg.gameserver.core.util.redis.JedisCacheClient;
import org.jyg.gameserver.core.util.redis.RedisCacheClient;
import org.jyg.gameserver.auth.processor.LoginHttpProcessor;
import org.jyg.gameserver.auth.processor.TokenReceiveSuccessProtoProcessor;
import org.jyg.gameserver.auth.processor.TokenSendHttpProcessor;

/**
 * created by jiayaoguang at 2018年4月10日
 */
public class AuthModule extends AbstractModule{

	@Override
	protected void configure() {
		binder().requireExplicitBindings();
		this.bind(TokenSendHttpProcessor.class).in(Scopes.SINGLETON);

		this.bind(TokenReceiveSuccessProtoProcessor.class).in(Scopes.SINGLETON);
		this.bind(TokenSendHttpProcessor.class).in(Scopes.SINGLETON);

		this.bind(RedisCacheClient.class).to(JedisCacheClient.class).in(Scopes.SINGLETON);
		this.bind(LoginHttpProcessor.class).in(Scopes.SINGLETON);

		
	}

}

