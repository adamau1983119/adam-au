plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.wtsaskingforsignature"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.wts.dsfortune"
        minSdk = 26
        targetSdk = 36
        versionCode = 7
        versionName = "1.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
    }

    // 定義簽章設定需在 buildTypes 之前
    signingConfigs {
        create("release") {
            storeFile = file("wts-release-key.keystore")
            storePassword = "123456"
            keyAlias = "wts-key"
            keyPassword = "123456"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
            // 確保版本一致性
            buildConfigField("String", "BUILD_TYPE", "\"release\"")
            buildConfigField("String", "API_BASE_URL", "\"https://api.deepseek.com/\"")
            buildConfigField("boolean", "USE_REMOTE_API", "false")
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            // 確保 debug 版本不使用代碼混淆
            buildConfigField("String", "BUILD_TYPE", "\"debug\"")
            buildConfigField("String", "API_BASE_URL", "\"https://api.deepseek.com/\"")
            buildConfigField("boolean", "USE_REMOTE_API", "false")
            // 使用相同的簽名以確保行為一致
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    
    lint {
        baseline = file("lint-baseline.xml")
        checkReleaseBuilds = false
        abortOnError = false
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}


dependencies {
    val composeBom = platform("androidx.compose:compose-bom:2024.06.00")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.3")
    implementation("androidx.activity:activity-compose:1.9.1")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3:1.2.1")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Material Components (提供 Theme.Material3.* XML 主題資源)
    implementation("com.google.android.material:material:1.12.0")

    // Compose Google Fonts（用於 Noto Serif TC 下載字體）
    implementation("androidx.compose.ui:ui-text-google-fonts")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Security & Biometric
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    implementation("androidx.biometric:biometric:1.1.0")

    // Work Manager
    implementation("androidx.work:work-runtime-ktx:2.9.1")

    // GIF 圖片播放（Coil）
    implementation("io.coil-kt:coil-compose:2.6.0")
    implementation("io.coil-kt:coil-gif:2.6.0")
    
    // Google AdMob 广告
    implementation("com.google.android.gms:play-services-ads:22.6.0")
    
    // DeepSeek API Dependencies
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.11.0")
    implementation("com.google.code.gson:gson:2.10.1")

    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    testImplementation("junit:junit:4.13.2")
}
