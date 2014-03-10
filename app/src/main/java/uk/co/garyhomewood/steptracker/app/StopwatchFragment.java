package uk.co.garyhomewood.steptracker.app;


import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;

/**
 * Created by user1 on 10/03/2014.
 */
public class StopwatchFragment extends Fragment {

    private Chronometer chronometer;
    private long elapsedTime;

    public static StopwatchFragment newInstance() {
        StopwatchFragment fragment = new StopwatchFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        chronometer = (Chronometer) rootView.findViewById(R.id.chronometer);
        chronometer.setBase(SystemClock.elapsedRealtime());

        return rootView;
    }

    public void resetClock() {
        elapsedTime = 0;
        chronometer.setBase(SystemClock.elapsedRealtime());
    }

    public void startClock() {
        resetClock();
        chronometer.start();
    }

    public long stopClock() {
        elapsedTime = SystemClock.elapsedRealtime() - chronometer.getBase();
        chronometer.stop();
        return elapsedTime;
    }
}
