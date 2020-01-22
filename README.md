
Lint Rules for Android.
==========

[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

Android Lint Rules includes several rules for Android platform.

## Contents: ##
* Background Vector : Detect vectors used for background 

# Installation
 - To implement **Android Lint Rules** to your Android project via Gradle, you need to add JitPack repository to your root build.gradle.
```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
 - After adding JitPack repository, you can add **Android Lint Rules** dependency to your app level build.gradle.
```gradle
dependencies {
    implementation "com.github.bsobe:androidlintrules:1.0.0"
}
```

License
--------
    Copyright 2019 bsobe / Barış Söbe

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
