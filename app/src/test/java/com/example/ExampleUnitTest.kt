package com.example

import com.example.data.datasource.BangladeshiFoodDatabase
import com.example.util.NutritionCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun `test bangladeshi food database contains traditional items`() {
        val allFoods = BangladeshiFoodDatabase.items
        assertTrue("Database should have items", allFoods.isNotEmpty())

        val rice = allFoods.find { it.id == "rice_plain" }
        assertTrue("Plain rice must exist", rice != null)
        assertEquals("সাদা ভাত", rice?.nameBn)

        val khichuri = allFoods.find { it.id == "trad_bhuna_khichuri" }
        assertTrue("Bhuna Khichuri must exist", khichuri != null)
        assertEquals("ভুনা খিচুড়ি", khichuri?.nameBn)
    }

    @Test
    fun `test bangladeshi food search in english and bengali`() {
        val searchRice = BangladeshiFoodDatabase.search("Rice")
        assertFalse("Search 'Rice' should return results", searchRice.isEmpty())

        val searchBengali = BangladeshiFoodDatabase.search("ভাত")
        assertFalse("Search 'ভাত' should return results", searchBengali.isEmpty())

        val searchTehari = BangladeshiFoodDatabase.search("তেহারি")
        assertFalse("Search 'তেহারি' should return results", searchTehari.isEmpty())
    }

    @Test
    fun `test nutrition calculator protects teenagers from restrictive targets`() {
        // Age 15 teenager
        val targets = NutritionCalculator.calculateTargets(
            age = 15,
            heightCm = 165.0,
            weightKg = 55.0,
            activityLevel = "sedentary"
        )
        assertTrue("Teenager should be identified", targets.isTeenager)
        assertTrue("Teenager calories should not be restricted below 2000", targets.calories >= 2000)
    }

    @Test
    fun `test nutrition calculator balanced macro distribution for adult`() {
        val targets = NutritionCalculator.calculateTargets(
            age = 28,
            heightCm = 175.0,
            weightKg = 70.0,
            activityLevel = "moderately_active"
        )
        assertFalse("Adult is not a teenager", targets.isTeenager)
        assertTrue("Calories should be realistic", targets.calories in 1800..3200)
        assertTrue("Protein should be calculated", targets.proteinGrams > 50)
        assertTrue("Water target should be healthy", targets.waterMl in 2000..3500)
    }
}
