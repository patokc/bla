package hr.foi.air1719.personaltracker.fragments;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import hr.foi.air1719.core.facade.DatabaseFacade;
import hr.foi.air1719.database.entities.Activity;
import hr.foi.air1719.database.entities.ActivityMode;
import hr.foi.air1719.database.entities.GpsLocation;
import hr.foi.air1719.location.IGPSActivity;
import hr.foi.air1719.location.MyLocation;
import hr.foi.air1719.personaltracker.Main;
import hr.foi.air1719.personaltracker.R;

/**
 * Created by Nikolina on 13.12.2017..
 */

public class RunningModeFragment extends Fragment implements IGPSActivity
{

    MyLocation myLocation=null;
    Button btnRunningModeStart=null;
    TextView txtTotalDistance=null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.running_mode_fragment, container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        txtTotalDistance=(TextView) getView().findViewById(R.id.txtTotalDistance);

        btnRunningModeStart = (Button) getView().findViewById(R.id.btnRunningModeStart);
        btnRunningModeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                onClick_StartRunningMode(v);
            }
        });



    }

    private void onClick_StartRunningMode(View v)

    {
        if(myLocation ==null)
        {
            Toast.makeText(this.getActivity(), "Start Running mode", Toast.LENGTH_SHORT).show();
            myLocation = new MyLocation();
            myLocation.LocationStart(this);
            btnRunningModeStart.setText("Stop");
        }

        else
        {
            Toast.makeText(this.getActivity(), "Stop Running mode", Toast.LENGTH_SHORT).show();
            myLocation.LocationListenerStop();
            myLocation = null;
            btnRunningModeStart.setText("Start");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
    }


    Location lastPoint = null;
    double totalDistance = 0;
    public double CalculateDistance(Location startPoint, Location endPoint)
    {
        return startPoint.distanceTo(endPoint);
    }


    @Override
    public void locationChanged(Location location)
    {
        try
        {

            if(lastPoint==null)lastPoint = location;

            totalDistance += CalculateDistance(lastPoint, location);

            lastPoint=location;
            txtTotalDistance.setText(totalDistance + " km");

            DatabaseFacade dbf = new DatabaseFacade(getView().getContext());
            Activity activity = new Activity(ActivityMode.RUNNING);
            dbf.saveLocation(new GpsLocation(activity.getActivityId(), location.getLongitude(), location.getLatitude(), location.getAccuracy()));
        }catch (Exception E)

        {
        E.printStackTrace();
        }
    }


    public void onResume(){
        super.onResume();
        ((Main) getActivity()).setActionBarTitle("Running mode");
         NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
       //  navigationView.setNavigationItemSelectedListener((Main) getActivity());
        navigationView.setCheckedItem(R.id.runningMode);

    }
}

