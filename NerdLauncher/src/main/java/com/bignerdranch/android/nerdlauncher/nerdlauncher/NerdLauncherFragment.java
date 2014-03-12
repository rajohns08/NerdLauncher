package com.bignerdranch.android.nerdlauncher.nerdlauncher;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class NerdLauncherFragment extends ListFragment {
    private static final String TAG = "NerdLauncherFragment";
    private PackageManager pm;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent startupIntent = new Intent(Intent.ACTION_MAIN);
        startupIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        pm = getActivity().getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(startupIntent, 0);

        Log.i(TAG, "I've found " + activities.size() + " activities.");

        Collections.sort(activities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo a, ResolveInfo b) {
                return String.CASE_INSENSITIVE_ORDER.compare(a.loadLabel(pm).toString(), b.loadLabel(pm).toString());
            }
        });

        AppAdapter appAdapter = new AppAdapter(activities);
        setListAdapter(appAdapter);

//        TODO: ADD ITEM TO ACTION BAR FOR CHANGING TO ANOTHER ACTIVITY
    }

    private class AppAdapter extends ArrayAdapter<ResolveInfo> {
        public AppAdapter(List<ResolveInfo> apps) {
            super(getActivity(), 0, apps);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_application, null);
            }

            ResolveInfo ri = getItem(position);
            ImageView imageView = (ImageView)convertView.findViewById(R.id.application_image);
            imageView.setImageDrawable(ri.loadIcon(pm));
            TextView textView = (TextView)convertView.findViewById(R.id.application_label);
            textView.setText(ri.loadLabel(pm));

            return convertView;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ResolveInfo resolveInfo = (ResolveInfo)l.getAdapter().getItem(position);
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        if (activityInfo == null) return;

        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setClassName(activityInfo.applicationInfo.packageName, activityInfo.name);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        startActivity(i);
    }
}
