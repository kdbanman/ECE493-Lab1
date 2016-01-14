package ece493.kdbanman.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import ece493.kdbanman.Model.Filterable;
import ece493.kdbanman.Model.ImageFilter;
import ece493.kdbanman.Model.ModelServer;
import ece493.kdbanman.ObserverActivity;
import ece493.kdbanman.R;

public class FilterImageActivity extends ObserverActivity {

    private static final int REQUEST_CODE_IMAGE_URI = 0;

    private Filterable image;
    private ImageFilter imageFilter;

    @Override
    protected void renderViews() {
        if (image.hasImage()) {
            ImageView imageView = (ImageView) findViewById(R.id.imageView);
            int width = imageView.getMaxWidth();
            int height = imageView.getMaxHeight();
            imageView.setImageBitmap(image.getScaledCopy(height, width));
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_filter_image, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultcode, Intent data) {
        super.onActivityResult(requestCode, resultcode, data);

        // from http://stackoverflow.com/a/16930842
        if (requestCode == REQUEST_CODE_IMAGE_URI) {
            if (data != null && data.getData() != null && data.getData().getPath() != null) {
                Bitmap newBitmap = BitmapFactory.decodeFile(data.getData().getPath());

                // TODO test for null, throw exception.  probably wrap a bunch of this in setNewBitmap() in Model mutation section
                imageFilter.cancelBackgroundFilterTask();
                image.setImage(newBitmap);

            } else {
                Toast.makeText(this, R.string.error_no_image_path, Toast.LENGTH_LONG).show();
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
