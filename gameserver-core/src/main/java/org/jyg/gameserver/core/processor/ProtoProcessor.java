package org.jyg.gameserver.core.processor;

import java.lang.reflect.InvocationTargetException;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.net.EventDispatcher;
import org.jyg.gameserver.core.session.Session;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class ProtoProcessor<T extends GeneratedMessageV3> extends AbstractProcessor<T> {

	private final Parser<? extends GeneratedMessageV3> parser;
	private final Class<? extends GeneratedMessageV3> clazz;
	private EventDispatcher eventDispatcher;

	public ProtoProcessor(Class<? extends GeneratedMessageV3> protoClazz) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		GeneratedMessageV3 defaultInstance = (GeneratedMessageV3)protoClazz.getMethod("getDefaultInstance").invoke(null);
		this.parser = defaultInstance.getParserForType();
		this.clazz = protoClazz;
	}

	public ProtoProcessor(GeneratedMessageV3 messageLite) {
		this.parser = messageLite.getParserForType();
		this.clazz = messageLite.getClass();
	}
	
//	public ProtoProcessor(GeneratedMessageV3.Builder<T> messageLite) {
//		this(messageLite.build());
//	}


	@Override
	public EventDispatcher getEventDispatcher() {
		return eventDispatcher;
	}

	@Override
	public void setEventDispatcher(EventDispatcher eventDispatcher) {
		this.eventDispatcher = eventDispatcher;
	}

	public String getProtoClassName() {
		return clazz.getName();
	}
	public Class<? extends GeneratedMessageV3> getProtoClass() {
		return clazz;
	}
	public int getProtoEventId() {
		return getEventDispatcher().getEventIdByProtoClazz(clazz);
	}

	public final Parser<? extends MessageLite> getProtoParser() {
		return parser;
	}
	
	

	public void process(LogicEvent<T> event) {
//		System.out.println("eventid : "+event.getEventId());

		Session session = getEventDispatcher().getSession(event.getChannel());
		if(session == null){
			logger.error("session == null..................................");
			return;
		}
		process(session,event.getData());
	}
	
	public abstract void process(Session session , T msg);

}
