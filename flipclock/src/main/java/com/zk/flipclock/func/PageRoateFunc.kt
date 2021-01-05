package com.zk.flipclock.func

import com.zk.flipclock.func.BaseFuncImpl

//页面漫游功能
class PageRoateFunc : BaseFuncImpl {

    constructor()
    constructor(initValue: Float, inParamMax: Float) : super(initValue, inParamMax)

    override fun execute(inParam: Float): Float {
        val rate = (outParamMax - outParamMin) / (inParamMax - inParamMin)
        val result = rate * inParam + initValue
        if (result in outParamMin..outParamMax) {
            return result
        } else if (result < outParamMin) {
            return outParamMin
        } else {
            return outParamMax
        }
    }

}