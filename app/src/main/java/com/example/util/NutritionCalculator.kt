package com.example.util

import kotlin.math.roundToInt

object NutritionCalculator {

    data class NutritionTargets(
        val calories: Int,
        val proteinGrams: Int,
        val carbsGrams: Int,
        val fatGrams: Int,
        val waterMl: Int,
        val isTeenager: Boolean,
        val healthMessageEn: String,
        val healthMessageBn: String
    )

    /**
     * Calculates healthy, non-restrictive daily nutritional targets.
     * Uses Mifflin-St Jeor formula with a balanced lifestyle multiplier.
     * Never applies restrictive deficits to teenagers or extreme deficits to adults.
     */
    fun calculateTargets(
        age: Int,
        heightCm: Double,
        weightKg: Double,
        activityLevel: String,
        dietaryPreference: String = "standard"
    ): NutritionTargets {
        val isTeen = age in 13..18

        // Safe baseline calculation
        val safeWeight = if (weightKg in 30.0..250.0) weightKg else 65.0
        val safeHeight = if (heightCm in 100.0..250.0) heightCm else 170.0
        val safeAge = if (age in 10..110) age else 25

        // Approximate BMR (Mifflin-St Jeor baseline average)
        val bmr = 10 * safeWeight + 6.25 * safeHeight - 5 * safeAge + 5

        val activityMultiplier = when (activityLevel.lowercase()) {
            "sedentary" -> 1.25
            "lightly_active" -> 1.375
            "moderately_active" -> 1.55
            "very_active" -> 1.725
            else -> 1.4
        }

        var maintenanceCalories = (bmr * activityMultiplier).roundToInt()

        // Protect teenagers from extreme or restrictive targets
        val healthMessageEn: String
        val healthMessageBn: String

        if (isTeen) {
            // Guarantee adequate growth nutrition (at least 2000-2400 kcal)
            if (maintenanceCalories < 2000) maintenanceCalories = 2000
            healthMessageEn = "Nourish your body for growth and healthy vitality. Focus on whole foods and balanced meals."
            healthMessageBn = "শারীরিক বৃদ্ধি ও সতেজতার জন্য সুষম খাবার খান। শাক-সবজি, মাছ-মাংস ও পুষ্টিকর খাবার গ্রহণ করুন।"
        } else {
            if (maintenanceCalories < 1600) maintenanceCalories = 1600
            healthMessageEn = "Eat balanced meals to maintain active energy and long-term wellbeing."
            healthMessageBn = "দীর্ঘমেয়াদী সুস্বাস্থ্যের জন্য প্রতিদিন পুষ্টিকর ও সুষম খাবার উপভোগ করুন।"
        }

        // Balanced macro split: 20% protein, 50% carbs, 30% healthy fats
        val proteinCalories = maintenanceCalories * 0.20
        val carbsCalories = maintenanceCalories * 0.50
        val fatCalories = maintenanceCalories * 0.30

        val proteinGrams = (proteinCalories / 4).roundToInt()
        val carbsGrams = (carbsCalories / 4).roundToInt()
        val fatGrams = (fatCalories / 9).roundToInt()

        // Daily water target: approx 35 ml per kg, minimum 2000ml, max 3800ml
        val calculatedWater = (safeWeight * 35).roundToInt()
        val safeWaterMl = calculatedWater.coerceIn(2000, 3500)

        return NutritionTargets(
            calories = maintenanceCalories,
            proteinGrams = proteinGrams,
            carbsGrams = carbsGrams,
            fatGrams = fatGrams,
            waterMl = safeWaterMl,
            isTeenager = isTeen,
            healthMessageEn = healthMessageEn,
            healthMessageBn = healthMessageBn
        )
    }
}
