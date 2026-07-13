package com.example.fragment.project.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

/**
 * 将时间戳或 ISO 格式时间转换为相对时间字符串
 * 显示规则：刚刚、x分钟前、x小时前、x天前、x个月前、x年前
 *
 * @param time 可以是毫秒时间戳（Long）或 ISO 格式时间字符串（String）
 */
fun relativeTime(time: Any): String {
    val timestamp = try {
        when (time) {
            is Long -> time
            is String -> parseTimestamp(time)
            else -> return ""
        }
    } catch (e: Exception) {
        return ""
    }

    if (timestamp <= 0) return ""

    val now = System.currentTimeMillis()
    val diff = now - timestamp

    if (diff < 0) return "刚刚"

    val seconds = TimeUnit.MILLISECONDS.toSeconds(diff)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    val months = days / 30
    val years = days / 365

    return when {
        seconds < 60 -> "刚刚"
        minutes < 60 -> "${minutes}分钟前"
        hours < 24 -> "${hours}小时前"
        days < 30 -> "${days}天前"
        months < 12 -> "${months}个月前"
        else -> "${years}年前"
    }
}

/**
 * 将 ISO 格式时间字符串解析为毫秒时间戳
 */
private fun parseTimestamp(timeStr: String): Long {
    if (timeStr.isBlank()) return 0L

    val trimmed = timeStr.trim()

    if (trimmed.all { it.isDigit() }) {
        return if (trimmed.length >= 13) {
            trimmed.toLong()
        } else {
            trimmed.toLong() * 1000
        }
    }

    val formats = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ssZ",
        "yyyy-MM-dd HH:mm:ss",
        "yyyy/MM/dd HH:mm:ss",
        "yyyy-MM-dd",
        "yyyy/MM/dd"
    )

    for (format in formats) {
        try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.isLenient = true
            val date = sdf.parse(trimmed)
            if (date != null) {
                return date.time
            }
        } catch (_: Exception) {
        }
    }

    return try {
        Date(timeStr).time
    } catch (_: Exception) {
        0L
    }
}

/**
 * 格式化时间为指定格式
 */
fun formatTime(time: Any, pattern: String = "yyyy-MM-dd HH:mm:ss"): String {
    val timestamp = try {
        when (time) {
            is Long -> time
            is String -> parseTimestamp(time)
            else -> return ""
        }
    } catch (e: Exception) {
        return ""
    }

    if (timestamp <= 0) return ""

    return try {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}

/**
 * 格式化数字显示（超过万显示为x.x万）
 */
fun formatCount(count: Int): String {
    return when {
        count < 0 -> "0"
        count < 10000 -> count.toString()
        count < 100000000 -> String.format("%.1f万", count / 10000.0)
        else -> String.format("%.1f亿", count / 100000000.0)
    }
}
