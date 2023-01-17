package com.myprojectcode.APIModule;

import android.system.Os;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.Socket;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Callback;
import okhttp3.Call;

import java.io.IOException;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public class APIModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private io.socket.engineio.client.Socket socket;
    private OkHttpClient client = new OkHttpClient();
    private final LinkedBlockingQueue<String> dataBuffer = new LinkedBlockingQueue<>();
    private Socket.Options opts;

    public APIModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    private final Executor executor = Executors.newSingleThreadExecutor();

    private void callAPI(String url) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Request request = new Request.Builder()
                        .url(url)
                        .build();
                // your API call code here
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NonNull Call call, @NonNull IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String responseData = response.body().string();
                            sendEvent("FetchData", responseData);
                        } else {
                        }
                    }
                });
            }
        });
    }

    @Override
    public String getName() {

//        WritableMap params = Arguments.createMap();
//        params.putString("key", "value");
        return "APIModule";
    }

    public void sendEvent(String eventName, @NonNull String params) {
        this.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    public void getData(String url) {
        callAPI(url);
    }
    @ReactMethod
    private void runSocket() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                // handle connect socket here
                try {
                    opts = new Socket.Options();
                    opts.transports = new String[] {WebSocket.NAME};
                    opts.policyPort = 3001;
                    opts.rememberUpgrade = true;
                    opts.timestampRequests = true;
                    socket = new Socket("http://192.168.0.102:3001", opts);

                    socket.listeners("NEW_LIST");

                    socket.on("NEW_LIST", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            String data = (String)args[0];
                            sendEvent("SocketData", data);
                        }
                    });
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }

            }
        });
    }

//    private void sendData() {
//        executor.execute(new Runnable() {
//            @Override
//            public void run() {
//                while (true) {
//                    try {
//                        String data = dataBuffer.take();
//                        WritableMap params = Arguments.createMap();
//                        params.putString("data", data);
//                        sendEvent("FetchData", params);
//                    } catch (InterruptedException e) {
//                        // handle exception
//                    }
//                }
//            }
//        });
//    }
//
//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

//    @ReactMethod
//    public void scheduleSendData() {
//        scheduler.scheduleAtFixedRate(new Runnable() {
//            public void run() {
//                while (true) {
//                    try {
//                        String data = dataBuffer.take();
//                        WritableMap params = Arguments.createMap();
//                        params.putString("data", data);
//                        sendEvent("FetchData", params);
//                    } catch (InterruptedException e) {
//                        // handle exception
//                    }
//                }
//            }
//        }, 0, 1, TimeUnit.SECONDS);
//    }
}