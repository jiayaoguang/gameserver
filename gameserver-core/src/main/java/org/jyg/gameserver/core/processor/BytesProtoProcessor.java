package org.jyg.gameserver.core.processor;

import org.jyg.gameserver.core.bean.LogicEvent;
import org.jyg.gameserver.core.proto.MsgBytes;
import org.jyg.gameserver.core.session.Session;

/**
 * created by jiayaoguang at 2017年12月16日
 */
public class BytesProtoProcessor extends ProtoProcessor<MsgBytes> {

	public BytesProtoProcessor() {
		super(MsgBytes.getDefaultInstance());
	}

	public  void process(Session session , MsgBytes msg){
		int id = msg.getId();
		byte[] bytes = msg.getByteData().toByteArray();
	}


//	public int getProtoEventId() {
//		return 0;
//	}

}
