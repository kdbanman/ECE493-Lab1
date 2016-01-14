package ece493.kdbanman;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of the Observable component of the Observer GOF pattern.
 *
 * Created by kdbanman on 1/13/16.
 */
public abstract class Observable {

    private List<Observer> observers = new ArrayList<>();

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.observerNotify();
        }
    }

}
