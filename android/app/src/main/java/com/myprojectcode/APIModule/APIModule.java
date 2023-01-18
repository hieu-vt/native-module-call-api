package com.myprojectcode.APIModule;

import android.net.TrafficStats;
import android.system.Os;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import io.socket.emitter.Emitter;
import io.socket.engineio.client.transports.WebSocket;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Callback;
import okhttp3.Call;

import java.io.IOException;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import io.socket.client.IO;
import io.socket.client.Socket;

public class APIModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
//    private io.socket.engineio.client.Socket socket;
    private OkHttpClient client = new OkHttpClient();
    private final LinkedBlockingQueue<String> dataBuffer = new LinkedBlockingQueue<>();
    private Socket socket;
    private IO.Options opts;

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
                try {
                    opts = new IO.Options();
                    opts.reconnection = true;
                    opts.reconnectionDelay = 1000;
                    opts.reconnectionAttempts = 5;
                    opts.transports =  new String[] {WebSocket.NAME};
                    sendEvent("SocketData", "Connecting");

                    socket = IO.socket("http://10.0.70.1:3001", opts);
                    TrafficStats.setThreadStatsTag(42);

                    socket.on("NEW_LIST", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            // Handle the event here
                            sendEvent("SocketData", args.toString());
                        }});

                    socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            // Handle the disconnect event here
                            sendEvent("SocketData", "Disconnected");
                        }
                    });


                } catch (URISyntaxException e) {
                    e.printStackTrace();
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