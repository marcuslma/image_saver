// Copyright 2017 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.imagesaver;

import android.os.Environment;
import androidx.annotation.VisibleForTesting;

import java.io.File;
import java.io.IOException;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;


public class ImageSaverPlugin implements MethodChannel.MethodCallHandler {
    private static final String CHANNEL = "mastercarl.com/image_saver";

    private static final int SOURCE_CAMERA = 0;
    private static final int SOURCE_GALLERY = 1;

    private final PluginRegistry.Registrar registrar;
    private final ImagePickerDelegate delegate;

    public static void registerWith(PluginRegistry.Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);

        final File externalFilesDirectory =
                registrar.activity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        final ExifDataCopier exifDataCopier = new ExifDataCopier();
        final ImageResizer imageResizer = new ImageResizer(externalFilesDirectory, exifDataCopier);

        final ImagePickerDelegate delegate =
                new ImagePickerDelegate(registrar.activity(), externalFilesDirectory, imageResizer);
        registrar.addActivityResultListener(delegate);
        registrar.addRequestPermissionsResultListener(delegate);

        final ImageSaverPlugin instance = new ImageSaverPlugin(registrar, delegate);
        channel.setMethodCallHandler(instance);
    }

    @VisibleForTesting
    ImageSaverPlugin(PluginRegistry.Registrar registrar, ImagePickerDelegate delegate) {
        this.registrar = registrar;
        this.delegate = delegate;
    }

    @Override
    public void onMethodCall(MethodCall call, MethodChannel.Result result) {
        if (registrar.activity() == null) {
            result.error("no_activity", "image_picker plugin requires a foreground activity.", null);
            return;
        }
        if (call.method.equals("saveFile")) {
            try {
                delegate.saveImageToGallery(call, result);
            } catch (IOException e) {
                e.printStackTrace();
                throw new IllegalArgumentException(e);
            }
        } else {
            throw new IllegalArgumentException("Unknown method " + call.method);
        }
    }
}
