package com.example.mobv2.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class UriUtils
{
    private final Context context;

    public UriUtils(Context context)
    {
        this.context = context;
    }

    public File getFileFromUri(Uri uri)
    {
        try
        {
            var stream = context.getContentResolver()
                                .openInputStream(uri);
            var bitmap = BitmapFactory.decodeStream(stream);

            stream.close();

            File file = new File(context.getCacheDir(), "photo");
            file.createNewFile();

            var byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, byteArrayOutputStream);
            byte[] bitmapdata = byteArrayOutputStream.toByteArray();

            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(bitmapdata);
            fileOutputStream.flush();
            fileOutputStream.close();

            return file;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}