package ece493.kdbanman.Activities;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ece493.kdbanman.Model.Filterable;
import ece493.kdbanman.Model.ImageFilter;
import ece493.kdbanman.Model.ModelServer;
import ece493.kdbanman.ObserverActivity;
import ece493.kdbanman.R;

public class SettingsActivity extends ObserverActivity {

    TextView kernelSizeTextView;
    EditText kernelSizeEditText;

    Filterable image;
    ImageFilter imageFilter;

    @Override
    protected void renderViews() {
        if (!allViewsInitialized()) {
            Log.w("SettingsActivity", "renderViews() called before views initialized.");
            return;
        }

        if (!modelInitialized()) {
            Log.w("SettingsActivity", "renderViews() called before model initialized.");
            return;
        }

        renderKernelSize();
    }

    private void renderKernelSize() {
        kernelSizeTextView.setText(Integer.toString(imageFilter.getKernelSize()));
        kernelSizeEditText.setTextColor((0xFF000000));
    }



    // =====================
    // Android boilerplate and control events

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        initializeModel();
        initializeViews();
        initializeControllers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    // =====================
    // Private methods

    private void initializeModel() {
        imageFilter = ModelServer.getInstance().getImageFilter(this);
        image = ModelServer.getInstance().getFilterable(this);
    }

    private void initializeViews() {
        kernelSizeTextView = (TextView) findViewById(R.id.textViewReadOnlyKernelSize);
        kernelSizeEditText = (EditText) findViewById(R.id.editTextKernelSize);
    }

    private void initializeControllers() {
        kernelSizeEditText.addTextChangedListener(new KernelSizeTextWatcher());
    }

    private boolean modelInitialized() {
        return !(image == null || imageFilter == null);
    }

    private boolean allViewsInitialized() {
        return !(kernelSizeTextView == null || kernelSizeEditText == null);
    }



    // ======================
    // Controller methods and classes

    // NOTE: This controller class crosses some responsibilities of the view and the model, but I'm
    //       not sure how to address it.  It changes text color (view responsibility) in response to
    //       model object relationship validation (model responsibility).
    private class KernelSizeTextWatcher implements TextWatcher {
        boolean badKernelSize = false;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 0) {
                return;
            }

            try {
                int kernelSize = Integer.parseInt(s.toString());

                if (image.hasImage() && kernelSize > Math.min(image.getWidth(), image.getHeight())) {
                    throw new IllegalArgumentException();
                }
                imageFilter.setKernelSize(kernelSize);
                badKernelSize = false;
            } catch (NumberFormatException e) {
                Log.w("SettingsActivity", "Kernel size input was not an integer.");
                badKernelSize = true;
                kernelSizeEditText.setTextColor(0xFFFF0000);
                queueBadKernelSizeToast();
            } catch (IllegalArgumentException e) {
                badKernelSize = true;
                kernelSizeEditText.setTextColor(0xFFFF0000);
                queueBadKernelSizeToast();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
        }

        private void queueBadKernelSizeToast() {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (badKernelSize) {
                        Toast.makeText(SettingsActivity.this, R.string.error_bad_kernel_size, Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1500);
        }
    }
}
