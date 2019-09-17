# How to create a new module

1. Create a new package under fyi.modules with the name of the module.
1. Inside the fyi.modules.<new-package> create a new package called models that will contain all relevant models.
1. Create a new File with the name of the module that will contain all the requests.
1. Each request should have a onSuccess and onFailure lambdas that will be executed after the response from the server.
1. After validating the data inserted by the user, create the header and body maps of the request using the Args class.
1. Then use the class RequestLauncher to send the request. Specify the type of the request, Put, Post, etc.. and the header and body maps.
1. You should then parse the response from the server using KotlinxSerialization to the classes defined in the models package in case of a success. If an error occurred parse the response to a ErrorResponse object.
1. Return to the user via the callback lambdas.

# How to submit a new version

## iOS

To submit a new version to Cocoapods, commit changes to github with the desired Tag.
Then, on the podspec file change the version of spec.version and the tag in spec.source to the same as the tag of your last commit.

Check if your podspec is valid:

```

 pod spec lint YOUR_FILE.podspec

```

If this command succeed, you can proceed to upload your framework to cocoapods, otherwise you must fix the issues.

If its the first time you are uploading the library first you have to register yourself with the command:

```

 pod trunk register YOUR_GITHUB_EMAIL 'YOUR_GITHUB_USERNAME'

```

If you are already registered you may ignored the step above.

After all this validations were completed, you are able to submit your framework to Cocoapods, for that purpose use the following command:

```

 pod trunk push YOUR_FILE.podspec

```

### How to add a tag to your repository

You need to have the repository syncronized with github.

#### Using Sourcetree

On the commit you want to publish, right click it, select 'Tag...', select an existing Tag or create a new one, make sure you select the push tag option and press 'Add'.

---

#### Using Git

Note: if you are not on the desired branch you can change it using the command:

````
			git checkout -b BRANCH_NAME <tag_name>
````

Using the terminal, add your changes using 

````
			git add -A
````

then commit your changes to the respective branch.

````
			git commit -m "YOUR MESSAGE"
````

create a new tag for your release:

````
			git tag 'YOUR_TAG'
````

and finally push your changes with the respective tags

````
			git push origin --tags
````

---

Some examples of common tags for android libraries are: '0.0.1', '1.0.0', '3.1.2'

## Android

To submit a new version to Jitpack, commit changes to github to the desired TAG.
Jitpack will build it and publish all the builds for any tag you have.

### How to add a tag to your repository

You need to have the repository syncronized with github.

#### Using Sourcetree

On the commit you want to publish, right click it, select 'Tag...', select an existing Tag or create a new one, make sure you select the push tag option and press 'Add'.

---

#### Using Git

Note: if you are not on the desired branch you can change it using the command:

````
			git checkout -b BRANCH_NAME <tag_name>
````

Using the terminal, add your changes using 

````
			git add -A
````

then commit your changes to the respective branch.

````
			git commit -m "YOUR MESSAGE"
````

create a new tag for your release:

````
			git tag 'YOUR_TAG'
````

and finally push your changes with the respective tags

````
			git push origin --tags
````

---

Some examples of common tags for android libraries are: '0.0.1', '1.0.0', '3.1.2'
