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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.InputStream;

import ece493.kdbanman.Model.FilterKernelType;
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

    private static final int REQUEST_CODE_IMAGE_URI = 0,
        REQUEST_CODE_READ_STORAGE = 1;

    private Filterable image;
    private ImageFilter imageFilter;

    @Override
    protected void renderViews() {
        if (imageView == null || filterChooserSpinner == null || filterImageButton == null) {
            Log.w("FilterImageActivity", "renderViews() called before views initialized.");
            return;
        }

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

        if (imageFilter.isFilterRunning()) {
            filterStatusTextView.setVisibility(View.VISIBLE);
            filterCancelButton.setVisibility(View.VISIBLE);
            imageFilterProgressBar.setVisibility(View.VISIBLE);
            imageView.setAlpha(0.35f);

            if (imageFilter.isTaskStopping()) {
                filterStatusTextView.setText(R.string.stopping_filter_message);
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

    // =====================
    // Boilerplate and control events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_image);

        image = ModelServer.getInstance().getFilterable(this);
        imageFilter = ModelServer.getInstance().getImageFilter(this);

        imageView = (ImageView) findViewById(R.id.imageView);
        filterImageButton = (Button) findViewById(R.id.buttonFilterImage);
        filterChooserSpinner = (Spinner) findViewById(R.id.spinnerFilterChooser);
        imageFilterProgressBar = (ProgressBar) findViewById(R.id.progressBarImageFilter);
        filterStatusTextView = (TextView) findViewById(R.id.textViewFilterStatus);
        filterCancelButton = (Button) findViewById(R.id.button);

        filterImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageFilter.isFilterRunning()) {
                    imageFilter.cancelBackgroundFilterTasks();
                }

                imageFilter.backgroundFilterImage(image);
            }
        });
        filterCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageFilter.isFilterRunning()) {
                    imageFilter.cancelBackgroundFilterTasks();
                }
            }
        });
        filterChooserSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case (0):
                        imageFilter.setKernelType(FilterKernelType.MEAN);
                        break;
                    case (1):
                        imageFilter.setKernelType(FilterKernelType.MEDIAN);
                        break;
                    default:
                        Toast.makeText(FilterImageActivity.this, R.string.unrecognized_filter_from_view, Toast.LENGTH_LONG).show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                imageFilter.setKernelType(FilterKernelType.MEAN);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_STORAGE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter_image, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultcode, Intent returnedIntent) {
        super.onActivityResult(requestCode, resultcode, returnedIntent);

        // from http://stackoverflow.com/a/9745454
        if (requestCode == REQUEST_CODE_IMAGE_URI) {
            if (resultcode == RESULT_OK) {
                try {
                    if (returnedIntent != null && returnedIntent.getData() != null && returnedIntent.getData().getPath() != null) {
                        Uri imageUri = returnedIntent.getData();

                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_STORAGE);
                            throw new Exception("Access to external storage must be granted before images may be loaded.");
                        }

                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        Bitmap newBitmap = BitmapFactory.decodeStream(imageStream, null, options);

                        if (newBitmap == null) {
                            throw new IllegalArgumentException(getString(R.string.no_image_error));
                        }

                        // TODO test for null, throw exception.  probably wrap a bunch of this in setNewBitmap() in Model mutation section
                        if (imageFilter.isFilterRunning()) {
                            imageFilter.cancelBackgroundFilterTasks();
                        }
                        image.setImage(newBitmap);

                    } else {
                        throw new IllegalArgumentException(getString(R.string.no_image_error));
                    }
                } catch (Throwable e) {
                    Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent startSettingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsIntent);

            return true;
        } else if (id == R.id.action_load_image) {
            // From http://stackoverflow.com/a/16930842
            Intent chooseImageIntent = new Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(chooseImageIntent, REQUEST_CODE_IMAGE_URI);
        }

        return super.onOptionsItemSelected(item);
    }

    // ======================
    // Model mutation


}
