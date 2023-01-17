package com.example.speedometer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import com.example.speedometer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        mainBinding.changePaddingBtn.setOnClickListener {
            mainBinding.speedometerView.setPadding(0, 0, 0, 0)
            mainBinding.speedometerView.requestLayout()
        }

        with(mainBinding) {
            speedometerSeekbar.max = speedometerView.max
            speedometerSeekbar.progress = speedometerView.speedProgress
            speedometerSeekbar.setOnSeekBarChangeListener(object :
                SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    speedometerView.setProgress(p1)
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })
        }
    }
}