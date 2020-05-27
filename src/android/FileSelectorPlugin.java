package org.multiplefileselector;


import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PermissionHelper;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static android.app.Activity.RESULT_OK;


public class FileSelectorPlugin extends CordovaPlugin {

    public static final int REQUEST_IMAGE_CAPTURE = 1;
    public static final int PICK_IMAGES = 2;

    private static final String TAG = "FileSelectorPlugin";

    private ArrayList<ImageModel> imageList;


    private String mCurrentPhotoPath;


    private String[] projection = {MediaStore.MediaColumns.DATA};
    File image;

    private boolean IsFromCamera;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private CallbackContext callbackContext;

    @Override
    public boolean execute(String action, JSONArray args,
                           final CallbackContext callbackContext) {
        this.callbackContext = callbackContext;
        try {
            IsFromCamera = args.getBoolean(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (action.equalsIgnoreCase("select")) {
            if (hasPermisssion()) {
                init();
            } else {
                PermissionHelper.requestPermissions(this, 0, permissions);
            }
            return true;
        }
        return false;
    }


    public void init() {

        imageList = new ArrayList<>();
        if (IsFromCamera) {
            cordova.getThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    takePicture();
                }
            });
        } else {
            getPickImageIntent();
        }
    }

    public void takePicture() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = createImageFile();
        if (photoFile != null) {
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
            this.cordova.startActivityForResult(this, cameraIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    public void getPickImageIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        this.cordova.startActivityForResult(this, intent, PICK_IMAGES);
    }

    public File createImageFile() {
        String dateTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + dateTime + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        try {
            image = File.createTempFile(imageFileName, ".jpg", storageDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                if (mCurrentPhotoPath != null) {
                    addImage(mCurrentPhotoPath, image.length() / 1024f, image.getName());

                    sendResponseToPlugin();
                } else {

                    try {

                        JSONObject jsonErrorObject = new JSONObject();
                        jsonErrorObject.put("ErrorCode", "0");
                        jsonErrorObject.put("ErrorMessage", "File not selected");
                        callbackContext.error(jsonErrorObject);


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            } else if (requestCode == PICK_IMAGES) {
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();
                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        Cursor returnCursor =
                                cordova.getActivity().getContentResolver().query(uri, null, null, null, null);
                        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
                        returnCursor.moveToFirst();
                        float fileSize = returnCursor.getLong(sizeIndex) / 1024f;
                        getImageFilePath(uri, fileSize, returnCursor.getString(nameIndex));
                    }

                    if (mClipData.getItemCount() == 0) {
                        try {

                            JSONObject jsonErrorObject = new JSONObject();

                            jsonErrorObject.put("ErrorCode", "0");
                            jsonErrorObject.put("ErrorMessage", "File not selected");
                            callbackContext.error(jsonErrorObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    } else {

                        sendResponseToPlugin();
                    }


                }


            }
        }
    }

    private void sendResponseToPlugin() {
        try {


            JSONArray imageData = new JSONArray();
            for (int i = 0; i < imageList.size(); i++) {
                imageList.get(i);
                JSONObject jsonErrorObject = new JSONObject();
                jsonErrorObject.put("Base64Data", imageList.get(i).getBase64ImageData());
                jsonErrorObject.put("FileSize", imageList.get(i).getFileSize());
                jsonErrorObject.put("FileName", imageList.get(i).getFileName());
                imageData.put(jsonErrorObject);
            }
            Log.e("ArraySize", ": " + imageData.length());
            callbackContext.success(imageData);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    // Get image file path
    public void getImageFilePath(Uri uri, float fileSize, String fileName) {

        Cursor cursor = cordova.getActivity().getContentResolver().query(uri, projection, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String absolutePathOfImage = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));

                Log.e("absolutePathOfImage", ":" + absolutePathOfImage);
                if (absolutePathOfImage != null) {
                    checkImage(absolutePathOfImage, fileSize, fileName);
                } else {
                    checkImage(String.valueOf(uri), fileSize, fileName);
                }
            }
        }
    }

    // add image in selectedImageList and imageList
    public void checkImage(String filePath, float fileSize, String fileName) {
        addImage(filePath, fileSize, fileName);
    }

    // add image in selectedImageList and imageList
    public void addImage(String filePath, float fileSize, String fileName) {
        ImageModel imageModel = new ImageModel(getBase64FromUri(filePath), fileName, fileSize);
        imageList.add(imageModel);
    }


    private String getBase64FromUri(String FilePathUri) {
        String encImage = null;

        final InputStream imageStream;
        try {
            imageStream = this.cordova.getActivity().getContentResolver().openInputStream(Uri.parse(FilePathUri));
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            selectedImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            encImage = Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("encImage", ":" + encImage);
        } catch (Exception e) {
        }
        return encImage;
    }

    public boolean hasPermisssion() {
        for (String p : permissions) {
            if (!PermissionHelper.hasPermission(this, p)) {
                return false;
            }
        }
        return true;
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions,
                                          int[] grantResults) {


        if (callbackContext != null) {
            for (int r : grantResults) {
                if (r == PackageManager.PERMISSION_DENIED) {
                    Log.e(TAG, "Permission Denied!");

                    try {
                        JSONObject jsonErrorObject = new JSONObject();
                        jsonErrorObject.put("ErrorCode", "1");
                        jsonErrorObject.put("ErrorMessage", PluginResult.Status.ILLEGAL_ACCESS_EXCEPTION);
                        callbackContext.error(jsonErrorObject);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    return;
                }
            }
            init();
        }
    }
}