package ece493.kdbanman.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import ece493.kdbanman.Activities.FilterImageControllers.CancelFilterOnClickListener;
import ece493.kdbanman.Activities.FilterImageControllers.ChooseFilterOnItemSelectedListener;
import ece493.kdbanman.Activities.FilterImageControllers.FilterImageOnClickListener;
import ece493.kdbanman.Activities.FilterImageControllers.WarpImageOnTouchListener;
import ece493.kdbanman.Model.Filterable;
import ece493.kdbanman.Model.ImageFilter;
import ece493.kdbanman.Model.ModelServer;
import ece493.kdbanman.ObserverActivity;
import ece493.kdbanman.R;

public class FilterImageActivity extends ObserverActivity {

    private ImageView imageView;
    private Button filterImageButton;
    private Spinner filterChooserSpinner;
    private ProgressBar imageFilterProgressBar;
    private TextView filterStatusTextView;
    private Button filterCancelButton;

    private static final int
            REQUEST_CODE_IMAGE_URI = 0,
            REQUEST_CODE_READ_STORAGE = 1;

    private Filterable image;
    private ImageFilter imageFilter;

    @Override
    protected void renderViews() {
        if (!allViewsInitialized()) {
            Log.w("FilterImageActivity", "renderViews() called before views initialized.");
            return;
        }

        if (!modelInitialized()) {
            Log.w("FilterImageActivity", "renderViews() called before model initialized.");
            return;
        }

        renderImage();
        renderFilterChoice();
        renderRunningFilter();
    }



    // =====================
    // Android boilerplate and control events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_image);

