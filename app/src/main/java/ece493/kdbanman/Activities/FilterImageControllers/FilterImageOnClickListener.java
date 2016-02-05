package ece493.kdbanman.Activities.FilterImageControllers;

import android.view.View;

import ece493.kdbanman.Model.Filterable;
import ece493.kdbanman.Model.ImageFilter;

/**
 * Created by kdban on 2/5/2016.
 */
public class FilterImageOnClickListener implements View.OnClickListener {

    ImageFilter imageFilter;
    Filterable image;

    public FilterImageOnClickListener(ImageFilter imageFilter, Filterable image) {
        this.imageFilter = imageFilter;
        this.image = image;
    }

    @Override
    public void onClick(View v) {
        if (imageFilter.isFilterRunning()) {
            imageFilter.cancelBackgroundFilterTasks();
        }

        imageFilter.filterImageInBackground(image);
    }
}
