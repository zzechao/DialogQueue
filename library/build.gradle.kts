plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    //id("maven-publish")
}
apply {
    from("${rootProject.projectDir}/publish-mavencentral.gradle")
}
android {
    namespace = "com.zhouz.dialogqueue"
    compileSdk = 31

    defaultConfig {
        minSdk = 15
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

configurations.all {
    resolutionStrategy {
        cacheDynamicVersionsFor(0,"seconds")
        cacheChangingModulesFor(0,"seconds")
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}

//group = "com.zhouz.dialogqueue"
//version = "1.0.1.2"
//
//publishing {
//    publications {
//        create<MavenPublication>("dialogqueue") {
//            groupId = "com.zhouz.dialogqueue"
//            version = "1.0.1.2"
//        }
//    }
//    repositories {
//        maven(uri("../maven-repo/"))
//    }
//}