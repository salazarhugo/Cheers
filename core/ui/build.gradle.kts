plugins {
    id("cheers.android.library")
    id("cheers.android.library.compose")
}

android {
    namespace = "com.salazar.cheers.core.ui"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":common"))
    implementation(project(":core:util"))
    implementation(project(":core:model"))
    implementation(project(":core:shared"))

    debugImplementation("androidx.compose.ui:ui-tooling:1.5.4")

    implementation(project(":data:party"))
    implementation(project(":data:post"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // ExoPlayer 2
    implementation(libs.exoplayer)

    // Jetpack Compose
    implementation(libs.androidx.compose.ui.util)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)

    // Accompanist
    implementation(libs.accompanist.navigation.material)
    implementation(libs.accompanist.navigation.animation)
    implementation(libs.accompanist.pager.indicators)

    // Map Box SDK
    implementation(libs.mapbox)
    implementation("com.mapbox.extension:maps-compose:11.0.0")
    implementation(libs.mapbox.sdk.services)

    // Coil
    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    // Accompanist Permissions
    implementation(libs.accompanist.permissions)

    implementation(project(mapOf("path" to ":core:protobuf")))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}