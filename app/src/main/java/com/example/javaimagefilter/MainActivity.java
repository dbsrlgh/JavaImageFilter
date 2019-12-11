package com.example.javaimagefilter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.javaimagefilter.Adapter.ViewPagerAdapter;
import com.example.javaimagefilter.Interface.BrushFragmentListener;
import com.example.javaimagefilter.Interface.EditImageFragmentListener;
import com.example.javaimagefilter.Interface.FiltersListFragmentListener;
import com.example.javaimagefilter.Utils.BitmapUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.IOException;
import java.util.List;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MainActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener, BrushFragmentListener {

    public static final String pictureName = "flash.jpg";
    public static final int PERMISSION_PICK_IMAGE = 1000;

    PhotoEditorView mainActivityPhotoEditorView;
    PhotoEditor mainActivityPhotoEditor;

    CoordinatorLayout mainActivityCoordinatorLayout;

    Bitmap mainActivityOriginalBitmap, mainActivityFilteredBitmap, mainActivityFinalBitmap;

    FiltersListFragment mainActivityFiltersListFragment;
    EditImageFragment mainActivityEditImageFragment;

    CardView btn_filters_list, btn_edit, btn_brush, btn_emoticon;

    int mainActivityBrightnessFinal = 0;
    float mainActivitySaturationFinal = 1.0f;
    float mainActivityContrastFinal = 1.0f;

    // Load native image filters lib
    static{
        System.loadLibrary("NativeImageProcessor");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar mainActivityToolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(mainActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Instagram Filter");

        //View
        mainActivityPhotoEditorView = (PhotoEditorView) findViewById(R.id.activity_main_image_preview);
        mainActivityPhotoEditor = new PhotoEditor
                .Builder(this, mainActivityPhotoEditorView)
                .setPinchTextScalable(true)
                .build();

        mainActivityCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main_coordinator);

        btn_filters_list = (CardView) findViewById(R.id.btn_filters_list);
        btn_filters_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltersListFragment mainActivityFiltersListFragment = FiltersListFragment.getInstance();
                mainActivityFiltersListFragment.setFiltersListFragmentListener(MainActivity.this);
                mainActivityFiltersListFragment.show(getSupportFragmentManager(), mainActivityFiltersListFragment.getTag());
            }
        });

        btn_edit = (CardView) findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImageFragment mainActivityEditImageFragment = EditImageFragment.getInstance();
                mainActivityEditImageFragment.setEditImageFragmentListener(MainActivity.this);
                mainActivityEditImageFragment.show(getSupportFragmentManager(), mainActivityEditImageFragment.getTag());
            }
        });

        btn_brush = (CardView) findViewById(R.id.btn_brush);
        btn_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Enable Brush Mode
                mainActivityPhotoEditor.setBrushDrawingMode(true);

                BrushFragment mainActivityBrushFragment = BrushFragment.getInstance();
                mainActivityBrushFragment.setBrushFragmentListener(MainActivity.this);
                mainActivityBrushFragment.show(getSupportFragmentManager(), mainActivityBrushFragment.getTag());

            }
        });

        loadImage();
    }

    private void loadImage() {
        mainActivityOriginalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName, 300, 300);
        mainActivityFilteredBitmap = mainActivityOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mainActivityFinalBitmap = mainActivityOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mainActivityPhotoEditorView.getSource().setImageBitmap(mainActivityOriginalBitmap);
    }

    private void setupViewPager(ViewPager mainActivityViewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        mainActivityFiltersListFragment = new FiltersListFragment();
        mainActivityFiltersListFragment.setFiltersListFragmentListener(this);

        mainActivityEditImageFragment = new EditImageFragment();
        mainActivityEditImageFragment.setEditImageFragmentListener(this);

        adapter.addFragment(mainActivityFiltersListFragment, "FILTERS");
        adapter.addFragment(mainActivityEditImageFragment, "EDIT");
        mainActivityViewPager.setAdapter(adapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        mainActivityBrightnessFinal = brightness;
        Filter myFilterInBrightness = new Filter();
        myFilterInBrightness.addSubFilter(new BrightnessSubFilter(brightness));
        mainActivityPhotoEditorView.getSource().setImageBitmap(myFilterInBrightness.processFilter(mainActivityFinalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        mainActivitySaturationFinal = saturation;
        Filter myFilterInSaturation = new Filter();
        myFilterInSaturation.addSubFilter(new SaturationSubfilter(saturation));
        mainActivityPhotoEditorView.getSource().setImageBitmap(myFilterInSaturation.processFilter(mainActivityFinalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
        mainActivityContrastFinal = contrast;
        Filter myFilterInContrast = new Filter();
        myFilterInContrast.addSubFilter(new ContrastSubFilter(contrast));
        mainActivityPhotoEditorView.getSource().setImageBitmap(myFilterInContrast.processFilter(mainActivityFinalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap = mainActivityFilteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(mainActivityBrightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(mainActivitySaturationFinal));
        myFilter.addSubFilter(new ContrastSubFilter(mainActivityContrastFinal));

        mainActivityFinalBitmap = myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        resetControl();
        mainActivityFilteredBitmap = mainActivityOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mainActivityPhotoEditorView.getSource().setImageBitmap(filter.processFilter(mainActivityFilteredBitmap));
        mainActivityFinalBitmap = mainActivityFilteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void resetControl() {
        if(mainActivityEditImageFragment != null){
            mainActivityEditImageFragment.resetControls();
        }
        mainActivityBrightnessFinal=0;
        mainActivitySaturationFinal=1.0f;
        mainActivityContrastFinal=1.0f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_open) {
            openImageFromGallery();
            return true;
        }

        if(id == R.id.action_save) {
            saveImageToGallery();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveImageToGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            mainActivityPhotoEditor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {
                                    try {

                                        mainActivityPhotoEditorView.getSource().setImageBitmap(saveBitmap);

                                        final String imagePathSelectedAndFiltered = BitmapUtils.insertImage(getContentResolver(),
                                                saveBitmap,
                                                System.currentTimeMillis() + "profile.jpg", null);

                                        if(!TextUtils.isEmpty(imagePathSelectedAndFiltered)){
                                            Snackbar snackbar = Snackbar.make(mainActivityCoordinatorLayout,
                                                    "Image saved to Gallery!",
                                                    Snackbar.LENGTH_LONG)
                                                    .setAction("OPEN", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            openImage(imagePathSelectedAndFiltered);
                                                        }
                                                    });
                                            snackbar.show();
                                        }

                                        else{
                                            Snackbar snackbar = Snackbar.make(mainActivityCoordinatorLayout,
                                                    "Unable to save Image!",
                                                    Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }

                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        }

                        else{
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openImage(String imagePathSelectedAndFiltered) {
        Intent openSavedImageFromGalleryIntent = new Intent();
        openSavedImageFromGalleryIntent.setAction(Intent.ACTION_VIEW);
        openSavedImageFromGalleryIntent.setDataAndType(Uri.parse(imagePathSelectedAndFiltered), "image/*");
        startActivity(openSavedImageFromGalleryIntent);
    }

    private void openImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            Intent openImageFromGalleryIntent = new Intent(Intent.ACTION_PICK);
                            openImageFromGalleryIntent.setType("image/*");
                            startActivityForResult(openImageFromGalleryIntent, PERMISSION_PICK_IMAGE);
                        }

                        else{
                            Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == PERMISSION_PICK_IMAGE) {
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

            // clear bitmap memory
            mainActivityOriginalBitmap.recycle();
            mainActivityFilteredBitmap.recycle();
            mainActivityFinalBitmap.recycle();

            mainActivityOriginalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            mainActivityFilteredBitmap = mainActivityOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            mainActivityFinalBitmap = mainActivityOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            mainActivityPhotoEditorView.getSource().setImageBitmap(mainActivityOriginalBitmap);
            bitmap.recycle();

            // render selected img thumbnail
            mainActivityFiltersListFragment.displayThumbnail(mainActivityOriginalBitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBrushSizeChangedListener(float size) {
        mainActivityPhotoEditor.setBrushSize(size);
    }

    @Override
    public void onBrushOpacityChangedListener(int opacity) {
        mainActivityPhotoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushColorChangedListener(int color) {
        mainActivityPhotoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangedListener(boolean isEraser) {
        if(isEraser){
            mainActivityPhotoEditor.brushEraser();
        }

        else{
            mainActivityPhotoEditor.setBrushDrawingMode(true);
        }
    }
}
