import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.devtools.ksp)
    kotlin("plugin.serialization") version "1.9.23"
}

abstract class GitVersionHelper @Inject constructor(
    private val execOps: ExecOperations
) {
    fun getVersionCode(): Int = try {
        val stdout = ByteArrayOutputStream()
        execOps.exec {
            commandLine("git", "rev-list", "--count", "main")
            standardOutput = stdout
        }
        stdout.toString().trim().toInt()
    } catch (ignored: Exception) {
        1
    }
}

val gitHelper = objects.newInstance(GitVersionHelper::class.java)
val versionCodeValue = gitHelper.getVersionCode()

android {
    namespace = "ua.nure.nomnomsave"
    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "ua.nure.nomnomsave"
        minSdk = 33
        targetSdk = 36
        versionCode = versionCodeValue
        versionName = "1.$versionCodeValue"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    applicationVariants.all {
        outputs.all {
            // AAB file name that You want. Flavor name also can be accessed here.
            val projectName = rootProject.name
            val aabPackageName = "$projectName-v$versionName.aab"
            val apkPackageName = "$projectName-v$versionName.apk"
            // Get final bundle task name for this variant
            val bundleFinalizeTaskName = buildString {
                append("sign")
                productFlavors.forEach {
                    append(it.name.replaceFirstChar { c -> c.uppercase() })
                }
                append(buildType.name.replaceFirstChar { c -> c.uppercase() })
                append("Bundle")
            }

            tasks.named(
                bundleFinalizeTaskName,
                com.android.build.gradle.internal.tasks.FinalizeBundleTask::class.java,
            ) {
                val file = finalBundleFile.asFile.get()
                val finalFile = File(file.parentFile, aabPackageName)
                finalBundleFile.set(finalFile)
            }

            this as com.android.build.gradle.internal.api.ApkVariantOutputImpl
            outputFileName = apkPackageName
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
//    implementation(libs.lifecycle.runtime.compose)

//DI
    implementation(libs.android.hilt)
    ksp(libs.android.hilt.compiler)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.navigation.compose)
//Network
    implementation(libs.chucker)

    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.logging)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.auth)
//    implementation(libs.chuckerNoOp)
//DB
    implementation(libs.roomCommon)
    implementation(libs.roomRuntime)
    implementation(libs.roomKtx)
    implementation(libs.roomPaging)
    ksp(libs.roomCompiler)
//  AsyncImage
    implementation(libs.coil.compose)
    implementation(libs.coil.network)
// DataStore
    implementation(libs.datastore.preferences)

    //Google
    implementation(libs.credentials)
    implementation(libs.credentials.play)
    implementation(libs.googleid)
    implementation(libs.google.signin)

    implementation(libs.kizitonwose.calendar)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}