package com.gigigo.logger

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.design.widget.FloatingActionButton
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnDragListener
import android.view.ViewGroup

class Logger(activity: Activity, defaultContainer: Int = R.id.container1) {

    init {
        setDebugContentView(activity, defaultContainer)
    }

    @Suppress("DEPRECATION")
    private fun setDebugContentView(
        activity: Activity,
        defaultContainer: Int
    ) {
        val rootView: ViewGroup = activity.findViewById(android.R.id.content)

        val view = LayoutInflater.from(activity)
            .inflate(
                R.layout.activity_logger_base,
                rootView,
                true
            )

        val button = FloatingActionButton(activity).apply {
            this.setImageResource(R.drawable.ic_bug_report_black_24dp)
        }

        button.setOnClickListener {
            activity.startActivity(Intent(activity, LogActivity::class.java))
        }

        button.setOnLongClickListener { fab ->
            val shadowBuilder = View.DragShadowBuilder(fab)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                fab.startDragAndDrop(null, shadowBuilder, fab, 0)
            } else {
                fab.startDrag(null, shadowBuilder, fab, 0)
            }

            fab.visibility = View.INVISIBLE
            return@setOnLongClickListener true
        }

        view.findViewById<ViewGroup>(defaultContainer).addView(button)

        view.findViewById<View>(R.id.container1).setOnDragListener(MyDragListener())
        view.findViewById<View>(R.id.container2).setOnDragListener(MyDragListener())
        view.findViewById<View>(R.id.container3).setOnDragListener(MyDragListener())
        view.findViewById<View>(R.id.container4).setOnDragListener(MyDragListener())

    }

    internal inner class MyDragListener : OnDragListener {

        override fun onDrag(v: View, event: DragEvent): Boolean {
            when (event.action) {
                DragEvent.ACTION_DRAG_STARTED -> {
                    // do nothing
                }
                DragEvent.ACTION_DRAG_ENTERED -> {
                    // do nothing
                }
                DragEvent.ACTION_DRAG_EXITED -> {
                    // do nothing
                }
                DragEvent.ACTION_DROP -> {
                    // Dropped, reassign View to ViewGroup
                    val view = event.localState as View
                    val owner = view.parent as ViewGroup
                    owner.removeView(view)
                    val container = v as ViewGroup
                    container.addView(view)
                    view.visibility = View.VISIBLE
                }
                else -> {
                    // do nothing
                }
            }
            return true
        }
    }
}
