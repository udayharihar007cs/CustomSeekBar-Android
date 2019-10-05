package com.hrh.customseekbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.hrh.customseekbarui.CustomSeekBarView;
import com.hrh.customseekbarui.TickMark;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements CustomSeekBarView.OnCustomSeekBarChangeListener {
    private final String TAG = MainActivity.class.getSimpleName();
    private CustomSeekBarView customSeekBar;
    private final int ID_1 = 1001;
    private final int ID_2 = 1002;
    private final int ID_3 = 1003;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initSeekBar();
        setUpSeekBar();
    }

    private void setUpSeekBar() {
        int width = (int) getResources().getDimension(R.dimen.seek_bar_width);
        int height = (int) getResources().getDimension(R.dimen.seek_bar_height);
        TickMark save = new TickMark(AppCompatResources.getDrawable(this, android.R.drawable.ic_menu_save),
                AppCompatResources.getDrawable(this, android.R.drawable.ic_menu_add),
                ID_1);

        TickMark mic = new TickMark(AppCompatResources.getDrawable(this, android.R.drawable.ic_dialog_alert),
                AppCompatResources.getDrawable(this, android.R.drawable.ic_btn_speak_now),
                ID_2);

        TickMark email = new TickMark(AppCompatResources.getDrawable(this, android.R.drawable.ic_delete),
                AppCompatResources.getDrawable(this, android.R.drawable.ic_dialog_email),
                ID_3);

        ArrayList<TickMark> switchTicksList = new ArrayList<>();

        switchTicksList.add(save);
        switchTicksList.add(mic);
        switchTicksList.add(email);

        customSeekBar.setSwitchTicksList(switchTicksList);
        setCustomSeekBarWidthHight(width, height, 0);
    }

    private void setCustomSeekBarWidthHight(int width, int height, int progress) {
        ViewGroup.LayoutParams lp = customSeekBar.getLayoutParams();
        lp.height = height;
        lp.width = width;
        customSeekBar.setLayoutParams(lp);
        customSeekBar.setProgress(progress);
    }

    private void initSeekBar() {
        customSeekBar = findViewById(R.id.custom_seek_bar);
        customSeekBar.setOnCustomSeekBarChangeListener(this);
    }

    @Override
    public void onSwitchProgressChanged(CustomSeekBarView customSeekBar, int progress, boolean fromUser, TickMark tickMark) {
        Log.d(TAG,"Selected :"+tickMark.toString());
    }

    @Override
    public void onStartTrackingTouch(CustomSeekBarView customSeekBar) {

    }

    @Override
    public void onStopTrackingTouch(CustomSeekBarView customSeekBar) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
