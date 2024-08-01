package com.example.daisoworks.GlideFTP;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.media3.common.util.Log;


import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.signature.ObjectKey;

import java.io.InputStream;

//https://muyangmin.github.io/glide-docs-cn/tut/custom-modelloader.html
public class FTPModelLoader implements ModelLoader<FTPModel, InputStream> {

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(@NonNull FTPModel model, int width, int height, @NonNull Options options) {
        Log.d("testest" , "4444");
        return new LoadData<>(new ObjectKey(model),  new FTPDataFetcher(model));
    }

    @Override
    public boolean handles(@NonNull FTPModel FTPModel) {
        return true;
    }
}
