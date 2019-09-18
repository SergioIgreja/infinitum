# Mobile SDK for Infinitum

### Table of Contents
1. [Installation](#Installation)
	1. [iOS](#iOS)
	1. [Android](#Android)
1. [Usage](#Usage)
	1. [iOS](#iOS-1)
	1. [Android-Kotlin](#Android---Kotlin)
	1. [Android-Java](#Android---Java)
1. [Modules](#Modules)
	1. [Infinitum](#Infinitum)
	1. [Apps](#Apps)
	1. [Auth](#Auth)
	1. [DevicePosition](#DevicePosition)
	1. [Users](#Users)
	1. [Utils](#Utils)
1. [Responses](#Responses)
	1. [ErrorResponse](#ErrorResponse)
	1. [ConfigResponse](#ConfigResponse)
	1. [InitResponse](#InitResponse)
	1. [Apps](#Apps-1)
	1. [PhotoResponse](#PhotoResponse)
	1. [DevicePositionResponse](#DevicePositionResponse)
	1. [UserResponse](#UserResponse)
	
---

## Installation

### iOS

The Infinitum SDK can be added to the project using [Cocoapods](https://cocoapods.org/).

First you have to add Cocoapods to your project. Simply go to the terminal, navigate to your project folder and type
the following command:

```
 pod init
```

Then go to the generated Podfile and add the following pod:

```ruby
 pod 'InfinitumSDKMobile'
```

By default, the latest version will be used.

*Note that there is no support for emulators as of yet.*

### Android

An android application that uses Java requires a few configurations before being able to use Infinitum.
Since Infinitum is a multiplatform library that uses Kotlin, it becomes necessary for your application to enable Kotlin. 
The easiest way to accomplish this is to create a random Kotlin file in your project. Then, the Android Studio IDE will prompt you to configure Kotlin. Press 'configure', then 'All modules containing Kotlin files' and wait for the project to sync.
The second step is to add the following lines to your application build.gradle, inside the android task:
```Groovy
compileOptions {
	sourceCompatibility = '1.8'
        targetCompatibility = '1.8'
}
```
This will allow your application to use Java 1.8 which introduces lambdas to java. This allows us to use functions as callbacks.

#### To import the Infinitum SDK to your project you will need to:

1. Add the [Jitpack repository](https://jitpack.io/#infinitum-dev/mobile-sdk) to your project build.gradle file
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
```
2. Add the SDK dependency to your application build.gradle file:
```groovy
dependencies {
	implementation('com.github.infinitum-dev:mobile-sdk:latest-version@aar') {
		transitive = true
	}
}
```

3. Sometimes the build may fail because of duplicate META-INF files. To solve this issue simply add the following lines to the android task in your application build.gradle file:

```groovy
packagingOptions {
	exclude 'META-INF/*.kotlin_module'
}
```

---

## Usage

Every call to our API requires two callbacks, onSuccess and onFailure. The onSuccess lambda will require different parameters depending on the method being called. For example config will require a lambda that receives a [ConfigResponse](#ConfigResponse) object, doInit will require a [InitResponse](#InitResponse), etc. On the other hand, onFailure will always require a lambda with an [ErrorResponse](#ErrorResponse) parameter.

Infinitum has various modules that accomplish different goals. To use these modules you need to initialize the sdk by using:

[iOS](#iOS-1) [Android Kotlin](#Android---Kotlin) [Android Java](#Android---Java)

### iOS

```Swift
let infinitum = Infinitum.Companion().getInstance(applicationContext: ApplicationContext())
infinitum.config(
	domain: "demo.infinitum.app", 
	appType: "example-appType", 
	onSuccess: { (response) in self.doInit(configResponse: response)}, 
	onFailure: { (error) in print(error) })
```
```Swift
func doInit(configResponse: ConfigResponse) {
        infinitum.doInit(
		domain: "demo.infinitum.app", 
		appToken: configResponse.apps.first?.token ?? "default",
		onSuccess: photo, //Another way to send a lambda
		onFailure: {(error) in print(error)}),
		eventBuilder: NodeEvent.NodeEventBuilder().addEvent(event: "device-licensed", onEvent: {() in print("LICENSED")}))
}
```
Since this is a multiplatform SDK and we require android apps to send their Context, it was necessary to create a class called ApplicationContext with different implementations for iOS and Android. The iOS implementation does not require any parameters.

After initialization the SDK will remember your session so you don't need to repeat these steps every time your application starts.

All the modules are available through the Infinitum class.
Here's an example that utilizes Auth's module photo method:

```Swift
func photo() {
	let image = UIImage(imageLiteralResourceName: "resource")
        let image64 = Utils().convertImageToBase64(image: image)
        
    	infinitum.auth()?.photo(
		photoB64: image64, 
		onSuccess: {(response) in print(response)}, 
		onFailure: {(error) in print(error)})
}
```

### Android - Kotlin

```Kotlin
infinitum = Infinitum.Companion.getInstance(ApplicationContext(baseContext))
infinitum.config(
	"demo.infinitum.app",
        "example-appType",
	onSuccess = ::doInit,
	onFailure = { error ->
                //error handling
                Log.d(TAG, error.toString())
	})
```

```Kotlin
fun doInit(configResponse: ConfigResponse) {
	val app = configResponse.apps[0]

        infinitum.init(
            domain = "demo.infinitum.app",
            appToken = app.token,
            onSuccess = ::countUsers,
            onFailure = ::onError,
            eventBuilder = NodeEvent.NodeEventBuilder().
                addEvent("device-licensed", { println("----------LICENSED----------")}).
                addEvent("device-unlicensed", {println("----------UNLICENSED----------")})
        )
}
```
Since this is a multiplatform SDK and we require android apps to send their Context, it was necessary to create a class called ApplicationContext with different implementations for iOS and Android. The Android implementation requires the context to be sent as a parameter.

After initialization the SDK will remember your session so you don't need to repeat these steps every time your application starts.

All the modules are available through the Infinitum class.
Here's an example that utilizes App's module getAppById method:

```Kotlin
fun getAppById(initResponse: InitResponse) {
	infinitum.apps()?.getAppById(
		40,
            	onSuccess = { app ->
                	println(app)
            	},
            	onFailure = { errorResponse ->
                	Log.d(TAG, errorResponse.toString())
            	})
}
```

### Android - Java

```Java
Infinitum infinitum = Infinitum.Companion.getInstance(new ApplicationContext(this));
infinitum.config(
	"demo.infinitum.app",
	"example-appType",
	this::configSuccess,
	this::error
);
```

```Java
private Unit configSuccess(ConfigResponse response) {
	infinitum.init(
                "demo.infinitum.app",
                response.getApps().get(0).getToken(),
                this::initSuccess,
                errorResponse -> {
                    Log.d(TAG, errorResponse.toString());
                    return Unit.INSTANCE;
                }
        );
        return Unit.INSTANCE;
}
```
Note that all the functions that sent as a parameter (callbacks) need to end with return Unit.INSTANCE. This is because Kotlin doesn't have Void like Java, but Unit is basically the same thing.

Since this is a multiplatform SDK and we require android apps to send their Context, it was necessary to create a class called ApplicationContext with different implementations for iOS and Android. The Android implementation requires the context to be sent as a parameter.

After initialization the SDK will remember your session so you don't need to repeat these steps every time your application starts.

All the modules are available through the Infinitum class.
Here's an example that utilizes Auth's module photo method:

```Java
private Unit photo(InitResponse response) {
	Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.resource);

        String image = Utils.INSTANCE.convertImageToBase64(bitmap);

        infinitum.auth().photo(
                image,
                photoResponse -> {
                    System.out.println(photoResponse.toString());
                    return Unit.INSTANCE;
                },
                errorResponse -> {
                    System.out.println(errorResponse.toString());
                    return Unit.INSTANCE; });

        return Unit.INSTANCE;
}
```

---

## Modules

### Infinitum

Our main class, Infinitum, contains the functions to initialize the sdk and references to all the available modules.  

```Kotlin
    	fun config(
		domain: String,
               	appType: String,
               	onSuccess: (ConfigResponse) -> Unit,
               	onFailure: (ErrorResponse) -> Unit
	)
 ```
domain - Domain of the company. e.g: demo.infinitum.app to use the demo.  
appType - Type of the application you want to connect.  
onSuccess - Function that will be executed if the request succeeds. Returns a [ConfigResponse](#ConfigResponse) object that contains a list of applications of the given type.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
	fun init(
		domain: String,
             	appToken: String,
             	onSuccess: (InitResponse) -> Unit,
             	onFailure: (ErrorResponse) -> Unit,
		eventBuilder: NodeEventBuilder
	)
```
domain - Domain of the company. e.g: demo.infinitum.app to use the demo.   
appToken - Application token.  
eventBuilder - Events to be listened to by the SDK's websocket. Expects a [NodeEventBuilder](#NodeEventBuilder) object. (Event examples: "device-licensed", "device-unlicensed").  
onSuccess - Function that will be executed if the request succeeds. Returns a [InitResponse](#InitResponse) object that contains more information about the Application chosen.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
	fun apps(): Apps?
```
Returns the [Apps module](#Apps) if the SDK has been initialized, otherwise returns null.  

```Kotlin
	fun auth(): Auth?
```
Returns the [Auth module](#Auth) if the SDK has been initialized, otherwise returns null.  

```Kotlin
	fun devicePosition(): DevicePosition?
```
Returns the [DevicePosition module](#DevicePosition) if the SDK has been initialized, otherwise returns null.  

```Kotlin
	fun users(): Users?
```
Returns the [Users module](#Users) if the SDK has been initialized, otherwise returns null.

```Kotlin
	fun devices(): Devices?
```
Returns the [Devices module](#Devices) if the SDK has been initialized, otherwise returns null.

```Kotlin
	fun deviceInput(): DeviceInput?
```
Returns the [DeviceInput module](#DeviceInput) if the SDK has been initialized, otherwise returns null.

```Kotlin
	fun requests(): Requests?
```
Returns the [Requests module](#Requests) if the SDK has been initialized, otherwise returns null.

```Kotlin
	fun roles(): Roles?
```
Returns the [Roles module](#Roles) if the SDK has been initialized, otherwise returns null.

```Kotlin
	apis(): Apis?
```
Returns the [Apis module](#Roles) if the SDK has been initialized, otherwise returns null.

---

### Apps

```Kotlin
   	//Get all the apps associated with the domain given during the initialization.  
   	fun getApps(
        	onSuccess: (List<App>) -> Unit,
        	onFailure: (ErrorResponse) -> Unit
    	)
```
onSuccess - Function that will be executed if the request succeeds. Returns a List of [Applications](#Apps).  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
	//Create a new Application
	fun createApp(
        	appName: String,
        	appTypeId: Int,
        	token: String,
        	onSuccess: (Boolean) -> Unit,
        	onFailure: (ErrorResponse) -> Unit
    	)
```
appName - Application name.  
appTypeId - Application type.  
token - Application token.  
onSuccess - Function that will be executed if the request succeeds. Returns true.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
	//Get application by id
	fun getAppById(
        	appId: Int,
        	onSuccess: (App) -> Unit,
        	onFailure: (ErrorResponse) -> Unit
    	) {
```
appId - Application id.  
onSuccess - Function that will be executed if the request succeeds. Returns a [app](#Apps) object.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
	//Deletes an Application
   	fun deleteApp(
        	appId: Int,
        	onSuccess: (Boolean) -> Unit,
        	onFailure: (ErrorResponse) -> Unit
        )
```
appId - Application id.  
onSuccess - Function that will be executed if the request succeeds. Returns true.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
	//Updates an Application
	fun updateApp(
        	appId: Int,
        	appName: String,
        	appTypeId: Int,
        	onSuccess: (Boolean) -> Unit,
        	onFailure: (ErrorResponse) -> Unit
    	)
```
appId - Application id.  
appName - Application name.  
appTypeId - Application type.  

---

### Auth

Contains all the authentication methods.

```Kotlin
   	//Authentication with facial recognition.
   	fun photo(
        	photoB64: String,
        	onSuccess: (PhotoResponse) -> Unit,
        	onFailure: (ErrorResponse) -> Unit
    	)
```
photoB64 - Photo in Base64 format. Make sure the image is not over 1mb.  
onSuccess - Function that will be executed if the request succeeds. Returns a [PhotoResponse](#PhotoResponse) object that contains information about the authenticated user.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

---

### DevicePosition

```Kotlin
    fun getAllDevicePositions(
        onSuccess: (List<DevicePositionResponse>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
onSuccess - Function that will be executed if the request succeeds. Returns a List of [DevicePositionResponse](#DevicePositionResponse).  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun newDevicePosition(
        latitude: String,
        longitude: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
Note: This method links this position to your device's identification. Use the next method to associate to a specific device id.  
latitude - Latitude of the current position.  
longititude - Longitude of the current position.  
onSuccess - Function that will be executed if the request succeeds. Returns true.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun newDevicePosition(
        deviceId: Int,
        latitude: String,
        longitude: String,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
deviceId - Device id.  
latitude - Latitude of the current position.  
longititude - Longitude of the current position.  
onSuccess - Function that will be executed if the request succeeds. Returns true.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun deleteDevicePosition(
        devicePositionId: Int,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
```
devicePositionId - id of the device position to be deleted.  
onSuccess - Function that will be executed if the request succeeds. Returns true.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun getDevicePositionById(
        devicePositionId: Int,
        onSuccess: (DevicePositionResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
devicePositionId - id of the device position.  
onSuccess - Function that will be executed if the request succeeds. Returns [DevicePositionResponse](#DevicePositionResponse) object.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun getDevicePositionsByDeviceId(
        deviceId: Int,
        onSuccess: (List<DevicePositionResponse>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
```
deviceId - Device id.  
onSuccess - Function that will be executed if the request succeeds. Returns a List of [DevicePositionResponse](#DevicePositionResponse) associated to the device of the given deviceId.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
fun updateDevicePosition(
        devicePositionId: Int,
        deviceId: Int,
        latitude: String,
        longitude: String,
        onSuccess: (DevicePositionResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
devicePositionId - The id of the DevicePosition to be altered.  
deviceId - Updated device id.  
latitude - Updated latitude.  
longititude - Updated longitude.  
onSuccess - Function that will be executed if the request succeeds. Returns updated [DevicePositionResponse](#DevicePositionResponse).  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.

```Kotlin
 fun updateDevicePosition(
        devicePositionId: Int,
        latitude: String,
        longitude: String,
        onSuccess: (DevicePositionResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    ) {
```
Note: Using this function the associated device will change to this device.
devicePositionId - The id of the DevicePosition to be altered.  
latitude - Updated latitude.  
longititude - Updated longitude.  
onSuccess - Function that will be executed if the request succeeds. Returns updated [DevicePositionResponse](#DevicePositionResponse).  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.

### Users

```Kotlin
    fun getAllUsersCount(
        onSuccess: (Int) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
onSuccess - Function that will be executed if the request succeeds. Returns a Integer value that represents the total user count of that application.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun getAllUsers(
        onSuccess: (List<UserResponse>) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
onSuccess - Function that will be executed if the request succeeds. Returns a List of [UserResponse](#UserResponse) objects that contain more information about an individual user.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun getUserById(
        userId: Int,
        onSuccess: (UserResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
userId - User id.  
onSuccess - Function that will be executed if the request succeeds. Returns a [UserResponse](#UserResponse) object.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun getUserByFace(
        photo: String,
        onSuccess: (UserResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
photo - An image in base64 that contains multiple faces. (You can use our [Utils class](#Utils) to convert an image to base64)  
onSuccess - Function that will be executed if the request succeeds. Returns a List of [UserResponse](#UserResponse) objects if the API recognizes the users.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun createUser(
        name: String,
        optionalParameters: UserOptionalParameters.Builder,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
name - Name of the user.  
optionalParameters - Optional builder that can be used to add more information about the user. (Ex: UserOptionalParameters.Builder().setPhone("911111111"))   
onSuccess - Function that will be executed if the request succeeds. Returns true.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun deleteUser(
        userId: Int,
        onSuccess: (Boolean) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
userId - User id.  
onSuccess - Function that will be executed if the request succeeds. Returns true.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun updateUser(
        userId: Int,
        name: String,
        otherParameters: UserOptionalParameters.Builder,
        onSuccess: (UserResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
userId - User id.  
name - Updated name of the user.  
optionalParameters - Optional builder that can be used to add more information about the user. (Ex: UserOptionalParameters.Builder().setPhone("911111111"))   
onSuccess - Function that will be executed if the request succeeds. Returns updated [UserResponse](#UserResponse).  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun userLivenessRequest(
        onSuccess: () -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
onSuccess - Function that will be executed if the request succeeds.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
fun verifyDocuments(
        front: String,
        back: String,
        onSuccess: (String) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
front - Image in Base64 of the front of the document. (You can use our [Utils class](#Utils) to convert an image to base64)  
back - Image in Base64 of the back of the document. (You can use our [Utils class](#Utils) to convert an image to base64)  
onSuccess - Function that will be executed if the request succeeds. Returns the body of the response.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

```Kotlin
    fun verifyUserByPhoto(
        photo: String,
        onSuccess: (UserResponse) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
photo - An image in base64 of the user. (You can use our [Utils class](#Utils) to convert an image to base64)  
onSuccess - Function that will be executed if the request succeeds. Returns a [UserResponse](#UserResponse) object.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  


```Kotlin
    fun verifyUserFaceProperties(
        photo: String,
        onSuccess: (String) -> Unit,
        onFailure: (ErrorResponse) -> Unit
    )
```
photo - An image in base64 of the user. (You can use our [Utils class](#Utils) to convert an image to base64)  
onSuccess - Function that will be executed if the request succeeds. Returns the body of the response.  
onFailure - Function that will be executed if the request fails. Returns a [ErrorResponse](#ErrorResponse) object.  

---



---

### Utils

Contains functions that will help you save time.  

```Kotlin
	/* Image that will be converted to Base64 format. On iOS this method requires an UIImage, on Android this method receives a Bitmap.*/
   	fun convertImageToBase64(image: Image): String 
	
	fun getDate(): String
```

---

## Responses

### ErrorResponse

```Kotlin
class ErrorResponse(
	var message: String?="", 
	var type: String?="", 
	var status: Int?=0)
```
### ConfigResponse

```Kotlin
class ConfigResponse(val apps: List<App>)

class App(
	val name: String="", 
	val token: String="")
```

### InitResponse

```Kotlin
class InitResponse(val config: Config)

class Config(
	val country: String = "",
    	val background: String = "",
    	val logo: String = "",
    	val text_color: String = "",
    	val button_color: String = "",
    	val button_text_color: String = "",
    	val pincode: String = "",
    	val offline: Int = -1
)
```

### Apps

```Kotlin
class App(
    	val id: Int,
    	val name: String,
    	val token: String,
    	val type: Type,
    	val client: Client
)

class Type(val alias: String)

class Client(
    	val id: String,
    	val secret: String)
```

### PhotoResponse

```Kotlin
class PhotoResponse(
    	val name: String,
    	val email: String
)
```

### DevicePositionResponse
```Kotlin
data class DevicePositionResponse(
    val id: Int,
    val device_id: Int,
    val lat: String,
    val lng: String
)
```
 
### UserResponse

```Kotlin
data class UserResponse(
    val id: Int,
    val name: String? = "",
    val email: String = "",
    val phone: String? = "",
    val avatar: String? = "",
    val info: Info? = null
)

data class Info(
    val birthdate: String? = "",
    val language: String? = "",
    val photo: String? = "",
    val data: String? = ""
)
```
