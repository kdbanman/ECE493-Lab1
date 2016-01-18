package ece493.kdbanman;

import android.app.Activity;

/**
 * An abstract extension of android's Activity that implies rerendering of all views from a single
 * model object change (i.e. observer notification) or activity resume (i.e. onResume()).
 *
 * Created by kdbanman on 1/13/16.
 */
public abstract class ObserverActivity extends Activity implements Observer {

    @Override
    public void observerNotify() {
        renderViews();
    }

    @Override
    protected void onResume() {
        super.onResume();

        renderViews();
    }

    protected abstract void renderViews();

}
