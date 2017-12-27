package com.knms.shop.android.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.knms.shop.android.helper.L;
import com.knms.shop.android.helper.SDCardHelper;
import com.knms.shop.android.util.SPUtils;
import com.knms.shop.android.util.SystemInfo;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RxUpdateApi {
    private static RxUpdateApi instance;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private Gson gson;

    private RxApiService apiService;
    public static RxUpdateApi getInstance() {
        if (instance == null) {
            synchronized (RxRequestApi.class) {
                if (instance == null) {
                    instance = new RxUpdateApi();
                }
            }
        }
        return instance;
    }
    private RxUpdateApi() {
        gson = new GsonBuilder().registerTypeAdapter(boolean.class, new TypeAdapter<Boolean>() {
            @Override
            public void write(JsonWriter out, Boolean value) throws IOException {
                if (value == null) {
                    out.nullValue();
                } else {
                    out.value(value);
                }
            }
            @Override
            public Boolean read(JsonReader in) throws IOException {
                JsonToken peek = in.peek();
                switch (peek) {
                    case BOOLEAN:
                        return in.nextBoolean();
                    case NULL:
                        in.nextNull();
                        return null;
                    case NUMBER:
                        return in.nextInt() != 0;
                    case STRING:
                        return Boolean.parseBoolean(in.nextString());
                    default:
                        throw new IllegalStateException("Expected BOOLEAN or NUMBER but was " + peek);
                }
            }
        }).create();

        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(HttpConstant.SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .cache(new Cache(SDCardHelper.getCacheDirFile(),10 * 1024 * 1024))
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        String knmsid = (String) SPUtils.getFromApp(SPUtils.KeyConstant.knmsid, "");
                        Request.Builder builder = chain.request().newBuilder();
                        Request newRequest = builder.addHeader("Cookie", knmsid)//增加接口唯一标识码
                                .addHeader("k_wf", SystemInfo.getNetworkState() + "")//用户网络类型
                                .addHeader("k_ci",SystemInfo.getVerSerCode())//客户端版本标识
                                .addHeader("k_iv",SystemInfo.getRelease())//系统版本
                                .addHeader("k_ih",android.os.Build.MODEL)//固件型号
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());
                        if (!TextUtils.isEmpty(originalResponse.header("Set-Cookie"))) {
                            String value = originalResponse.header("Set-Cookie");
                            if(!TextUtils.isEmpty(value) && value.contains(";")){
                                String knmsid = value.split(";")[0];
                                if(!TextUtils.isEmpty(knmsid)) SPUtils.saveToApp(SPUtils.KeyConstant.knmsid, knmsid);
                            }
                        }
                        return originalResponse;
                    }
                }).addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
                    @Override
                    public void log(String message) {
                        L.i_http(message);
                    }
                }).setLevel(HttpLoggingInterceptor.Level.BODY));
        okHttpClient = builder.build();
    }
    public RxApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(RxApiService.class);
        }
        return apiService;
    }
    protected Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(HttpConstant.HOST_UPDATE)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }

    private static String bodyToString(RequestBody request) {
        try {
            Buffer buffer = new Buffer();
            request.writeTo(buffer);
            return buffer.readUtf8();
        } catch (IOException e) {
            return "";
        }
    }
}
