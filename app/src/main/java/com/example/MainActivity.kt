package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.R
import com.example.ai.AiNutritionService
import com.example.data.model.FavoriteFood
import com.example.data.model.FoodEntry
import com.example.data.model.User
import com.example.data.model.WaterEntry
import com.example.data.repository.AdminRepository
import com.example.data.repository.AuthRepository
import com.example.data.repository.FoodRepository
import com.example.ui.components.AppHeader
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavScreen
import com.example.ui.screens.AdminScreen
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.AssistantScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.DiaryScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.ScannerScreen
import com.example.ui.screens.SearchFoodScreen
import com.example.ui.theme.DeepObsidian
import com.example.ui.theme.MyApplicationTheme
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 1. App Check initialization before any UI or Firebase operation
        configureAppCheck(this, intent)

        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val context = LocalContext.current
    val firestoreDb = remember {
        val dbId = context.getString(R.string.firestore_database_id)
        FirebaseFirestore.getInstance(dbId)
    }

    val authRepository = remember { AuthRepository(context, firestoreDb) }
    val foodRepository = remember { FoodRepository(firestoreDb) }
    val adminRepository = remember { AdminRepository(firestoreDb) }
    val aiNutritionService = remember { AiNutritionService() }

    var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }
    var showAuthScreen by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            currentUser = auth.currentUser
        }
        Firebase.auth.addAuthStateListener(listener)
        onDispose {
            Firebase.auth.removeAuthStateListener(listener)
        }
    }

    if (showAuthScreen) {
        AuthScreen(
            authRepository = authRepository,
            onAuthSuccess = { user ->
                currentUser = user
                showAuthScreen = false
            },
            onDismiss = {
                showAuthScreen = false
            }
        )
    } else {
        AppNavigationHost(
            currentUser = currentUser,
            authRepository = authRepository,
            foodRepository = foodRepository,
            adminRepository = adminRepository,
            aiNutritionService = aiNutritionService,
            onRequestSignIn = { showAuthScreen = true },
            onSignOut = {
                currentUser = null
            }
        )
    }
}

