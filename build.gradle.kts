plugins {
    java
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.graalvm.buildtools.native") version "0.9.18"
    id("org.sonarqube") version "3.5.0.2730"
}

group = "org.rrajesh1979"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

val wavefrontVersion = "2.3.1"

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("org.springframework.boot:spring-boot-starter-web")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("org.modelmapper:modelmapper:3.1.0")
    implementation("com.google.guava:guava:31.1-jre")
    annotationProcessor("org.projectlombok:lombok")
    compileOnly("org.projectlombok:lombok")

//    implementation("org.springframework.boot:spring-boot-starter-log4j2:3.0.0")

    testImplementation("org.testcontainers:testcontainers:1.17.6")
    testImplementation("org.testcontainers:junit-jupiter:1.17.6")
    testImplementation("org.testcontainers:mongodb:1.17.6")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

//Sonar config
sonarqube {
    properties {
        property("sonar.projectKey", "rrajesh1979_java-spring-mongo-rest-ref")
        property("sonar.organization", "rrajesh1979")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}