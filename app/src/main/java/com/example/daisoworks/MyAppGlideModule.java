package com.example.daisoworks;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

import com.example.daisoworks.GlideFTP.FTPModel;
import com.example.daisoworks.GlideFTP.FTPModelLoaderFactory;

@GlideModule
public class MyAppGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        registry.prepend(FTPModel.class, InputStream.class,
                new FTPModelLoaderFactory(context));
    }
    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}

