package ru.poas.patientassistant.client.utils

fun parseServerContent(content: String): String = content.replace('|', '\n')
