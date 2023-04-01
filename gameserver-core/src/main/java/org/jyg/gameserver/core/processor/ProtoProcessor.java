package org.jyg.gameserver.core.processor;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import org.jyg.gameserver.core.data.EventData;
import org.jyg.gameserver.core.event.MsgEvent;
import org.jyg.gameserver.core.msg.ByteMsgObj;
import org.jyg.gameserver.core.session.Session;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class ProtoProcessor<T extends MessageLite> extends AbstractProcessor<T> {

	private final Parser<? extends MessageLite> parser;
	private final Class<? extends MessageLite> clazz;

	private final MessageLite defaultInstance;


	public ProtoProcessor() {


		Type superClass = getClass().getGenericSuperclass();
		if (superClass instanceof Class<?>) { // sanity check, should never happen
			throw new IllegalArgumentException("Internal error: TypeReference constructed without actual type information");
		}

		Type _type = ((ParameterizedType) superClass).getActualTypeArguments()[0];


		this.clazz = (Class<? extends MessageLite>) _type;
		this.defaultInstance = getProtoDefaultInstance(this.clazz);
		this.parser = this.defaultInstance.getParserForType();


	}

	public ProtoProcessor(Class<? extends MessageLite> protoClazz)  {
		this(getProtoDefaultInstance(protoClazz));
	}

	private static MessageLite getProtoDefaultInstance(Class<? extends MessageLite> protoClazz){
		try {
			return (MessageLite)protoClazz.getMethod("getDefaultInstance").invoke(null);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public ProtoProcessor(MessageLite messageLite) {
		this.parser = messageLite.getParserForType();
		this.clazz = messageLite.getClass();
		this.defaultInstance = messageLite;
	}
	
//	public ProtoProcessor(MessageLite.Builder<T> messageLite) {
//		this(messageLite.build());
//	}





//	@Override
//	public void setEventDispatcher(EventDispatcher eventDispatcher) {
//		this.eventDispatcher = eventDispatcher;
//	}

	public String getProtoClassName() {
		return clazz.getName();
	}
	public Class<? extends MessageLite> getProtoClass() {
		return clazz;
	}
	
	@Deprecated
	public int getProtoMsgId() {
		return getContext().getMsgIdByProtoClass(clazz);
	}

	public final Parser<? extends MessageLite> getProtoParser() {
		return parser;
	}


	@Override
	public void process(Session session, MsgEvent<T> event) {
		process(session,event.getMsgData());
	}
//	public void process(LogicEvent<T> event) {
////		System.out.println("eventid : "+event.getEventId());
//
//		Session session = getContext().getSession(event.getChannel());
//		if(session == null){
//			logger.error("session == null..................................");
//			return;
//		}
//		process(session,event.getData());
//	}


	public abstract void process(Session session , T msg);

	public MessageLite getProtoDefaultInstance() {
		return defaultInstance;
	}
}
