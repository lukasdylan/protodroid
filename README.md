# Protodroid

Android Library for GRPC Network Interceptor


[ ![Download](https://api.bintray.com/packages/lukasdylan/Protodroid/protodroid/images/download.svg) ](https://bintray.com/lukasdylan/Protodroid/protodroid/_latestVersion)

<h3>What is Protodroid?</h3>

Protodroid is an Android library for intercept every API request and response on GRPC, inspired by Chucker. This library was built with 100% pure Kotlin, AndroidX based components, and Kotlin Coroutines. 

<h3>Why Protodroid?</h3>

Protodroid was built to help Engineering Team especially QA Team to verify the API request and response from client to server. Protodroid is a library that embedded in client App so there is no (you need to enable notification on your Android device so Protodroid can notify every API calls to you) extra configuration or installation to use this library, and also it can be checked anytime, anywhere.

For Frontend Android Team, you can also check in LogCat Android Studio as this library provide logging for the API calls. 

<h3>How to use Protodroid?</h3>

Make sure your project specification meet this requirements:
- AndroidX
- Min Android SDK: 16 (Jelly Bean)

Follow this step to implement this library:
1. Make sure you already added 
`implementation 'com.github.lukasdylan:protodroid:<latest_version>' `
into your module. (note: check the latest version [here](https://bintray.com/lukasdylan/Protodroid/protodroid))

2. Don’t forget to exclude `io.grpc.grpc-protobuf-lite` module if you already implement `io.grpc.grpc-protobuf` dependency

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
