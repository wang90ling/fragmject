package com.example.fragment.project.utils

import java.util.concurrent.TimeUnit

/**
 * 时间格式化工具类
 * 将时间戳转换为相对时间字符串
 */
object TimeUtils {

    fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = now - timestamp

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

    fun formatCount(count: Int): String {
        return when {
            count < 1000 -> count.toString()
            count < 10000 -> String.format("%.1fk", count / 1000.0)
            else -> String.format("%d万", count / 10000)
        }
    }
}