@Composable
fun AppNavigationHost(
    currentUser: FirebaseUser?,
    authRepository: AuthRepository,
    foodRepository: FoodRepository,
    adminRepository: AdminRepository,
    aiNutritionService: AiNutritionService,
    onRequestSignIn: () -> Unit,
    onSignOut: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf(NavScreen.HOME) }
    var isSearchOpen by remember { mutableStateOf(false) }
    var searchMealTypeTarget by remember { mutableStateOf("breakfast") }
    var guestLanguage by remember { mutableStateOf("en") }

    val uid = currentUser?.uid

    // Only observe Firestore when user is authenticated (satisfies firestore security rules)
    val userProfile by remember(uid) {
        if (uid != null) authRepository.observeUserProfile(uid)
        else flowOf<User?>(null)
    }.collectAsState(initial = null)

    val todayDate = remember { foodRepository.getTodayDate() }
    var selectedDiaryDate by remember { mutableStateOf(todayDate) }

    val todayEntries by remember(uid, todayDate) {
        if (uid != null) foodRepository.observeFoodEntries(todayDate)
        else flowOf<List<FoodEntry>>(emptyList())
    }.collectAsState(initial = emptyList())

    val diaryEntries by remember(uid, selectedDiaryDate) {
        if (uid != null) foodRepository.observeFoodEntries(selectedDiaryDate)
        else flowOf<List<FoodEntry>>(emptyList())
    }.collectAsState(initial = emptyList())

    val todayWater by remember(uid, todayDate) {
        if (uid != null) foodRepository.observeWaterEntries(todayDate)
        else flowOf<List<WaterEntry>>(emptyList())
    }.collectAsState(initial = emptyList())

    val favoriteFoods by remember(uid) {
        if (uid != null) foodRepository.observeFavoriteFoods()
        else flowOf<List<com.example.data.model.FavoriteFood>>(emptyList())
    }.collectAsState(initial = emptyList())

    val currentLang = userProfile?.language ?: guestLanguage

    // Back handling: return to Home if on secondary screen
    BackHandler(enabled = isSearchOpen || currentScreen != NavScreen.HOME) {
        if (isSearchOpen) {
            isSearchOpen = false
        } else {
            currentScreen = NavScreen.HOME
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepObsidian)
    ) {
        if (isSearchOpen) {
            SearchFoodScreen(
                user = userProfile,
                preselectedMealType = searchMealTypeTarget,
                foodRepository = foodRepository,
                favoriteFoods = favoriteFoods,
                onBack = { isSearchOpen = false },
                onFoodAdded = { isSearchOpen = false },
                onRequestSignIn = onRequestSignIn
            )
        } else {
            Scaffold(
                containerColor = DeepObsidian,
                topBar = {
                    AppHeader(
                        user = userProfile,
                        onLanguageToggle = {
                            if (userProfile != null) {
                                val newLang = if (currentLang == "en") "bn" else "en"
                                scope.launch {
                                    authRepository.updateUserProfile(userProfile!!.copy(language = newLang))
                                }
                            } else {
                                guestLanguage = if (guestLanguage == "en") "bn" else "en"
                            }
                        },
                        onAdminClick = { currentScreen = NavScreen.ADMIN },
                        onProfileClick = {
                            if (currentUser == null) {
                                onRequestSignIn()
                            } else {
                                currentScreen = NavScreen.PROFILE
                            }
                        }
                    )
                },
                bottomBar = {
                    BottomNavBar(
                        currentScreen = currentScreen,
                        onNavigate = { screen ->
                            when (screen) {
                                NavScreen.HOME -> currentScreen = screen
                                NavScreen.DIARY, NavScreen.SCAN, NavScreen.ASSISTANT, NavScreen.PROFILE -> {
                                    if (currentUser == null) {
                                        onRequestSignIn()
                                    } else {
                                        currentScreen = screen
                                    }
                                }
                                NavScreen.ANALYTICS -> {
                                    if (currentUser == null) {
                                        onRequestSignIn()
                                    } else {
                                        currentScreen = screen
                                    }
                                }
                                else -> currentScreen = screen
                            }
                        },
                        language = currentLang
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentScreen) {
                        NavScreen.HOME -> {
                            HomeScreen(
                                user = userProfile,
                                foodEntries = todayEntries,
                                waterEntries = todayWater,
                                foodRepository = foodRepository,
                                onNavigate = { screen ->
                                    if (currentUser == null && screen != NavScreen.HOME) {
                                        onRequestSignIn()
                                    } else {
                                        currentScreen = screen
                                    }
                                },
                                onOpenSearch = {
                                    searchMealTypeTarget = "lunch"
                                    isSearchOpen = true
                                },
                                onSignInClick = onRequestSignIn
                            )
                        }

                        NavScreen.DIARY -> {
                            DiaryScreen(
                                user = userProfile,
                                selectedDate = selectedDiaryDate,
                                onDateChange = { selectedDiaryDate = it },
                                foodEntries = diaryEntries,
                                foodRepository = foodRepository,
                                onAddFoodClick = { mealType ->
                                    searchMealTypeTarget = mealType
                                    isSearchOpen = true
                                }
                            )
                        }

                        NavScreen.SCAN -> {
                            ScannerScreen(
                                user = userProfile,
                                foodRepository = foodRepository,
                                aiNutritionService = aiNutritionService,
                                onSuccessAdded = {
                                    currentScreen = NavScreen.DIARY
                                }
                            )
                        }

                        NavScreen.ASSISTANT -> {
                            AssistantScreen(
                                user = userProfile,
                                aiNutritionService = aiNutritionService
                            )
                        }

                        NavScreen.ANALYTICS -> {
                            AnalyticsScreen(
                                user = userProfile,
                                foodRepository = foodRepository
                            )
                        }

                        NavScreen.PROFILE -> {
                            ProfileScreen(
                                user = userProfile,
                                authRepository = authRepository,
                                onSignOut = {
                                    onSignOut()
                                    currentScreen = NavScreen.HOME
                                },
                                onOpenAdmin = {
                                    currentScreen = NavScreen.ADMIN
                                }
                            )
                        }

                        NavScreen.ADMIN -> {
                            AdminScreen(
                                user = userProfile,
                                adminRepository = adminRepository,
                                onBack = { currentScreen = NavScreen.PROFILE }
                            )
                        }
                    }
                }
            }
        }
    }
}
