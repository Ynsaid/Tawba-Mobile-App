package com.example.tawbaapp.classes

data class AdhkarCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconRes: Int,
    val colorRes: androidx.compose.ui.graphics.Color,
    val type: AdhkarType
)

enum class AdhkarType {
    ADHKAR,
    DUA
}