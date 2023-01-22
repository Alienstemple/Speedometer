package com.example.speedometer

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AccelerateInterpolator
import android.widget.SeekBar
import com.example.speedometer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mainBinding: ActivityMainBinding
    private var speedAnim = ObjectAnimator()

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


//            speedAnim = AnimatorInflater.loadAnimator(this@MainActivity, R.animator.custom_view_animator) // TODO cannot cast Animator Set to ObjectAnimator
//            speedAnim.target = speedometerView

                mainBinding.animateBtn.setOnClickListener {

                AnimatorSet().apply {

                    val alpha = ObjectAnimator.ofFloat(speedometerView, "alpha", 0.0f , 1.0f)
                    val translation = ObjectAnimator.ofFloat(speedometerView, "translationY", 300.0f , 0.0f)
                    playTogether(alpha, translation)
                    duration = 2000
                    interpolator = AccelerateInterpolator()
                    start()
                }
            }
        }


    }
//
//    override fun onStart() {
//        super.onStart()
//        speedAnim.start()
//    }
//
//    override fun onStop() {
//        super.onStop()
//        speedAnim.end()
//    }
}