package com.hhly.ipc_2_messengersample;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @创建者 frank
 * @时间 2017/2/16 15:34
 * @描述：${TODO}
 */

public class MessengerService extends Service {

    public static final String TAG = Constants.TAG;

    private Messenger mServiceMessenger;

    //用于接收从客户端发送过来的消息。
    private static Handler clientHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what){
                case Constants.MSG_FROM_CLIENT:
                    Bundle data = msg.getData();
                    String message = (String) data.get(Constants.MSG_KEY);
                    Log.d(TAG,message);

                    Message toClientMsg = Message.obtain(null,Constants.MSG_FROM_SERVICE);
                    Bundle bundle = new Bundle();
                    bundle.putString(Constants.MSG_KEY,"hello i am a msg form service");
                    toClientMsg.setData(bundle);
                    Messenger mClientMessenger = msg.replyTo;
                    try {
                        mClientMessenger.send(toClientMsg);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            return false;
        }
    });


    @Override
    public void onCreate() {
        super.onCreate();

        mServiceMessenger = new Messenger(clientHandler);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mServiceMessenger.getBinder();
    }
}
