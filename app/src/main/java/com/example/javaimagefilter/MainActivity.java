package com.example.javaimagefilter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.javaimagefilter.Adapter.ViewPagerAdapter;
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

public class MainActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener {

    public static final String pictureName = "flash.jpg";
    public static final int PERMISSION_PICK_IMAGE = 1000;

    ImageView mainActivityImgPreview;
    TabLayout mainActivityTabLayout;
    ViewPager mainActivityViewPager;
    CoordinatorLayout mainActivityCoordinatorLayout;

    Bitmap mainActivityOriginalBitmap, mainActivityFilteredBitmap, mainActivityFinalBitmap;

    FiltersListFragment mainActivityFiltersListFragment;
    EditImageFragment mainActivityEditImageFragment;

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
        mainActivityImgPreview = (ImageView) findViewById(R.id.activity_main_image_preview);
        mainActivityTabLayout = (TabLayout) findViewById(R.id.activity_main_tabs);
        mainActivityViewPager = (ViewPager) findViewById(R.id.activity_main_viewPager);
        mainActivityCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.activity_main_coordinator);

        loadImage();
        setupViewPager(mainActivityViewPager);
        mainActivityTabLayout.setupWithViewPager(mainActivityViewPager);
    }

    private void loadImage() {
        mainActivityOriginalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName, 300, 300);
        mainActivityFilteredBitmap = mainActivityOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mainActivityFinalBitmap = mainActivityOriginalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        mainActivityImgPreview.setImageBitmap(mainActivityOriginalBitmap);
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
        mainActivityImgPreview.setImageBitmap(myFilterInBrightness.processFilter(mainActivityFinalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        mainActivitySaturationFinal = saturation;
        Filter myFilterInSaturation = new Filter();
        myFilterInSaturation.addSubFilter(new SaturationSubfilter(saturation));
        mainActivityImgPreview.setImageBitmap(myFilterInSaturation.processFilter(mainActivityFinalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onContrastChanged(float contrast) {
        mainActivityContrastFinal = contrast;
        Filter myFilterInContrast = new Filter();
        myFilterInContrast.addSubFilter(new ContrastSubFilter(contrast));
        mainActivityImgPreview.setImageBitmap(myFilterInContrast.processFilter(mainActivityFinalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
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
        mainActivityImgPreview.setImageBitmap(filter.processFilter(mainActivityFilteredBitmap));
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
                            try {
                                final String imagePathSelectedAndFiltered = BitmapUtils.insertImage(getContentResolver(),
                                        mainActivityFinalBitmap,
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
            mainActivityImgPreview.setImageBitmap(mainActivityOriginalBitmap);
            bitmap.recycle();

            // render selected img thumbnail
            mainActivityFiltersListFragment.displayThumbnail(mainActivityOriginalBitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
