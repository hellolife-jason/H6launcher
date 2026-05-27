package com.example.h6launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements SplitScreenLayout.OnWindowClickListener {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_APP_SELECTOR = 1001;
    private static final String SETTINGS_PACKAGE_NAME = "com.example.h6launcher.settings";
    
    private RelativeLayout rootLayout;
    private DockView dockView;
    private SplitScreenLayout splitScreenLayout;
    private FrameLayout contentContainer;
    private View appListView;
    private GridView appGrid;
    private ConfigManager configManager;
    private int selectedWindowIndex = -1;
    private List<AppInfo> allApps;
    private AppListAdapter appListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        configManager = H6LauncherApp.getInstance().getConfigManager();
        
        rootLayout = findViewById(R.id.root_layout);
        dockView = findViewById(R.id.dock_view);
        splitScreenLayout = findViewById(R.id.split_screen_layout);
        contentContainer = findViewById(R.id.content_container);
        
        splitScreenLayout.setOnWindowClickListener(this);
        
        initDock();
        initSplitScreen();
        initAppListView();
        
        dockView.setOnHomeToggleListener(this::onHomeToggle);
        
        loadLastConfig();
        
        updateLayoutForDockPosition();
    }

    @Override
    protected void onResume() {
        super.onResume();
        applySettings();
    }

    private void applySettings() {
        if (dockView == null || splitScreenLayout == null) {
            return;
        }
        
        int dockPosition = configManager.getDockPosition();
        int splitMode = configManager.getSplitMode();
        
        if (dockView.getPosition() != dockPosition) {
            dockView.setPosition(dockPosition);
            updateLayoutForDockPosition();
        }
        
        if (splitScreenLayout.getSplitMode() != splitMode) {
            splitScreenLayout.setSplitMode(splitMode);
        }
    }

    private void initDock() {
        dockView.setPosition(configManager.getDockPosition());
        dockView.setApps(getDefaultDockApps());
    }

    private void initSplitScreen() {
        splitScreenLayout.setSplitMode(configManager.getSplitMode());
    }

    private void initAppListView() {
        appListView = getLayoutInflater().inflate(R.layout.app_list_content, null);
        appGrid = appListView.findViewById(R.id.app_grid);
        
        allApps = AppUtils.getInstalledApps(this);
        
        AppInfo settingsApp = new AppInfo();
        settingsApp.setPackageName(SETTINGS_PACKAGE_NAME);
        settingsApp.setClassName(SettingsActivity.class.getName());
        settingsApp.setLabel("桌面设置");
        settingsApp.setIcon(getResources().getDrawable(R.drawable.ic_settings));
        allApps.add(0, settingsApp);
        
        appListAdapter = new AppListAdapter();
        appGrid.setAdapter(appListAdapter);
        
        appGrid.setOnItemClickListener((parent, view, position, id) -> {
            AppInfo app = allApps.get(position);
            if (SETTINGS_PACKAGE_NAME.equals(app.getPackageName())) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            } else {
                launchApp(app);
            }
        });
    }

    private void launchApp(AppInfo app) {
        try {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(app.getPackageName());
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                android.util.Log.e(TAG, "No launch intent for: " + app.getPackageName());
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to launch app: " + app.getPackageName(), e);
        }
    }

    @Override
    public void onBackPressed() {
        if (appListView.getParent() != null) {
            showDesktop();
        } else {
            super.onBackPressed();
        }
    }

    private List<AppInfo> getDefaultDockApps() {
        List<AppInfo> apps = AppUtils.getInstalledApps(this);
        List<AppInfo> dockApps = new ArrayList<>();
        
        String[] defaultPackages = {
            "com.android.settings",
            "com.android.contacts",
            "com.android.dialer",
            "com.android.music",
            "com.android.camera"
        };
        
        for (String pkg : defaultPackages) {
            for (AppInfo app : apps) {
                if (app.getPackageName().equals(pkg)) {
                    dockApps.add(app);
                    break;
                }
            }
        }
        
        if (dockApps.isEmpty()) {
            dockApps.addAll(apps.subList(0, Math.min(5, apps.size())));
        }
        
        return dockApps;
    }

    private void loadLastConfig() {
        int activeIndex = configManager.getActiveConfigIndex();
        SplitConfig config = configManager.getConfig(activeIndex);
        
        if (config != null) {
            applyConfig(config);
        }
    }

    private void applyConfig(SplitConfig config) {
        splitScreenLayout.setSplitMode(config.getSplitMode());
        splitScreenLayout.setRatios(config.getRatios());
        
        List<WindowConfig> windows = config.getWindows();
        for (int i = 0; i < windows.size(); i++) {
            splitScreenLayout.setWindowConfig(i, windows.get(i));
        }
    }

    @Override
    public void onWindowClick(int windowIndex) {
        selectedWindowIndex = windowIndex;
        Intent intent = new Intent(this, AppSelectorActivity.class);
        startActivityForResult(intent, REQUEST_APP_SELECTOR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_APP_SELECTOR && resultCode == RESULT_OK && data != null) {
            String packageName = data.getStringExtra("packageName");
            String className = data.getStringExtra("className");
            String label = data.getStringExtra("label");
            
            WindowConfig config = new WindowConfig(packageName, className, label);
            splitScreenLayout.setWindowConfig(selectedWindowIndex, config);
            
            // 启动选中的应用
            launchAppInWindow(packageName, className, selectedWindowIndex);
        }
    }
    
    private void launchAppInWindow(String packageName, String className, int windowIndex) {
        try {
            PackageManager pm = getPackageManager();
            Intent intent = pm.getLaunchIntentForPackage(packageName);
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                
                // 尝试使用系统分屏模式（API 24+）
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT);
                }
                
                startActivity(intent);
            }
        } catch (Exception e) {
            android.util.Log.e(TAG, "Failed to launch app in window: " + packageName, e);
        }
    }

    private void onHomeToggle(boolean isHomeMode) {
        if (isHomeMode) {
            showDesktop();
        } else {
            showAppList();
        }
    }

    private void showDesktop() {
        if (appListView.getParent() != null) {
            ((ViewGroup) appListView.getParent()).removeView(appListView);
        }
        splitScreenLayout.setVisibility(View.VISIBLE);
        dockView.setHomeMode(true);
    }

    private void showAppList() {
        splitScreenLayout.setVisibility(View.GONE);
        if (appListView.getParent() == null) {
            contentContainer.addView(appListView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, 
                FrameLayout.LayoutParams.MATCH_PARENT
            ));
        }
        dockView.setHomeMode(false);
    }

    public void saveCurrentConfig(int configIndex) {
        SplitConfig config = new SplitConfig();
        config.setSplitMode(splitScreenLayout.getSplitMode());
        config.setRatios(splitScreenLayout.getRatios());
        
        List<WindowConfig> windows = new ArrayList<>();
        int mode = config.getSplitMode();
        for (int i = 0; i < mode; i++) {
            WindowConfig windowConfig = splitScreenLayout.getWindowConfig(i);
            windows.add(windowConfig != null ? windowConfig : new WindowConfig());
        }
        config.setWindows(windows);
        
        configManager.saveConfig(configIndex, config);
        configManager.setActiveConfigIndex(configIndex);
    }

    public void loadConfig(int configIndex) {
        SplitConfig config = configManager.getConfig(configIndex);
        if (config != null) {
            applyConfig(config);
            configManager.setActiveConfigIndex(configIndex);
        }
    }

    public void setDockPosition(int position) {
        configManager.setDockPosition(position);
        dockView.setPosition(position);
        updateLayoutForDockPosition();
    }

    public void setSplitMode(int mode) {
        configManager.setSplitMode(mode);
        splitScreenLayout.setSplitMode(mode);
    }

    private void updateLayoutForDockPosition() {
        int position = configManager.getDockPosition();
        RelativeLayout.LayoutParams dockParams = (RelativeLayout.LayoutParams) dockView.getLayoutParams();
        RelativeLayout.LayoutParams containerParams = (RelativeLayout.LayoutParams) contentContainer.getLayoutParams();
        
        if (position == ConfigManager.DOCK_POSITION_LEFT) {
            dockParams.width = 72;
            dockParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            dockParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            dockParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            dockParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            dockParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, 0);
            
            containerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            containerParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            containerParams.addRule(RelativeLayout.RIGHT_OF, R.id.dock_view);
            containerParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            containerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            containerParams.addRule(RelativeLayout.ABOVE, 0);
        } else {
            dockParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            dockParams.height = 72;
            dockParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            dockParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            dockParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            dockParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            
            containerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            containerParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
            containerParams.addRule(RelativeLayout.ABOVE, R.id.dock_view);
            containerParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            containerParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            containerParams.addRule(RelativeLayout.RIGHT_OF, 0);
        }
        
        dockView.setLayoutParams(dockParams);
        contentContainer.setLayoutParams(containerParams);
    }

    private class AppListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return allApps.size();
        }

        @Override
        public Object getItem(int position) {
            return allApps.get(position);
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
            
            AppInfo app = allApps.get(position);
            iconView.setImageDrawable(app.getIcon());
            labelView.setText(app.getLabel());
            
            return view;
        }
    }
}
