package com.example.h6launcher;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.Toast;

public class SettingsActivity extends Activity {
    private ConfigManager configManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        configManager = H6LauncherApp.getInstance().getConfigManager();
        
        RadioGroup dockPositionGroup = findViewById(R.id.dock_position_group);
        RadioGroup splitModeGroup = findViewById(R.id.split_mode_group);
        
        int currentDockPosition = configManager.getDockPosition();
        int currentSplitMode = configManager.getSplitMode();
        
        dockPositionGroup.check(
            currentDockPosition == ConfigManager.DOCK_POSITION_LEFT 
                ? R.id.dock_left : R.id.dock_bottom
        );
        
        splitModeGroup.check(
            currentSplitMode == ConfigManager.SPLIT_MODE_2 
                ? R.id.split_2 : R.id.split_3
        );
        
        dockPositionGroup.setOnCheckedChangeListener((group, checkedId) -> {
            MainActivity launcher = (MainActivity) getParent();
            if (launcher != null) {
                launcher.setDockPosition(
                    checkedId == R.id.dock_left 
                        ? ConfigManager.DOCK_POSITION_LEFT 
                        : ConfigManager.DOCK_POSITION_BOTTOM
                );
            }
        });
        
        splitModeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            MainActivity launcher = (MainActivity) getParent();
            if (launcher != null) {
                launcher.setSplitMode(
                    checkedId == R.id.split_2 
                        ? ConfigManager.SPLIT_MODE_2 
                        : ConfigManager.SPLIT_MODE_3
                );
            }
        });
        
        Button saveConfig1Btn = findViewById(R.id.btn_save_config_1);
        Button saveConfig2Btn = findViewById(R.id.btn_save_config_2);
        Button saveConfig3Btn = findViewById(R.id.btn_save_config_3);
        Button loadConfig1Btn = findViewById(R.id.btn_load_config_1);
        Button loadConfig2Btn = findViewById(R.id.btn_load_config_2);
        Button loadConfig3Btn = findViewById(R.id.btn_load_config_3);
        
        saveConfig1Btn.setOnClickListener(v -> saveConfig(0));
        saveConfig2Btn.setOnClickListener(v -> saveConfig(1));
        saveConfig3Btn.setOnClickListener(v -> saveConfig(2));
        loadConfig1Btn.setOnClickListener(v -> loadConfig(0));
        loadConfig2Btn.setOnClickListener(v -> loadConfig(1));
        loadConfig3Btn.setOnClickListener(v -> loadConfig(2));
        
        updateConfigButtons();
    }
    
    private void saveConfig(int index) {
        MainActivity launcher = (MainActivity) getParent();
        if (launcher != null) {
            launcher.saveCurrentConfig(index);
            Toast.makeText(this, getString(R.string.config_saved), Toast.LENGTH_SHORT).show();
            updateConfigButtons();
        }
    }
    
    private void loadConfig(int index) {
        MainActivity launcher = (MainActivity) getParent();
        if (launcher != null) {
            launcher.loadConfig(index);
            Toast.makeText(this, getString(R.string.config_loaded), Toast.LENGTH_SHORT).show();
        }
    }
    
    private void updateConfigButtons() {
        Button loadConfig1Btn = findViewById(R.id.btn_load_config_1);
        Button loadConfig2Btn = findViewById(R.id.btn_load_config_2);
        Button loadConfig3Btn = findViewById(R.id.btn_load_config_3);
        
        loadConfig1Btn.setEnabled(configManager.hasConfig(0));
        loadConfig2Btn.setEnabled(configManager.hasConfig(1));
        loadConfig3Btn.setEnabled(configManager.hasConfig(2));
    }
    
    public void onClose(View view) {
        finish();
    }
}
