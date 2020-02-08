package com.zk.clock.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.zk.clock.R
import com.zk.clock.activity.MainActivity
import com.zk.clock.activity.MainActivity.OnUpTimeListener
import com.zk.clock.databinding.FragmentFlipClockBinding
import java.lang.ref.WeakReference
import java.util.*

class FlipClockFragment : Fragment(), OnUpTimeListener {
    var mHour = 0
    var mMinute = 0
    var mSecond = 0
    val title: String = "FlipClock"
    companion object {
        const val UP_TIME: Int = 0x01
    }
    private val mHandler = FlipClockFragmentHandler(this)
    var mBinding: FragmentFlipClockBinding? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.inflate<FragmentFlipClockBinding>(
            inflater,
            R.layout.fragment_flip_clock,
            container,
            false
        )
        val c = Calendar.getInstance() //
        val year = c[Calendar.YEAR] // 获取当前年份
        val month = c[Calendar.MONTH] + 1 // 获取当前月份
        val day = c[Calendar.DAY_OF_MONTH] // 获取当日期
        val week = c[Calendar.DAY_OF_WEEK] // 获取当前日期的星期
        mHour = c[Calendar.HOUR_OF_DAY] //时
        mMinute = c[Calendar.MINUTE] //分
        mSecond = c[Calendar.SECOND] //秒
        val amOrPm = c[Calendar.AM_PM]
        mBinding!!.fragmentFlipClockH.setFirstTextValue(mHour.toString())
        mBinding!!.fragmentFlipClockM.setFirstTextValue(mMinute.toString())
        mBinding!!.fragmentFlipClockS.setFirstTextValue(mSecond.toString())
        val mainActivity = activity as MainActivity
        mainActivity.addOnUpTimeListener(this)
        return mBinding!!.root
    }

    override fun nowTime(
        year: Int,
        month: Int,
        day: Int,
        week: Int,
        hour: Int,
        minute: Int,
        second: Int,
        amOrPm: Int
    ) {
        val bundle = Bundle()
        bundle.putInt("hour", hour)
        bundle.putInt("minute", minute)
        bundle.putInt("second", second)
        val message = Message.obtain()
        message.what = UP_TIME
        message.data = bundle
        mHandler.sendMessage(message)
    }

    private class FlipClockFragmentHandler : Handler {
        private var flipClockFragmentWeakReference: WeakReference<FlipClockFragment>? = null

        constructor(flipClockFragment: FlipClockFragment) : super() {
            flipClockFragmentWeakReference = WeakReference(flipClockFragment)
        }

        override fun handleMessage(msg: Message) {
            if (flipClockFragmentWeakReference != null) {
                flipClockFragmentWeakReference!!.get()!!.handleMessage(msg)
            }
        }
    }

    private fun handleMessage(msg: Message) {
        when(msg.what){
            UP_TIME ->{
                val bundle = msg.data
                val hour = bundle.getInt("hour")
                val minute = bundle.getInt("minute")
                val second = bundle.getInt("second")

                if (mSecond != second){
                    mSecond = second
                    mBinding!!.fragmentFlipClockS.setTextValue(mSecond.toString())
                    mBinding!!.fragmentFlipClockS.smoothFlip()
                }
                if(mMinute != minute){
                    mMinute = minute
                    mBinding!!.fragmentFlipClockM.setTextValue(mMinute.toString())
                    mBinding!!.fragmentFlipClockM.smoothFlip()
                }
                if(mHour != hour) {
                    mHour = hour
                    mBinding!!.fragmentFlipClockH.setTextValue(mHour.toString())
                    mBinding!!.fragmentFlipClockH.smoothFlip()
                }



            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }
}
