// Top-level build file where you can add configuration options common to all sub-projects/modules
buildscript {

    dependencies {
        var nav_version = "2.5.3"
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")
    }
}

plugins {
    id ("com.android.application") version "8.1.4" apply false
    id ("com.android.library") version "8.1.4" apply false
    id ("org.jetbrains.kotlin.android") version "1.7.20" apply false
}