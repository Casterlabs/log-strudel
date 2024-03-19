A Java-17 log daemon.

## Repository

We use GitHub packages + our own resolver for our deployment and hosting.

<details>
  <summary>Maven</summary>
  
  ```xml
  <repositories>
    <repository>
      <id>casterlabs-maven</id>
      <url>https://repo.casterlabs.co/maven</url>
    </repository>
  </repositories>
  ```
</details>

<details>
  <summary>Gradle</summary>
  
  ```gradle
allprojects {
	repositories {
		maven { url 'https://repo.casterlabs.co/maven' }
	}
}
  ```
</details>

<details>
  <summary>SBT</summary>
  
  ```
resolvers += "casterlabs-maven" at "https://repo.casterlabs.co/maven"
  ```
</details>

<details>
  <summary>Leiningen</summary>
  
  ```
:repositories [["casterlabs-maven" "https://repo.casterlabs.co/maven"]]
  ```
</details>

## Adding to your project

Replace `VERSION_OR_HASH` with the latest release tag or commit in this repo and make sure to add the [Repository](https://github.com/Casterlabs/log-strudel#Repository) to your build system.

<details>
  <summary>Maven</summary>
  
  ```xml
    <dependency>
        <groupId>co.casterlabs.log_strudel</groupId>
        <artifactId>java_client</artifactId>
        <version>VERSION_OR_HASH</version>
    </dependency>
  ```
</details>

<details>
  <summary>Gradle</summary>
  
  ```gradle
	dependencies {
        implementation 'co.casterlabs.log_strudel:java_client:VERSION_OR_HASH'
	}
  ```
</details>

<details>
  <summary>SBT</summary>
  
  ```
libraryDependencies += "co.casterlabs.log_strudel" % "java_client" % "VERSION_OR_HASH"
  ```
</details>

<details>
  <summary>Leiningen</summary>
  
  ```
:dependencies [[co.casterlabs.log_strudel/java_client "VERSION_OR_HASH"]]	
  ```
</details>

Or, simply make a POST request to the [endpoint](https://www.postman.com/casterlabs/workspace/casterlabs-api/request/11546462-028e46df-1fb9-473d-97af-da80d9806d9f?action=share&creator=11546462&ctx=documentation).

## Used by

- Us :^)

_Want your project included here? Open an issue and we'll add you ❤._

## Development

This project utilizes Lombok for code generation (e.g Getters, Setters, Constructors), in order for your IDE to properly detect this, you'll need to install the Lombok extension. Instructions can be found [here](https://projectlombok.org/setup/) under "IDEs".
