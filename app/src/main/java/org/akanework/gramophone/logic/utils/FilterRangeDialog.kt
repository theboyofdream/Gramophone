package org.akanework.gramophone.logic.utils

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.slider.RangeSlider
import org.akanework.gramophone.R

object FilterRangeDialog {

    fun showSizeFilter(
        context: Context,
        minBytes: Float,
        maxBytes: Float,
        currentMin: Float,
        currentMax: Float,
        onApply: (min: Float, max: Float) -> Unit,
        onReset: () -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_filter_range, null)
        val title = view.findViewById<TextView>(R.id.filter_title)
        val rangeLabel = view.findViewById<TextView>(R.id.filter_range_label)
        val slider = view.findViewById<RangeSlider>(R.id.range_slider)

        title.text = context.getString(R.string.filter_by_size)
        slider.valueFrom = minBytes
        slider.valueTo = maxBytes
        slider.stepSize = if (maxBytes - minBytes > 1_000_000f) 100_000f else 1_000f
        slider.values = listOf(currentMin.coerceIn(minBytes, maxBytes), currentMax.coerceIn(minBytes, maxBytes))

        fun formatBytes(bytes: Float): String {
            return when {
                bytes >= 1_073_741_824f -> String.format("%.1f GB", bytes / 1_073_741_824f)
                bytes >= 1_048_576f -> String.format("%.1f MB", bytes / 1_048_576f)
                bytes >= 1_024f -> String.format("%.1f KB", bytes / 1_024f)
                else -> "${bytes.toInt()} B"
            }
        }

        fun updateLabel(values: List<Float>) {
            rangeLabel.text = context.getString(R.string.filter_range_label,
                formatBytes(values[0]), formatBytes(values[1]))
        }

        updateLabel(slider.values)
        slider.addOnChangeListener { _, _, _ ->
            updateLabel(slider.values)
        }

        MaterialAlertDialogBuilder(context)
            .setView(view)
            .setPositiveButton(R.string.apply) { _, _ ->
                val values = slider.values
                onApply(values[0], values[1])
            }
            .setNegativeButton(R.string.dismiss, null)
            .setNeutralButton(R.string.reset) { _, _ ->
                onReset()
            }
            .show()
    }

    fun showDurationFilter(
        context: Context,
        minMs: Float,
        maxMs: Float,
        currentMin: Float,
        currentMax: Float,
        onApply: (min: Float, max: Float) -> Unit,
        onReset: () -> Unit
    ) {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_filter_range, null)
        val title = view.findViewById<TextView>(R.id.filter_title)
        val rangeLabel = view.findViewById<TextView>(R.id.filter_range_label)
        val slider = view.findViewById<RangeSlider>(R.id.range_slider)

        title.text = context.getString(R.string.filter_by_duration)
        slider.valueFrom = minMs
        slider.valueTo = maxMs
        slider.stepSize = ((maxMs - minMs) / 100f).coerceAtLeast(1000f)
        slider.values = listOf(currentMin.coerceIn(minMs, maxMs), currentMax.coerceIn(minMs, maxMs))

        fun formatDuration(ms: Float): String {
            val totalSeconds = (ms / 1000).toLong()
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60
            return if (hours > 0) String.format("%d:%02d:%02d", hours, minutes, seconds)
            else String.format("%d:%02d", minutes, seconds)
        }

        fun updateLabel(values: List<Float>) {
            rangeLabel.text = context.getString(R.string.filter_range_label,
                formatDuration(values[0]), formatDuration(values[1]))
        }

        updateLabel(slider.values)
        slider.addOnChangeListener { _, _, _ ->
            updateLabel(slider.values)
        }

        MaterialAlertDialogBuilder(context)
            .setView(view)
            .setPositiveButton(R.string.apply) { _, _ ->
                val values = slider.values
                onApply(values[0], values[1])
            }
            .setNegativeButton(R.string.dismiss, null)
            .setNeutralButton(R.string.reset) { _, _ ->
                onReset()
            }
            .show()
    }
}
