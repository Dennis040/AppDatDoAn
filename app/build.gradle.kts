plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.googleAndroidLibrariesMapsplatformSecretsGradlePlugin)
}

android {
    namespace = "com.example.grab_demo"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.grab_demo"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/NOTICE")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE.txt")
        exclude ("META-INF/INDEX.LIST")
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}
dependencies {
    implementation(
        fileTree(
            mapOf(
                "dir" to "E:\\ZaloPayLib",
                "include" to listOf("*.aar", "*.jar"),
                "exclude" to listOf("")
            )
        )
    )
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.google.android.gms:play-services-maps:19.0.0")
    implementation ("com.google.maps:google-maps-services:2.1.2")
//    implementation("com.google.maps:google-maps-services:0.18.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.activity:activity:1.9.3")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.airbnb.android:lottie:6.6.0")

    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.microsoft.sqlserver:mssql-jdbc:11.2.0.jre8")
//    implementation("com.microsoft.sqlserver:mssql-jdbc:12.7.0")  // Sử dụng phiên bản mới nhất nếu có
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
//    implementation("androidx.appcompat:appcompat:1.3.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.0")


    implementation("de.hdodenhof:circleimageview:3.1.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
    implementation("com.google.maps.android:maps-utils-ktx:5.1.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("net.sourceforge.jtds:jtds:1.3.1")
    implementation("com.github.blackfizz:eazegraph:1.2.3@aar")
    implementation("com.nineoldandroids:library:2.4.0")
    implementation("com.makeramen:roundedimageview:2.3.0")
    implementation ("org.slf4j:slf4j-simple:1.7.25")
//    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.stripe:stripe-android:20.53.0")
    //implementation("com.stripe:stripe-android:17.2.0")
    implementation("com.stripe:stripe-java:28.0.0")
    implementation("com.android.volley:volley:1.2.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")
    implementation ("androidx.lifecycle:lifecycle-process:2.8.7") // hoặc phiên bản mới hơn
    implementation ("com.google.cloud:google-cloud-dialogflow:2.1.0")
    implementation ("io.grpc:grpc-okhttp:1.30.0")
    implementation ("com.google.auth:google-auth-library-oauth2-http:0.20.0")
    implementation ("com.google.guava:guava:29.0-android")

}