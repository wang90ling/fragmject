package com.example.fragment.project.data

import androidx.compose.ui.graphics.Color
import com.example.fragment.project.AppTheme

data class NavigationItem(
    val label: String,
    val resId: Int,
    val selectedColor: Color = AppTheme.orange,
    val unselectedColor: Color = AppTheme.theme
)