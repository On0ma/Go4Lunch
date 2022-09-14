package com.onoma.go4lunch.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.onoma.go4lunch.R;
import com.onoma.go4lunch.databinding.ActivitySettingsBinding;
import com.onoma.go4lunch.ui.viewModel.UserViewModel;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private UserViewModel mUserViewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.settingsActivityToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUserViewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(UserViewModel.class);

        SwitchMaterial switchNotification = (SwitchMaterial) binding.settingsSwitchButton;
        Context context = getApplicationContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);

        boolean notificationChoice = sharedPreferences.getBoolean(getString(R.string.notifcations_choice_key), true);

        if (!notificationChoice) {
            switchNotification.setChecked(false);
            switchNotification.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
            switchNotification.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.text_secondary)));
        } else {
            switchNotification.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));
            switchNotification.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent_lighter)));
        }

        switchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.notifcations_choice_key), true);
                    editor.apply();
                    switchNotification.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent)));
                    switchNotification.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.accent_lighter)));
                } else {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(getString(R.string.notifcations_choice_key), false);
                    editor.apply();
                    switchNotification.setThumbTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                    switchNotification.setTrackTintList(ColorStateList.valueOf(getResources().getColor(R.color.text_secondary)));
                }
            }
        });

        /*binding.settingsDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onCreateDialog().show();
            }
        });*/
    }

    public Dialog onCreateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.setting_delete_popup)
                .setTitle(R.string.setting_delete_popup_title)
                .setPositiveButton(R.string.settings_delete_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mUserViewModel.deleteUser(getApplicationContext()).addOnSuccessListener(aVoid -> {
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }).addOnFailureListener(aVoid -> {
                           Log.e("Delete user error:", "Error deleting account");
                        });
                    }
                })
                .setNegativeButton(R.string.settings_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        return builder.create();
    }
}
