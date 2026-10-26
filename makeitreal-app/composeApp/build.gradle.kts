import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.androidApplication)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    jvm("desktop") {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
    
    sourceSets {
        val desktopMain by getting
        val androidMain by getting {
            dependencies {
                implementation(libs.androidx.activity.compose)
                implementation(libs.androidx.core.ktx)
            }
        }
        
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
        }
        
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

val envVersionCode = System.getenv("VERSION_CODE")?.toIntOrNull() ?: 2
val envVersionName = System.getenv("VERSION_NAME") ?: "1.0.1"
val keystorePass = System.getenv("KEYSTORE_PASSWORD") ?: "makeitreal123"
val keyAliasName = System.getenv("KEYSTORE_ALIAS") ?: "makeitreal"
val keyPass = System.getenv("KEY_PASSWORD") ?: keystorePass

android {
    namespace = "com.eufymake.real"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.eufymake.real"
        minSdk = 24
        targetSdk = 34
        versionCode = envVersionCode
        versionName = envVersionName
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("keystore/makeitreal.jks")
            storePassword = keystorePass
            keyAlias = keyAliasName
            keyPassword = keyPass
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    sourceSets {
        getByName("main") {
            assets.srcDirs("src/commonMain/resources")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

compose.desktop {
    application {
        mainClass = "com.eufymake.real.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "com.eufymake.real"
            packageVersion = envVersionName
        }
    }
}
