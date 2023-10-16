/*
 * Created by simonpojok on 16/10/2023, 20:05
 * Copyright (c) 2023 . All rights reserved.
 * Last modified 16/10/2023, 20:05
 */

package com.nphausg.app.embeddedserver.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.nphausg.app.embeddedserver.R


sealed class LogMessage(open val message: String) {
    data class Error(override val message: String) : LogMessage(message)
    data class Success(override val message: String) : LogMessage(message)
    data class Normal(override val message: String) : LogMessage(message)
}

class LogAdapter : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {
    private var logs: List<LogMessage> = emptyList()

    fun submitLogs(itemLogs: List<LogMessage>) {
        logs = itemLogs
        notifyDataSetChanged()
    }

    class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.log_text)
        fun bindLogItem(log: LogMessage) {
            textView.text = log.message
            val color = when(log) {
                is LogMessage.Normal -> R.color.white
                is LogMessage.Error -> R.color.red
                is LogMessage.Success -> R.color.green
            }
            textView.setTextColor(itemView.resources.getColor(color))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_view_log, parent, false)
        return LogViewHolder(view)
    }

    override fun getItemCount() = logs.size

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bindLogItem(logs[position])
    }
}