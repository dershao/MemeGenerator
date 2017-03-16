package com.example.derekshao.memegenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements TopSectionFragment.TopSectionListener {

    //widgets
    private Toolbar toolbar;
    private TextView topMemeText;
    private TextView bottomMemeText;
    private ImageView meme_photo;

    //request codes
    private static int REQUEST_IMAGE_CAPTURE = 1;
    private static int SELECT_IMAGE = 2;


    //debug tag
    private static String TAG = "derekishere";

    //final String path = Environment.DIRECTORY_DCIM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //textviews
        topMemeText = (TextView) findViewById(R.id.topMemeText);
        bottomMemeText = (TextView) findViewById(R.id.bottomMemeText);

        meme_photo = (ImageView) findViewById(R.id.photoImage);

        Typeface myCustomFont = Typeface.createFromAsset(getAssets(), "fonts/impact.ttf");
        topMemeText.setTypeface(myCustomFont);
        bottomMemeText.setTypeface(myCustomFont);

    }

    //inflates menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.toolbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Adding and Handling actions from toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.action_takePhoto:
                dispatchTakePictureIntent();
                return true;
            case R.id.action_restore:
                restoreDefault();
                return true;
            case R.id.action_settings:
                Toast.makeText(MainActivity.this, "Not available currently.", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_selectImage:
                selectImageIntent();
                return true;
            case R.id.action_save:
                saveImage();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //implemented method from top fragment
    @Override
    public void createMeme(String top, String bottom) {
        setMemeText(top, bottom);
    }

    public void saveImage() {
        Log.v(TAG, "save image");

        meme_photo.buildDrawingCache();
        Bitmap memeBitmap = meme_photo.getDrawingCache();


        String sdCard = Environment.getExternalStorageDirectory().toString();
        String filename = String.format("%d.png", System.currentTimeMillis());

        FileOutputStream os = null;

        File file = new File(sdCard, filename);

        try {
            os = new FileOutputStream(file);

            memeBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();

            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

        }
        catch(IOException e) {
            Toast.makeText(MainActivity.this, "Unable to save image." , Toast.LENGTH_SHORT).show();
            Log.v(TAG, e.toString());
        }
    }

    //restores default gnome child image
    public void restoreDefault() {
        Drawable image = ResourcesCompat.getDrawable(getResources(), R.drawable.gnome_child, null);
        meme_photo.setBackground(image);
    }

    //creates take picture intent
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent =  new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    //creates select image intent
    public void selectImageIntent() {
        Intent start_image_selector = new Intent(this, selectImage.class);
        startActivityForResult(start_image_selector, SELECT_IMAGE);
    }

    //result
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(MainActivity.this, "No image was taken.", Toast.LENGTH_SHORT).show();
                return;
            }
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            Drawable image = new BitmapDrawable(getResources(), imageBitmap);
            meme_photo.setBackground(image);
        }
        else if (requestCode == SELECT_IMAGE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            String img_name = extras.getString("image");

            switch(img_name) {
                case "Arthur":
                    Drawable arthur_hand = ResourcesCompat.getDrawable(getResources(), R.drawable.arthur_hand, null);
                    meme_photo.setBackground(arthur_hand);
                    break;
                case "Pepe":
                    Drawable pepe = ResourcesCompat.getDrawable(getResources(), R.drawable.pepe, null);
                    meme_photo.setBackground(pepe);
                    break;
                case "Evil Kermit":
                    Drawable evil_kermit = ResourcesCompat.getDrawable(getResources(), R.drawable.evil_kermit, null);
                    meme_photo.setBackground(evil_kermit);
                    break;
                default:
                    break;
            }
        }
    }

    public void setMemeText(String top, String bottom) {
        topMemeText.setText(top);
        bottomMemeText.setText(bottom);
    }
}
