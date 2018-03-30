package elvis.bluetooth;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

public class Utils {

    public static final String DEFAULT_PICTURE_PATH = "android.resource://me.aflak.bluetoothterminal/drawable/ukulele.jpg";

    public static Uri createUri(Context context) {
        ContentValues values = new ContentValues(1);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        //insert values into a table at EXTERNAL_CONTENT_URI and get the URI of the newly
        //created row
        return context.getContentResolver()
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static byte[] imgToByteArray(Bitmap b) {
        //Bitmap b = BitmapFactory.decodeResource(context.getResources(),R.drawable.test_img);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 10, stream);
        return stream.toByteArray();
    }

    public static Bitmap byteArrayToImage(byte[] imgBytes) {
        Bitmap b = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length); //Convert bytearray to bitmap
        return b;
    }
}

