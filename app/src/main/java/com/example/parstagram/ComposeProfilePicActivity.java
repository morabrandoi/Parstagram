package com.example.parstagram;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.parstagram.fragments.ProfileFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;

public class ComposeProfilePicActivity extends AppCompatActivity {
    public static final String TAG = "ProfilePicActivity";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 8;
    public static final String KEY_PROFILE_PIC = "profilePicture";
    private String photoFileName = "profilePic.jpg";
    private File photoFile;
    private Bitmap bitmapProfilePic;

    // View Related
    private Button btnCaptureImage;
    private Button btnChooseImage;
    private Button btnDone;
    private ImageView ivProfilePic;
    private TextView tvAddImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose_profile_pic);

        btnCaptureImage = findViewById(R.id.btnCaptureImage);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnDone = findViewById(R.id.btnDone);
        ivProfilePic = findViewById(R.id.ivPostImage);
        tvAddImage = findViewById(R.id.tvAddImage);

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera();
            }
        });
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (photoFile == null || ivProfilePic.getDrawable() == null){
                    Toast.makeText(ComposeProfilePicActivity.this, "There is no image!", Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                updateProfilePic(currentUser, photoFile);
                Intent intent = new Intent();
                setResult(ProfileFragment.PROFILE_PIC_REQUEST_CODE, intent);
                finish();
            }
        });
    }

    private void launchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // wrap File object into a content provider
        // required for API >= 24
        // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
        Uri fileProvider = FileProvider.getUriForFile(this, "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // by this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                bitmapProfilePic = takenImage;
                ivProfilePic.setImageBitmap(takenImage);
                ivProfilePic.setVisibility(View.VISIBLE);
                tvAddImage.setVisibility(View.GONE);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the File for a photo stored on disk given the fileName
    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    private void updateProfilePic(ParseUser currentUser, File photoFile) {

        currentUser.put(KEY_PROFILE_PIC, new ParseFile(photoFile));
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while updating profile pic", e );
                    Toast.makeText(ComposeProfilePicActivity.this, "Error while saving", Toast.LENGTH_SHORT).show();
                    return ;
                }
                Log.i(TAG, "Post save was successful!");
                ivProfilePic.setImageResource(0);
            }
        });
    }
}