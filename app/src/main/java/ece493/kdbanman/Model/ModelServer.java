package ece493.kdbanman.Model;

import ece493.kdbanman.Observer;

/**
 * Singleton data model server.  Serves observable model objects to the views observing them.
 *
 * Classes that are not instantiable outside the Model package (EX: ImageFilter) are instantiated
 * and managed by the ModelServer.  In essence, ModelServer is the single point of reference for
 * a view to access the Model.
 *
 * Created by kdbanman on 1/13/16.
 */
public class ModelServer {

    private Filterable filterable;
    private ImageFilter imageFilter;

    private static ModelServer instance;

    private ModelServer() {
        filterable = new Filterable();
        imageFilter = new ImageFilter();
    }

    public static ModelServer getInstance() {
        if (instance == null) {
            instance = new ModelServer();
        }

        return instance;
    }

    public Filterable getFilterable(Observer imageChangeObserver) {
        if (imageChangeObserver == null) {
            throw new IllegalArgumentException("Model objects will only be served to observers.");
        }

        filterable.addObserver(imageChangeObserver);
        return  filterable;
    }

    public ImageFilter getImageFilter(Observer filterChangeObserver) {
        if (filterChangeObserver == null) {
            throw new IllegalArgumentException("Model objects will only be served to observers.");
        }

        imageFilter.addObserver(filterChangeObserver);
        return imageFilter;
    }
}
