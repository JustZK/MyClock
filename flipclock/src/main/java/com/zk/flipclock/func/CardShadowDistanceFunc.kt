package com.zk.flipclock.func

import com.zk.flipclock.func.BaseFuncImpl

//卡影距离功能
class CardShadowDistanceFunc : BaseFuncImpl {

    constructor()

    /**
     * @param inParam 当前card的翻转角度
     */
    override fun execute(inParam: Float): Float {
        if (inParam < inParamMin) {
            return 10F
        } else if (inParam > inParamMax) {
            return outParamMin
        } else if (inParam >= 0F && inParam < 90F) {
            return 10F + inParam * (outParamMax - 10F) / 90F
        } else {
            return outParamMax + (inParam - 90F) * (outParamMin - outParamMax) / 90F
        }
    }

}