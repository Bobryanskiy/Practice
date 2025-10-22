package com.github.bobryanskiy.practice.domain.model

enum class Difficulty(val label: String, val tag: String) {
    BEGINNER("Новичок", "beginner"),
    INTERMEDIATE("Средний", "intermediate"),
    ADVANCED("Продвинутый", "advanced")
}