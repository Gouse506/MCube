package vmc.mcube.in.utils;

import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import vmc.mcube.in.activity.MyApplication;


/**
 * Created by mukesh on 11/3/16.
 */
public class SingleTon {
    public static SingleTon sInstance = null;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private SingleTon() {
        requestQueue = Volley.newRequestQueue(MyApplication.getAplicationContext());
        imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
            private LruCache<String, Bitmap> cache = new LruCache<>(((int) (Runtime.getRuntime().maxMemory() / 1024) / 8));

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });

    }

    public static SingleTon getInstance() {
        if (sInstance == null) {
            sInstance = new SingleTon();
        }
        return sInstance;

    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }
}