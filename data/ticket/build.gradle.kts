plugins {
    id("cheers.android.library")
    id("cheers.android.hilt")
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.salazar.cheers.data.ticket"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    implementation(project(":common"))
    implementation(project(":core:protobuf"))
    implementation(project(":core:model"))
    implementation(project(":core:util"))

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}