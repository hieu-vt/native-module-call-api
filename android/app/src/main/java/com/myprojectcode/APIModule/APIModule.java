package com.myprojectcode.APIModule;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Callback;
import okhttp3.Call;
import java.io.IOException;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class APIModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    private OkHttpClient client = new OkHttpClient();

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
                            WritableMap params = Arguments.createMap();
                            params.putString("data", response.body().string());

                            sendEvent("FetchData", params);
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

    public void sendEvent(String eventName, @NonNull WritableMap params) {
        this.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    public void getData(String url) {


        callAPI(url);
    }


}