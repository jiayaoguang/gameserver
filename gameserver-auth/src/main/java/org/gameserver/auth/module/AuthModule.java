package org.gameserver.auth.module;

import com.jyg.util.redis.JedisCacheClient;
import com.jyg.util.redis.RedisCacheClient;
import org.gameserver.auth.processor.LoginHttpProcessor;
import org.gameserver.auth.processor.TokenReceiveSuccessProtoProcessor;
import org.gameserver.auth.processor.TokenSendHttpProcessor;
import org.gameserver.auth.util.AuthToSMChannelMrg;
import org.gameserver.auth.util.AuthToSMChannelPool;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

/**
 * created by jiayaoguang at 2018年4月10日
 */
public class AuthModule extends AbstractModule{

	@Override
	protected void configure() {
		binder().requireExplicitBindings();
		this.bind(AuthToSMChannelMrg.class).in(Scopes.SINGLETON);
		this.bind(TokenSendHttpProcessor.class).in(Scopes.SINGLETON);
		this.bind(AuthToSMChannelPool.class).in(Scopes.SINGLETON);
		
		this.bind(TokenReceiveSuccessProtoProcessor.class).in(Scopes.SINGLETON);
		this.bind(TokenSendHttpProcessor.class).in(Scopes.SINGLETON);

		this.bind(RedisCacheClient.class).to(JedisCacheClient.class).in(Scopes.SINGLETON);
		this.bind(LoginHttpProcessor.class).in(Scopes.SINGLETON);

		
	}

}

