package com.knms.shop.android.net.uploadfile;

import com.google.gson.Gson;
import com.knms.shop.android.helper.L;
import com.knms.shop.android.net.HttpConstant;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by tdx on 2016/9/6.
 */
public class RxUploadApi {
    /**连接超时**/
    public static final int CONNECTION_TIME_OUT = 1*60;//1分钟
    private static RxUploadApi instance;
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private Gson gson;
    private ApiService apiService;

    public static RxUploadApi getInstance() {
        if (instance == null) {
            synchronized (RxUploadApi.class) {
                if (instance == null) {
                    instance = new RxUploadApi();
                }
            }
        }
        return instance;
    }
    private RxUploadApi() {
        gson = new Gson();
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(HttpConstant.SOCKET_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(CONNECTION_TIME_OUT, TimeUnit.SECONDS);
        builder.addInterceptor(new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                L.i_http(message);
            }
        }).setLevel(HttpLoggingInterceptor.Level.BODY));
        okHttpClient = builder.build();
    }
    protected Retrofit getRetrofit() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(HttpConstant.HOST)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
        return retrofit;
    }
    public ApiService getApiService() {
        if (apiService == null) {
            apiService = getRetrofit().create(ApiService.class);
        }
        return apiService;
    }
}
