/*
 * Copyright (c) Alexey Barykin 2020. 
 * All rights reserved.
 */

package ru.alexey.patientassistant.dateutils

import org.junit.Assert
import org.junit.Test
import ru.alexey.patientassistant.client.utils.DateUtils

class TestGetDatePlusDaysFunction {

    // DATABASE_DATE_PATTERN = "yyyy-MM-dd"

    @Test
    fun commonTest() {
        val result = DateUtils.getDatePlusDays("2020-04-12", 7)
        val expected = "2020-04-19"
        Assert.assertEquals(expected, result)
    }

    @Test
    fun incDate() {
        val result = DateUtils.getDatePlusDays("2020-04-12", 1)
        val expected = "2020-04-13"
        Assert.assertEquals(expected, result)
    }

    @Test
    fun decDate() {
        val result = DateUtils.getDatePlusDays("2020-04-12", -1)
        val expected = "2020-04-11"
        Assert.assertEquals(expected, result)
    }

    @Test
    fun nullDays() {
        val result = DateUtils.getDatePlusDays("2020-04-12", 0)
        val expected = "2020-04-12"
        Assert.assertEquals(expected, result)
    }
}