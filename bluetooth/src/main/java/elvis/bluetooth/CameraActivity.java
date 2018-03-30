package elvis.bluetooth;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.FileNotFoundException;

public class CameraActivity extends AppCompatActivity {
    private Uri imageUri;   //stores the uri of the last image saved to device
    private static final int TAKE_PICTURE = 101;
    private static final int OPEN_GALLERY = 202;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 303;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 404;
    private static final int IMG_SIZE_LIMIT = 1000;    // in bytes

    private ImageView picPreview;
    private Button btn3;
    private Button btn4;
    byte[] imgBuffer = null;
    private String instrument = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        picPreview = (ImageView) findViewById(R.id.preview);
        btn3 = (Button) findViewById(R.id.button3);
        btn3.setEnabled(false);
        btn4 = (Button) findViewById(R.id.button4);
        btn4.setEnabled(false);
    }

    public void takePicture(View view) {
        requestUserPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
    }

    public void takePictureHelper() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        this.imageUri = Utils.createUri(this);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, this.imageUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    /**
     * Source: http://androidbitmaps.blogspot.ca/2015/04/loading-images-in-android-part-iii-pick.html
     */
    public void openGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), OPEN_GALLERY);
    }

    public void upload(View view){
        if(imgBuffer != null){
            new Thread(new Runnable() {
                @Override
                public void run() {

                    BackEnd.addPost(imgBuffer);
                }
            }).start();
            Toast.makeText(getApplicationContext(), "Uploaded!", Toast.LENGTH_SHORT).show();
            btn4.setEnabled(true);
        }
        else{
            Toast.makeText(this, "Please upload a picture", Toast.LENGTH_SHORT).show();
        }
    }

    public void search(View view){
        StartPage.insUpdated = false;
        new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println(Current.getCurUserEmail());
                    instrument = BackEnd.getInstrument(Current.getCurUserEmail());
                }
            }).start();
        while(!StartPage.insUpdated){}
        if(StartPage.instrumrnt == null){
            Toast.makeText(this, "Loading", Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, "It is a " + StartPage.instrumrnt + " !", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, StartPage.class);
            startActivity(intent);
        }
    }
    /**
     * Handling result from startActivityForResult
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK) {
            setPicturePreview();
            btn3.setEnabled(true);
        } else if (requestCode == OPEN_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri galleryImageUri = data.getData();
            this.imageUri = galleryImageUri;
            Glide.with(this)
                    .load(galleryImageUri)
                    .centerCrop()
                    .into(picPreview);
            btn3.setEnabled(true);
        } else if (resultCode == Activity.RESULT_CANCELED) {
            this.imageUri = Uri.parse(Utils.DEFAULT_PICTURE_PATH);
        }
        Bitmap photoBitmap;
        try {
            photoBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
            imgBuffer = Utils.imgToByteArray(photoBitmap);  // to be sent to database
            if(imgBuffer.length / IMG_SIZE_LIMIT > 500) {
                Toast.makeText(this, "Image too large, change to another one", Toast.LENGTH_LONG).show();
            }
        } catch (FileNotFoundException e) {
            Toast.makeText(this, "error occurred during image selection", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setPicturePreview() {
        Glide.with(this)
                .load(this.imageUri)
                .centerCrop()
                .into(picPreview);
    }

    /**
     * Helper method to request device permissions
     * If permission is denied, request again
     * If permission requested is camera and is already granted, take the picture
     * If permission requested is external storage and is already granted, check the camera
     * permission
     *
     * @param devicePermission could be either camera permission or external storage permission
     * @param requestCode
     */
    private void requestUserPermission(String devicePermission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this,
                devicePermission)
                == PackageManager.PERMISSION_DENIED) {
            // try requesting the permission again.
            if (Build.VERSION.SDK_INT >= 23)
                requestPermissions(
                        new String[]{devicePermission},
                        requestCode);

        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            takePictureHelper();
        } else if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            requestUserPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Take the picture
                    takePictureHelper();
                } else {
                }
                return;
            }
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Ask for camera permission
                    requestUserPermission(Manifest.permission.CAMERA, MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }
}

