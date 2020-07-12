package org.jyg.gameserver.auth.processor;

import com.google.inject.Inject;
import org.jyg.gameserver.core.processor.ProtoProcessor;
import org.jyg.gameserver.core.session.Session;
import org.jyg.gameserver.core.util.AsynCallEvent;
import org.jyg.gameserver.core.util.CallBackEvent;
import org.jyg.gameserver.core.util.TokenUtil;
import org.jyg.gameserver.core.util.redis.RedisCacheClient;
import org.jyg.gameserver.proto.MsgLoginReply;
import org.jyg.gameserver.proto.MsgLoginRequest;

/**
 * create by jiayaoguang on 2020/7/5
 */
@Deprecated
public class LoginProtoProcessor  extends ProtoProcessor<MsgLoginRequest> {

    private final RedisCacheClient redisCacheClient;

    @Inject
    public LoginProtoProcessor(RedisCacheClient redisCacheClient) {
        super(MsgLoginRequest.getDefaultInstance());
        this.redisCacheClient = redisCacheClient;
    }

    @Override
    public void process(Session session, MsgLoginRequest msg) {

        long playerUid = 0;

        String key = "token_"+playerUid;


        getContext().getSingleThreadExecutorManager(playerUid).execute(() -> {
            final String token = TokenUtil.getToken();
            String setResult = null;
            try {
                setResult = redisCacheClient.setValueExpire(key, 60 * 1000, token);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (setResult == null) {
                System.out.println(" set value fail ");
                return null;
            } else {
                System.out.println(" set value success " + setResult);
            }
            return token;
        }, new CallBackEvent() {
            @Override
            public void execte() {

                Object data = getData();
                if(data == null){
                    return;
                }

                session.writeMessage(MsgLoginReply.newBuilder().setToken((String) data).build());
            }
        });

    }
}
