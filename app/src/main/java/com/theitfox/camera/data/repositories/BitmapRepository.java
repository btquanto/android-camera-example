package com.theitfox.camera.data.repositories;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.provider.MediaStore;

import com.theitfox.camera.data.cache.MemCache;
import com.theitfox.camera.utils.FileUtils;
import com.theitfox.camera.utils.MapUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;

/**
 * Created by btquanto on 07/12/2016.
 */

public class BitmapRepository extends Repository {

    private MapUtils mapUtils;

    @Inject
    public BitmapRepository(Context context, MemCache memCache, MapUtils mapUtils) {
        super(context, memCache);
        this.mapUtils = mapUtils;
    }

    public Observable<File> saveJPEGToSdCard(byte[] jpeg, String fileName) {
        return Observable.create(subscriber -> {
            FileOutputStream outStream = null;
            // Write to SD Card
            try {
                File file = FileUtils.createExternalPictureFile(fileName);

                outStream = new FileOutputStream(file);

                final int bufferSize = 262144; // 256KB
                for (int offset = 0; offset < jpeg.length; offset += bufferSize) {
                    int len = jpeg.length - offset;
                    if (len > bufferSize) {
                        len = bufferSize;
                    }
                    outStream.write(jpeg, offset, len);
                }
                outStream.flush();
                subscriber.onNext(file);
            } catch (Exception e) {
                subscriber.onError(e);
            } finally {
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (Exception ignored) {
                    }
                }
            }
        });
    }

    public Observable<Bitmap> getLastPhotoTaken(int inSampleSize) {

        return Observable.create(subscriber -> {
            Bitmap bitmap = null;

            String[] projection = new String[]{
                    MediaStore.Images.ImageColumns._ID,
                    MediaStore.Images.ImageColumns.DATA,
                    MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                    MediaStore.Images.ImageColumns.DATE_TAKEN,
                    MediaStore.Images.ImageColumns.MIME_TYPE
            };

            final Cursor cursor = context.getContentResolver()
                    .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                            null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");

            if (cursor.moveToFirst()) {
                String imageLocation = cursor.getString(1);
                int orientation = 0;
                try {
                    ExifInterface exif = new ExifInterface(imageLocation);
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
                } catch (IOException e) {
                }

                File imageFile = new File(imageLocation);
                if (imageFile.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = inSampleSize;
                    Bitmap srcBitmap = BitmapFactory.decodeFile(imageLocation, options);

                    Matrix matrix = new Matrix();
                    Map<Integer, Integer> rotationMap = mapUtils.create(new Integer[]{
                            ExifInterface.ORIENTATION_ROTATE_90,
                            ExifInterface.ORIENTATION_ROTATE_180,
                            ExifInterface.ORIENTATION_ROTATE_270}, new Integer[]{90, 180, 270});
                    Integer rotation = rotationMap.get(orientation);
                    matrix.postRotate(rotation != null ? rotation : 0);
                    bitmap = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), matrix, false);
                }
            }
            subscriber.onNext(bitmap);
        });
    }
}
