package org.jyg.gameserver.core.processor;

import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.Context;

import java.lang.reflect.InvocationTargetException;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public abstract class ProtoProcessor<T extends MessageLite> extends AbstractProcessor<T> {

	private final Parser<? extends MessageLite> parser;
	private final Class<? extends MessageLite> clazz;

	private Context context;

	public ProtoProcessor(Class<? extends MessageLite> protoClazz) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		MessageLite defaultInstance = (MessageLite)protoClazz.getMethod("getDefaultInstance").invoke(null);
		this.parser = defaultInstance.getParserForType();
		this.clazz = protoClazz;
	}

	public ProtoProcessor(MessageLite messageLite) {
		this.parser = messageLite.getParserForType();
		this.clazz = messageLite.getClass();
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
	public int getProtoMsgId() {
		return getContext().getMsgIdByProtoClass(clazz);
	}

	public final Parser<? extends MessageLite> getProtoParser() {
		return parser;
	}


	@Override
	public void process(Session session, LogicEvent<T> event) {
		process(session,event.getData());
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

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public abstract void process(Session session , T msg);

}
