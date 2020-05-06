/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant.client.utils

fun parseServerContent(content: String): String = content.replace('|', '\n')
