package com.zk.clock.activity

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.zk.clock.R
import com.zk.clock.adapter.FragmentAdapter
import com.zk.clock.databinding.ActivityMainBinding
import com.zk.clock.fragment.AnalogClockFragment
import com.zk.clock.fragment.DigitalClockFragment
import com.zk.clock.fragment.FlipClockFragment
import com.zk.common.utils.LogUtil
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    val timer = Timer()
    val mUpTimeListener = ArrayList<OnUpTimeListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding: ActivityMainBinding = DataBindingUtil.setContentView(this,
            R.layout.activity_main)
        LogUtil.instance.logSwitch = true
        val typeFace = Typeface.createFromAsset(assets, "fonts/096-CAI978.ttf")
//        binding.mainClock.typeface = typeFace


        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                if (mUpTimeListener.size > 0){
                    val c = Calendar.getInstance() //
                    val year = c[Calendar.YEAR] // 获取当前年份
                    val month = c[Calendar.MONTH] + 1 // 获取当前月份
                    val day = c[Calendar.DAY_OF_MONTH] // 获取当日期
                    val week = c[Calendar.DAY_OF_WEEK] // 获取当前日期的星期
                    val hour = c[Calendar.HOUR_OF_DAY] //时
                    val minute = c[Calendar.MINUTE] //分
                    val second = c[Calendar.SECOND] + 1 //秒
                    val amOrPm = c[Calendar.AM_PM]
                    LogUtil.instance.d("year$year month$month day$day week$week hour$hour minute$minute second$second amOrPm$amOrPm")
                    for (upTimeListener in mUpTimeListener){
                        upTimeListener.nowTime(year, month, day, week, hour, minute, second, amOrPm)
                    }
                }
            }
        }
        timer.schedule(timerTask, 0, 1000)

        val clockPagerAdapter = FragmentAdapter(this, supportFragmentManager)
        val analogClockFragment = AnalogClockFragment()
        val digitalClockFragment = DigitalClockFragment()
        val flipClockFragment = FlipClockFragment()
        clockPagerAdapter.addFragment(flipClockFragment, flipClockFragment.title)
        clockPagerAdapter.addFragment(digitalClockFragment, digitalClockFragment.title)
        clockPagerAdapter.addFragment(analogClockFragment, analogClockFragment.title)
        binding.mainVp.adapter = clockPagerAdapter
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        LogUtil.instance.d("hasFocus:$hasFocus")
        if (hasFocus) hideSystemUI()
//        else showSystemUI()
    }

    private fun hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private fun showSystemUI() {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    fun addOnUpTimeListener(upTimeListener: OnUpTimeListener){
        this.mUpTimeListener.add(upTimeListener)
    }

    interface OnUpTimeListener {
        fun nowTime(year: Int,
                    month: Int,
                    day: Int,
                    week: Int,
                    hour: Int,
                    minute: Int,
                    second: Int,
                    amOrPm: Int)
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }
}
