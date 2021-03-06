package com.example.derekshao.memegenerator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements TopSectionFragment.TopSectionListener {

    //widgets
    private Toolbar toolbar;
    private TextView topMemeText;
    private TextView bottomMemeText;
    private ImageView meme_photo;

    //request codes
    private static int REQUEST_IMAGE_CAPTURE = 1;
    private static int REQUEST_DEFAULT_IMAGE = 2;
    private static int REQUEST_SELECT_IMAGE = 3;

    //font
    private static Typeface impact_font;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //views
        topMemeText = (TextView) findViewById(R.id.topMemeText);
        bottomMemeText = (TextView) findViewById(R.id.bottomMemeText);
        meme_photo = (ImageView) findViewById(R.id.photoImage);

        //set textviews text as impact.tff
        impact_font = Typeface.createFromAsset(getAssets(), "fonts/impact.ttf");
        topMemeText.setTypeface(impact_font);
        bottomMemeText.setTypeface(impact_font);
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
            case R.id.action_DefaultImage:
                defaultImageIntent();
                return true;
            case R.id.action_save:
                saveImage();
                return true;
            case R.id.action_selectImage:
                dispatchSelectImageIntent();
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

    //stores image to gallery
    public void saveImage() {

        meme_photo.buildDrawingCache();
        Bitmap memeBitmap = meme_photo.getDrawingCache();

        String sdCard = Environment.getExternalStorageDirectory().toString();
        String filename = String.format("%d.png", System.currentTimeMillis());

        FileOutputStream os = null;

        File file = new File(sdCard, filename);

        //canvas with meme photo as background
        Canvas canvas = new Canvas(memeBitmap);

        String topText= topMemeText.getText().toString();
        String botText = bottomMemeText.getText().toString();

        //paint for drawing on canvas
        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(impact_font);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(100);


        //top and bottom text positioned as seen as in the preview
        canvas.drawText(topText, canvas.getWidth()/2, 100, textPaint);
        canvas.drawText(botText, canvas.getWidth()/2, 900, textPaint);

        //remember to enable storage permission on phone
        try {
            os = new FileOutputStream(file);

            memeBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();

            MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

            Toast.makeText(MainActivity.this, "Image saved successfully.", Toast.LENGTH_LONG).show();
        }
        catch(IOException e) {
            Toast.makeText(MainActivity.this, "Unable to save image (check app storage permissions)." , Toast.LENGTH_LONG).show();
            Log.e(this.getLocalClassName(), e.toString());
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

    //creates select iamge intent
    public void dispatchSelectImageIntent() {
        Intent selectImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectImageIntent.setType("image/*");
        selectImageIntent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(selectImageIntent, "Select an image from your device"), REQUEST_SELECT_IMAGE);
    }

    //creates select image intent
    public void defaultImageIntent() {
        Intent start_image_selector = new Intent(this, selectImage.class);
        startActivityForResult(start_image_selector, REQUEST_DEFAULT_IMAGE);
    }

    //set background image after selecting image
    public void setBackgroundImage(String img_name) {

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
            case "Donald":
                Drawable donald = ResourcesCompat.getDrawable(getResources(), R.drawable.donald, null);
                meme_photo.setBackground(donald);
            default:
                break;
        }
    }

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
        else if (requestCode == REQUEST_DEFAULT_IMAGE && resultCode == RESULT_OK) {

            Bundle extras = data.getExtras();
            String img_name = extras.getString("image");

            setBackgroundImage(img_name);

        }
        else if (requestCode == REQUEST_SELECT_IMAGE && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            Drawable newImage;

            try {
                InputStream inputStream = getContentResolver().openInputStream(selectedImage);
                newImage = Drawable.createFromStream(inputStream, selectedImage.toString());
                meme_photo.setBackground(newImage);
            }
            catch(FileNotFoundException e) {
                Toast.makeText(MainActivity.this, "Unable to set new image.", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void setMemeText(String top, String bottom) {
        topMemeText.setText(top);
        bottomMemeText.setText(bottom);
    }
}
