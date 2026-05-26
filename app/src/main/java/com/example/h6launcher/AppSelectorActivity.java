package com.example.h6launcher;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppSelectorActivity extends Activity {
    private List<AppInfo> apps;
    private AppListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_selector);
        
        GridView gridView = findViewById(R.id.app_grid);
        
        apps = AppUtils.getInstalledApps(this);
        adapter = new AppListAdapter();
        gridView.setAdapter(adapter);
        
        gridView.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo app = apps.get(position);
            Intent result = new Intent();
            result.putExtra("packageName", app.getPackageName());
            result.putExtra("className", app.getClassName());
            result.putExtra("label", app.getLabel());
            setResult(RESULT_OK, result);
            finish();
        });
    }

    private class AppListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return apps.size();
        }

        @Override
        public Object getItem(int position) {
            return apps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.app_item, parent, false);
            }
            
            ImageView iconView = view.findViewById(R.id.app_icon);
            TextView labelView = view.findViewById(R.id.app_label);
            
            AppInfo app = apps.get(position);
            iconView.setImageDrawable(app.getIcon());
            labelView.setText(app.getLabel());
            
            return view;
        }
    }
}
