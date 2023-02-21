package com.myprojectcode.APIModule;
import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class APIModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;
    public APIModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }
    private ConcurrentHashMap<String, Promise> dataChannels = new ConcurrentHashMap<String, Promise>();

    private final Executor executor = Executors.newSingleThreadExecutor();

    @Override
    public String getName() {
        return "APIModule";
    }

    public void sendEvent(String eventName, @NonNull String params) {
        this.reactContext
                .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                .emit(eventName, params);
    }

    @ReactMethod
    private void addCallback(String key, Promise promise) {
        dataChannels.put(key, promise);
    }

    @ReactMethod
    public void removeCallback(String key) {
        dataChannels.remove(key);
    }

    @ReactMethod
    public void publicChannel(String key, Promise promise) {
        addCallback(key, promise);
    }

    @ReactMethod
    public void subscribe(String key, String data) {
        new Thread(new Runnable() {
            public void run() {
                Promise promise = dataChannels.get(key);
                if (promise != null) {
                    WritableMap map = Arguments.createMap();
                    map.putString("data", data);
                    promise.resolve(map);
                }
            }
        }).start();
    }
}