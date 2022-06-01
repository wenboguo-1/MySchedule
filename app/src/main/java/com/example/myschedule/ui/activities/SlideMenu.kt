package com.example.myschedule.ui.activities

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.Scroller

class SlideMenu : FrameLayout {
    private var menuView: View? = null
    private var mainView: View? = null
    private var menuWidth = 0
    private var scroller: Scroller? = null

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    private fun init() {
        scroller = Scroller(context)
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        menuView = getChildAt(0)
        mainView = getChildAt(1)
        menuWidth = menuView?.layoutParams?.width!!
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        menuView?.layout(-menuWidth, 0, 0, b)
        mainView?.layout(0, 0, r, b)
    }

    private var downX = 0
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> downX = event.x.toInt()
            MotionEvent.ACTION_MOVE -> {
                val moveX = event.x.toInt()
                val deltaX = moveX - downX
                var newScrollX = scrollX - deltaX
                if (newScrollX < -menuWidth) newScrollX = -menuWidth
                if (newScrollX > 0) newScrollX = 0
                scrollTo(newScrollX, 0)
                downX = moveX
            }
            MotionEvent.ACTION_UP ->                 //当滑动距离小于Menu宽度的一半时，平滑滑动到主页面
                if (scrollX > -menuWidth / 2) {
                    closeMenu()
                } else {
                    //当滑动距离大于Menu宽度的一半时，平滑滑动到Menu页面
                    openMenu()
                }
        }
        return true
    }

    //关闭menu
    private fun closeMenu() {
        scroller!!.startScroll(scrollX, 0, 0 - scrollX, 0, 400)
        invalidate()
    }

    private fun openMenu() {
        scroller!!.startScroll(scrollX, 0, -menuWidth - scrollX, 0, 400)
        invalidate()
    }

    override fun computeScroll() {
        super.computeScroll()
        if (scroller!!.computeScrollOffset()) {
            scrollTo(scroller!!.currX, 0)
            invalidate()
        }
    }

    fun switchMenu() {

        if (scrollX == 0) {
            openMenu()
            Log.e("heihei","xiei")
        } else {
            closeMenu()

        }
    }
}