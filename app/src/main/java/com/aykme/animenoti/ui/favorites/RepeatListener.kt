package com.aykme.animenoti.ui.favorites

import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.View

class RepeatListener(
    initialInterval: Int,
    private val repeatInterval: Int,
    private val clickListener: View.OnClickListener
) : View.OnTouchListener {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var handlerRunnable: Runnable
    private lateinit var touchedView: View

    init {
        if (initialInterval < 0 || repeatInterval < 0)
            throw IllegalArgumentException("Incorrect repeat interval")

        handlerRunnable = Runnable {
            if (touchedView.isPressed) {
                handler.postDelayed(handlerRunnable, repeatInterval.toLong())
                clickListener.onClick(touchedView)
            } else {
                handler.removeCallbacks(handlerRunnable)
                touchedView.isPressed = false
            }
        }
    }

    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        if (view == null || motionEvent == null) {
            handler.removeCallbacks(handlerRunnable)
            return false
        } else {
            touchedView = view
        }
        return when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {
                handler.removeCallbacks(handlerRunnable)
                handler.postDelayed(handlerRunnable, repeatInterval.toLong())
                touchedView.isPressed = true
                clickListener.onClick(view)
                true
            }
            MotionEvent.ACTION_UP -> {
                handler.removeCallbacks(handlerRunnable)
                touchedView.isPressed = false
                true
            }
            else -> false
        }
    }
}
