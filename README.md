# Protodroid

Android Library for GRPC Network Interceptor

<h5>Debug Mode</h5>

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.lukasdylan/protodroid/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.lukasdylan/protodroid)

<h5>Production (no-op) Mode</h5>

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.lukasdylan/protodroid-no-op/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.lukasdylan/protodroid-no-op)

<h3>What is Protodroid?</h3>

Protodroid is an Android library for intercept every API request and response on GRPC, inspired by [Chucker](https://github.com/ChuckerTeam/chucker). This library was built with 100% pure Kotlin, AndroidX based components, and Kotlin Coroutines. 

<h3>Why Protodroid?</h3>

Protodroid was built to help Engineering Team especially QA Team to verify the API request and response from client to server. Protodroid is a library that embedded in client App so there is no (you need to enable notification on your Android device so Protodroid can notify every API calls to you) extra configuration or installation to use this library, and also it can be checked anytime, anywhere.

For Frontend Android Team, you can also check in LogCat Android Studio as this library provide logging for the API calls. 

<h3>How to use Protodroid?</h3>
<h2>New Update!!</h4>

Currently Protodroid have 2 versions:
- Jetpack Compose version (starting from `1.1.0` later)
- Non-compose version (starting until `1.0.x`)

Make sure your project specification meet this requirements:
- AndroidX
- Min Android SDK: 16 (Jelly Bean) for non-compose version, and 21 (Lollipop) for Jetpack Compose version

Follow this step to implement this library:
1. Make sure you already added 
`implementation 'com.github.lukasdylan:protodroid:<latest_version>' `
into your module. (note: check the latest version [here](https://search.maven.org/artifact/io.github.lukasdylan/protodroid))

2. Don’t forget to exclude `io.grpc.grpc-protobuf-lite` module if you already implement `io.grpc.grpc-protobuf` dependency 
   `implementation 'com.github.lukasdylan:protodroid:<latest_version>' {`
       `exclude group: "io.grpc" module:"grpc-protobuf-lite"`
   `}`

3. Every GRPC API call need `io.grpc.Channel` as a parameter. Inside of this class, you can put any `io.grpc.ClientInterceptors` that can be stacked for every interceptor by calling 
> ClientInterceptors.intercept('your client interceptors')

4. You can put `id.lukasdylan.grpc.protodroid.ProtodroidInterceptor` by calling
> ClientInterceptors.intercept(ProtodroidInterceptor(context))

  (note: you need to passing `ApplicationContext` or `Application` as this is required to show notification. Please avoid using Activity or Fragment Context)

5. You can also put multiple interceptor inside .intercept() function. For best practice, please put ProtodroidInterceptor class on the last argument, in case you need to add additional parameter(s) at header on another interceptor class, so it can be also logged in Protodroid. 
For example: 
> ClientInterceptors.intercept('your another client interceptors', ProtodroidInterceptor(context))

6. Don’t forget to check if the build apk is on Debug Mode when you initialize ProtodroidInterceptor to avoid showing Protodroid notification on Production App. 

7. Profit!
