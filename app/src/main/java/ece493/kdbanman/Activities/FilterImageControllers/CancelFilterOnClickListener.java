package ece493.kdbanman.Activities.FilterImageControllers;

import android.view.View;

import ece493.kdbanman.Model.ImageFilter;

/**
 * Created by kdban on 2/5/2016.
 */
public class CancelFilterOnClickListener implements View.OnClickListener {

    private ImageFilter imageFilter;

    public CancelFilterOnClickListener(ImageFilter imageFilter) {
        this.imageFilter = imageFilter;
    }

    @Override
    public void onClick(View v) {
        if (imageFilter.isFilterRunning()) {
            imageFilter.cancelBackgroundFilterTasks();
        }
    }
}
