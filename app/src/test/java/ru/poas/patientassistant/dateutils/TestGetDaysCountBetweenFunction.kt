/*
 * Copyright (c) Alexey Barykin 2020.
 * All rights reserved.
 */

package ru.poas.patientassistant.dateutils

import org.junit.Assert
import org.junit.Test
import ru.poas.patientassistant.client.utils.DateUtils

class TestGetDaysCountBetweenFunction {

    // DATABASE_DATE_PATTERN = "yyyy-MM-dd"

    @Test
    fun commonTest() {
        val result = DateUtils.getDaysCountBetween("2020-04-12", "2020-04-13")
        val expected = 1
        Assert.assertEquals(expected, result)
    }

    @Test
    fun datesAreEqual() {
        val result = DateUtils.getDaysCountBetween("2020-04-12", "2020-04-12")
        val expected = 0
        Assert.assertEquals(expected, result)
    }

    @Test
    fun secondDateIsLower() {
        val result = DateUtils.getDaysCountBetween("2020-04-12", "2020-04-10")
        val expected = -2
        Assert.assertEquals(expected, result)
    }

    @Test
    fun weekRange() {
        val result = DateUtils.getDaysCountBetween("2020-04-12", "2020-04-19")
        val expected = 7
        Assert.assertEquals(expected, result)
    }
}