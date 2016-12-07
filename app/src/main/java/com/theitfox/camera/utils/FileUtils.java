package com.theitfox.camera.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.OpenableColumns;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by btquanto on 05/08/2016.
 */
public class FileUtils {
    private static final String CACHE_DIR = "Iris_Studio";

    /**
     * Read a stream and returns its content as String
     *
     * @param is the is
     * @return The stream's string content
     */
    public static String readStream(InputStream is) {
        final int bufferSize = 1024;
        char[] buffer = new char[bufferSize];
        StringBuilder builder = new StringBuilder();

        try {
            Reader reader = new InputStreamReader(is, "UTF-8");
            for (int size; (size = reader.read(buffer, 0, bufferSize)) > 0; ) {
                builder.append(buffer, 0, size);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return builder.toString();
    }

    /**
     * Read bytes byte [ ].
     *
     * @param context the context
     * @param uri     the uri
     * @return the byte [ ]
     */
    public static byte[] readBytes(Context context, Uri uri) {
        byte[] buffer = new byte[0];
        try {
            ContentResolver contentResolver = context.getContentResolver();
            InputStream stream = contentResolver.openInputStream(uri);
            buffer = new byte[stream.available()];
            for (; stream.read(buffer) != -1; ) {
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * Gets file name.
     *
     * @param context the context
     * @param uri     the uri
     * @return the file name
     */
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }


    /**
     * Create cache file.
     *
     * @param context  the context
     * @param fileName the file name
     * @return the file
     */
    public static File createAppCacheFile(Context context, String fileName) {
        File cacheDir = new File(context.getExternalCacheDir(), CACHE_DIR);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return new File(cacheDir, fileName);
    }


    /**
     * Create external file.
     *
     * @param context  the context
     * @param fileName the file name
     * @return the file
     */
    public static File createAppExternalFile(Context context, String fileName) {
        File cacheDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), CACHE_DIR);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return new File(cacheDir, fileName);
    }


    /**
     * Create external picture file.
     *
     * @param fileName the file name
     * @return the file
     */
    public static File createExternalPictureFile(String fileName) {
        // TODO: check if external storage is available
        File cacheDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), CACHE_DIR);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }
        return new File(cacheDir, fileName);
    }
}