package ece493.kdbanman.Activities.FilterImageControllers;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import ece493.kdbanman.Model.FilterKernelType;
import ece493.kdbanman.Model.ImageFilter;

/**
 * Created by kdban on 2/5/2016.
 */
public class ChooseFilterOnItemSelectedListener implements AdapterView.OnItemSelectedListener {

    ImageFilter imageFilter;

    public ChooseFilterOnItemSelectedListener(ImageFilter imageFilter) {
        this.imageFilter = imageFilter;
    }

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
                Log.e("FilterSelectedListener", "Unrecognized filter type sent from view.");
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        imageFilter.setKernelType(FilterKernelType.MEAN);
    }
}
