package com.hhly.ipc_2_messengersample;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MessengerActivity extends AppCompatActivity {

    public static final String TAG = Constants.TAG;

    private Messenger mServiceMessenger;

    private Messenger mClientMessenger;

    //用来接收从服务端发过来的消息
    private static Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            switch (msg.what) {
                case Constants.MSG_FROM_SERVICE:

                    Bundle data = msg.getData();
                    String msgfromService = data.getString(Constants.MSG_KEY);
                    Log.d(TAG,msgfromService);
                    break;
            }
            return false;
        }
    });


    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            mServiceMessenger = new Messenger(service);

            Message message = Message.obtain(null, Constants.MSG_FROM_CLIENT);
            mClientMessenger = new Messenger(mHandler);//创建客户端的Messenger.
            Bundle bundle = new Bundle();
            bundle.putString(Constants.MSG_KEY, "hello ,i am a message from client");
            message.setData(bundle);
            message.replyTo = mClientMessenger;

            try {
                mServiceMessenger.send(message);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent(this,MessengerService.class);
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
    }
}
