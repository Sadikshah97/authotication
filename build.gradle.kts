/*buildscript {
    repositories {
        google() // ✅ Ensures Google Maven is available
        mavenCentral() // ✅ Ensures dependencies from Maven Central
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.1.1") // Update to latest Gradle version if needed
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.0")
    }
}*/
/*allprojects {
    repositories {
        google()
        mavenCentral()
        maven ( url ="https://jitpack.io" )
    }
}*/

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}