        initializeModel();
        initializeViews();
        initializeControllers();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!hasFilePermissions()) {
            requestFilePermissions();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter_image, menu);
        return true;
    }

    // Currently used only to process image selection from filesystem.
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultCode, returnedIntent);

        // from http://stackoverflow.com/a/9745454
        if (requestCode == REQUEST_CODE_IMAGE_URI) {
            if (resultCode == RESULT_OK) {
                handleImageLoadIntent(returnedIntent);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            launchSettings();
            return true;
        } else if (id == R.id.action_load_image) {
            launchImageChooser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // =====================
    // Private methods

    private void initializeModel() {
        image = ModelServer.getInstance().getFilterable(this);
        imageFilter = ModelServer.getInstance().getImageFilter(this);
    }

    private void initializeViews() {
        imageView = (ImageView) findViewById(R.id.imageView);
        filterImageButton = (Button) findViewById(R.id.buttonFilterImage);
        filterChooserSpinner = (Spinner) findViewById(R.id.spinnerFilterChooser);
        imageFilterProgressBar = (ProgressBar) findViewById(R.id.progressBarImageFilter);
        filterStatusTextView = (TextView) findViewById(R.id.textViewFilterStatus);
        filterCancelButton = (Button) findViewById(R.id.button);
    }

    private void initializeControllers() {
        imageView.setOnTouchListener(new WarpImageOnTouchListener(ViewConfiguration.get(this).getScaledTouchSlop()));
        filterImageButton.setOnClickListener(new FilterImageOnClickListener(imageFilter, image));
        filterCancelButton.setOnClickListener(new CancelFilterOnClickListener(imageFilter));
        filterChooserSpinner.setOnItemSelectedListener(new ChooseFilterOnItemSelectedListener(imageFilter));
    }

    private boolean modelInitialized() {
        return !(image == null ||
                imageFilter == null);
    }

    private boolean allViewsInitialized() {
        return !(imageView == null ||
                filterImageButton == null ||
                filterChooserSpinner == null ||
                imageFilterProgressBar == null ||
                filterStatusTextView == null ||
                filterCancelButton == null);
    }

    private void renderFilterChoice() {
        switch (imageFilter.getKernelType()) {
            case MEAN:
                filterChooserSpinner.setSelection(0);
                break;
            case MEDIAN:
                filterChooserSpinner.setSelection(1);
                break;

            default:
                Toast.makeText(this, "Unrecognized filter type sent to view.", Toast.LENGTH_LONG).show();
                break;
        }
    }

    private void renderImage() {
        if (image.hasImage()) {
            // Add and remove a ViewTreeObserver to ensure measured width and height are used.
            //     see http://stackoverflow.com/a/4649842/3367144
            ViewTreeObserver.OnPreDrawListener preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    int width = imageView.getWidth();
                    int height = imageView.getHeight();
                    imageView.setImageBitmap(image.getScaledCopy(height, width));

                    imageView.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            };
            imageView.getViewTreeObserver().addOnPreDrawListener(preDrawListener);

            filterImageButton.setEnabled(true);
        } else {
            imageView.setImageResource(R.drawable.placeholder);
            filterImageButton.setEnabled(false);
        }
    }

    private void renderRunningFilter() {
        if (imageFilter.isFilterRunning()) {
            filterStatusTextView.setVisibility(View.VISIBLE);
            filterCancelButton.setVisibility(View.VISIBLE);
            imageFilterProgressBar.setVisibility(View.VISIBLE);
            imageView.setAlpha(0.35f);

            if (imageFilter.isTaskStopping()) {
                filterStatusTextView.setText(R.string.message_stopping_filter);
                filterStatusTextView.setTextColor(0xFFFF0000);

                imageFilterProgressBar.setIndeterminateTintMode(PorterDuff.Mode.DST);
                imageFilterProgressBar.getIndeterminateDrawable().setColorFilter(0xFFFF0000, PorterDuff.Mode.MULTIPLY);
            } else {
                filterStatusTextView.setText(Integer.toString(imageFilter.getTaskProgress()) + " %");
                filterStatusTextView.setTextColor(0xFF000000);

                imageFilterProgressBar.setIndeterminateTintMode(PorterDuff.Mode.SRC_ATOP);
                imageFilterProgressBar.getIndeterminateDrawable().setColorFilter(0xFF006600, PorterDuff.Mode.MULTIPLY);
            }
        } else {
            filterStatusTextView.setVisibility(View.GONE);
            filterCancelButton.setVisibility(View.GONE);
            imageFilterProgressBar.setVisibility(View.GONE);
            imageView.setAlpha(1f);
        }
    }

    private void requestFilePermissions() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_STORAGE);
    }

    private boolean hasFilePermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void handleImageLoadIntent(Intent returnedIntent) {
        try {
            Uri imageUri = getUriFromIntentReturn(returnedIntent);
            Bitmap newBitmap = readBitmapFromUri(imageUri);
            setNewBitmap(newBitmap);
        } catch (Throwable e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Uri getUriFromIntentReturn(Intent returnedIntent) {
        if (returnedIntent == null || returnedIntent.getData() == null || returnedIntent.getData().getPath() == null) {
            throw new IllegalArgumentException(getString(R.string.error_no_image_error_received));
        }

        return returnedIntent.getData();
    }

    private Bitmap readBitmapFromUri(Uri imageUri) throws IOException {
        if (!hasFilePermissions()) {
            requestFilePermissions();
            throw new IOException(getString(R.string.message_allow_file_permissions));
        }

        InputStream imageStream = getContentResolver().openInputStream(imageUri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(imageStream, null, options);
    }

    private void launchSettings() {
        Intent startSettingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(startSettingsIntent);
    }

    private void launchImageChooser() {
        // From http://stackoverflow.com/a/16930842
        Intent chooseImageIntent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(chooseImageIntent, REQUEST_CODE_IMAGE_URI);
    }



    // ======================
    // Controller methods and classes

    private void setNewBitmap(Bitmap newBitmap) {
        if (newBitmap == null) {
            throw new IllegalArgumentException(getString(R.string.error_no_image_error_received));
        }

        if (imageFilter.isFilterRunning()) {
            imageFilter.cancelBackgroundFilterTasks();
        }
        image.setImage(newBitmap);

        if (imageFilter.getKernelSize() > Math.min(image.getWidth(), image.getHeight())) {
            int shrunkKernelSize = Math.min(image.getWidth(), image.getHeight());
            shrunkKernelSize = Math.max(3, shrunkKernelSize - 1);
            if (shrunkKernelSize % 2 == 0) {
                shrunkKernelSize -= 1;
            }

            imageFilter.setKernelSize(shrunkKernelSize);
            Toast.makeText(this, R.string.message_kernel_size_shrunk, Toast.LENGTH_LONG).show();
        }
    }

}
