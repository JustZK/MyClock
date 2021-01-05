package com.zk.flipclock.func

import com.zk.flipclock.func.BaseFuncImpl

//卡旋转功能
class CardRotateFunc : BaseFuncImpl {

    constructor()

    override fun execute(inParam: Float): Float {
        if (inParam > inParamMax) {
            return outParamMin
        } else if (inParam < inParamMin) {
            return outParamMax
        } else {
            //斜率
            val rate = (outParamMin - outParamMax) / (inParamMax - inParamMin)
            return outParamMax + inParam * rate
        }
    }
}