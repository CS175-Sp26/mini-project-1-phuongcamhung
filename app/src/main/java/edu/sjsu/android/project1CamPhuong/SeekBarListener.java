package edu.sjsu.android.project1CamPhuong;

import android.widget.SeekBar;

public interface SeekBarListener extends SeekBar.OnSeekBarChangeListener{
    @Override
    public default void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }
    @Override
    public default void onStartTrackingTouch(SeekBar seekBar) { }
    @Override
    public default void onStopTrackingTouch(SeekBar seekBar) { }
}
