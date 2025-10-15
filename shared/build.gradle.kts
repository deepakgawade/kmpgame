import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig


plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    kotlin("plugin.serialization") version "2.2.20"
    id("com.google.devtools.ksp")
    id("com.rickclephas.kmp.nativecoroutines")
    id("jacoco")

}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }
    
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }
    
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    val ktorVersion = "3.2.3"

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.experimental.ExperimentalObjCName")
                optIn("kotlin.time.ExperimentalTime")
            }
        }
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.7.1")
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
            implementation("io.ktor:ktor-client-core:${ktorVersion}")
            implementation("io.ktor:ktor-client-content-negotiation:${ktorVersion}")
            implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
            // put your Multiplatform dependencies here
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:$ktorVersion")
            
       }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation("io.ktor:ktor-client-mock:$ktorVersion") // Use your ktorVersion
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.1")
        }
    }
}

android {
    namespace = "org.example.kmpgame.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
}

tasks.withType<Test>().configureEach {
    if(name == "testDebugUnitTest"){
        configure<JacocoTaskExtension>{
            isIncludeNoLocationClasses = true
            excludes = listOf("jdk.internal.*")
        }
    }
}

tasks.register<JacocoReport>("sharedJacocoReport"){
    dependsOn("testDebugUnitTest")
    group = "verification"
    description = "Generate Jacoco code coverage for the shared module."
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    val sourceDirs = files("src/commonMain/kotlin")
    val classesDirs = fileTree("$buildDir/classes/kotlin/jvm/main"){
        exclude("**/R.class", "**/R$*.class", "**/BuildConfig.*", "**/Manifest*.*")
    }
    sourceDirectories.setFrom(sourceDirs)
    classDirectories.setFrom(classesDirs)
    executionData.setFrom(fileTree(buildDir){
        include("jacoco/testDebugUnitTest.exec")
    })
}
