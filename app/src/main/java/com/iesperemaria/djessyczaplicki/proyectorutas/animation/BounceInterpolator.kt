package com.iesperemaria.djessyczaplicki.proyectorutas.animation

import android.view.animation.Interpolator

class BounceInterpolator(
    amplitude: Double,
    frequency: Double

) : Interpolator {
    private var mAmplitude = 1.0
    private var mFrequency = 10.0
    override fun getInterpolation(time: Float): Float {
        return (-1 * Math.pow(Math.E, - time / mAmplitude) *
                Math.cos(mFrequency * time) + 1).toFloat()
    }

    init {
        mAmplitude = amplitude
        mFrequency = frequency
    }
}