package com.example.util

object LanguageManager {
    const val LANG_EN = "en"
    const val LANG_BN = "bn"

    fun getText(key: String, lang: String): String {
        val isBn = lang == LANG_BN
        return when (key) {
            "app_title" -> if (isBn) "আবির ক্যাল এআই" else "ABIR CAL AI"
            "tagline" -> if (isBn) "স্মার্ট খাবার। সঠিক ট্র্যাকিং। সুস্থ জীবন।" else "Eat Smart. Track Better. Live Healthy."
            "home" -> if (isBn) "হোম" else "Home"
            "diary" -> if (isBn) "ডায়েরি" else "Diary"
            "scan" -> if (isBn) "স্ক্যান" else "Scan"
            "assistant" -> if (isBn) "এআই চ্যাট" else "Ask AI"
            "progress" -> if (isBn) "অগ্রগতি" else "Progress"
            "profile" -> if (isBn) "প্রোফাইল" else "Profile"
            "admin_panel" -> if (isBn) "অ্যাডমিন প্যানেল" else "Admin Panel"

            "breakfast" -> if (isBn) "সকালের নাস্তা" else "Breakfast"
            "lunch" -> if (isBn) "দুপুরের খাবার" else "Lunch"
            "dinner" -> if (isBn) "রাতের খাবার" else "Dinner"
            "snack" -> if (isBn) "বিকেলের নাস্তা" else "Snacks"

            "calories" -> if (isBn) "ক্যালোরি" else "Calories"
            "protein" -> if (isBn) "প্রোটিন" else "Protein"
            "carbs" -> if (isBn) "কার্বস" else "Carbs"
            "fat" -> if (isBn) "ফ্যাট" else "Fat"
            "consumed" -> if (isBn) "গ্রহণ করা হয়েছে" else "Consumed"
            "target" -> if (isBn) "দৈনিক লক্ষ্য" else "Target"
            "remaining" -> if (isBn) "অবশিষ্ট" else "Remaining"
            "water_tracker" -> if (isBn) "পানি পান ট্র্যাকার" else "Water Tracker"

            "add_food" -> if (isBn) "খাবার যোগ করুন" else "Add Food"
            "scan_food" -> if (isBn) "খাবার স্ক্যান করুন" else "Scan Food"
            "search_food" -> if (isBn) "খাবার খুঁজুন" else "Search Food"
            "add_water" -> if (isBn) "পানি যোগ করুন" else "Add Water"
            "ask_ai" -> if (isBn) "এআই কে জিজ্ঞাসা করুন" else "Ask AI"

            "empty_diary_msg" -> if (isBn) "আজকের কোনো খাবার যোগ করা হয়নি।" else "No meals logged for today yet."
            "empty_diary_sub" -> if (isBn) "খাবারের ছবি স্ক্যান করে শুরু করুন।" else "Scan your food to get started."
            "empty_history" -> if (isBn) "এই দিনে কোনো তথ্য লিপিবদ্ধ করা হয়নি।" else "No entries recorded for this date."

            "disclaimer_ai" -> if (isBn) "এআই প্রদর্শিত পুষ্টিমান আনুমানিক। সঠিক পরিমাপের জন্য পরিমাণ পরিবর্তন করতে পারেন।" else "AI-generated nutrition values are estimates, not exact measurements."
            "save_to_diary" -> if (isBn) "ডায়েরিতে সংরক্ষণ করুন" else "Save to Diary"
            "cancel" -> if (isBn) "বাতিল" else "Cancel"
            "edit" -> if (isBn) "সম্পাদনা" else "Edit"
            "delete" -> if (isBn) "মুছে ফেলুন" else "Delete"

            "search_placeholder" -> if (isBn) "ভাত, তেহারি, ডাল, ডিম খুঁজুন..." else "Search rice, tehari, dal, egg..."
            "all_categories" -> if (isBn) "সব খাবার" else "All Foods"
            "recent_meals" -> if (isBn) "সাম্প্রতিক খাবার" else "Recent Meals"
            "meal_planner" -> if (isBn) "মিল প্ল্যানার" else "Meal Planner"
            "custom_meal" -> if (isBn) "নতুন খাবার তৈরি" else "Custom Meal"
            "save_favorite" -> if (isBn) "পছন্দের তালিকায় রাখুন" else "Save Favorite"

            "sign_out" -> if (isBn) "লগ আউট" else "Log Out"
            "super_admin" -> if (isBn) "সুপার অ্যাডমিন" else "Super Admin"
            "admin" -> if (isBn) "অ্যাডমিন" else "Admin"
            "user" -> if (isBn) "ব্যবহারকারী" else "User"

            else -> key
        }
    }
}
