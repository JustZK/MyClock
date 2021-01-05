package com.zk.flipclock.func

import com.zk.flipclock.func.BaseFuncImpl

//阴影大小功能
class ShadowSizeFunc : BaseFuncImpl {

    constructor()

    override fun execute(inParam: Float): Float {
        val rate = (outParamMax - outParamMin) / (inParamMax / 2 - inParamMin)
        val result = rate * inParam * 2 + initValue
        if (result in outParamMin..outParamMax) {
            return result
        } else if (result < outParamMin) {
            return outParamMin
        } else {
            return outParamMax
        }
    }
}