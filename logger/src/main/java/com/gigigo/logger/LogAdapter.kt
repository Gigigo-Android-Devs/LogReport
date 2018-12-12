package com.gigigo.logger

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.gigigo.logger.MessageType.VERBOSE

class LogAdapter : RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    private var backupList = mutableListOf<LogMessage>()
    private var list = mutableListOf<LogMessage>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.view_logger_log_line,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position])
    }

    fun clear() {
        list.clear()
        notifyDataSetChanged()
    }

    fun add(buffer: String?) {
        buffer?.let {
            val message = LogMessage(it.messageType(), it)
            list.add(message)
            backupList.add(message)
            notifyItemInserted(list.size - 1)
        }
    }

    fun filterBy(typePosition: Int) {
        val filter = try {
            MessageType.values()[typePosition.toString().toInt()]
        } catch (error: NumberFormatException) {
            MessageType.VERBOSE
            list = backupList
        }

        list.apply {
            clear()
            addAll(backupList.filter {
                when (filter) {
                    VERBOSE -> true
                    else -> it.type == filter
                }
            })
        }

        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val logTextView = view.findViewById<TextView>(R.id.logTextView)
        fun bind(line: LogMessage) {
            logTextView.setTextColor(line.type.color())
            logTextView.text = line.message
        }
    }
}