package com.example.daisoworks.GlideFTP;

import android.content.Context;

import androidx.media3.common.util.Log;

import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

public class FTPModelLoaderFactory implements ModelLoaderFactory<FTPModel, InputStream> {
    public FTPModelLoaderFactory(Context context) {
        Log.d("testest" , "66666");
    }

    @Override
    public ModelLoader<FTPModel, InputStream> build(MultiModelLoaderFactory multiFactory) {

        Log.d("testest" , "6666");
        return new FTPModelLoader();
    }

    @Override
    public void teardown() {
        Log.d("testest" , "666666");
        // Do nothing.
    }
}
