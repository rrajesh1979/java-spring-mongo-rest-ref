import org.gradle.internal.classpath.Instrumented.systemProperty

plugins {
    java
    id("org.springframework.boot") version "3.0.0"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.graalvm.buildtools.native") version "0.9.18"
    id("jacoco")
    id("org.sonarqube") version "3.5.0.2730"

    //jib to build container image
    id("com.google.cloud.tools.jib") version "3.3.1"
}

group = "org.rrajesh1979"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

//compileJava {
//    options.compilerArgs << '-parameters'
//}

jib {
    to {
        image = "registry.hub.docker.com/rrajesh1979/url-shortener"
    }
    container {
        ports = listOf("8000")
        jvmFlags = listOf("-Dspring.profiles.active=container")
    }
}

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

    //Observability related
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    runtimeOnly("io.micrometer:micrometer-registry-prometheus:1.10.2")
    implementation("io.micrometer:micrometer-tracing-bridge-brave:1.0.0")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave:2.16.3")
    implementation("com.github.loki4j:loki-logback-appender:1.4.0-rc1")

    //Caching related
    implementation("org.springframework.boot:spring-boot-starter-cache")

    //OpenAPI related - use springdoc-openapi-starter-webmvc-ui instead of springdoc-openapi-ui t
    //to work with Spring Boot 3.0.0
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.0")
    //implementation("org.springdoc:springdoc-openapi-ui:1.6.13")

    //Rate Limiting related
    implementation("com.github.vladimir-bukhtoyarov:bucket4j-core:7.6.0")

}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
}

//Sonar config
sonarqube {
    properties {
        property("sonar.projectKey", "rrajesh1979_java-spring-mongo-rest-ref")
        property("sonar.organization", "rrajesh1979")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}

tasks.test {
    finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}
tasks.jacocoTestReport {
    dependsOn(tasks.test)   // tests are required to run before generating the report
    reports {
        xml.required.set(true)
        csv.required.set(true)
        html.outputLocation.set(layout.buildDirectory.dir("jacocoHtml"))
    }
}

//Custom Gradle Task which sets "spring.profiles.active" as "container"
//TODO: Fix this. Need to find a better way to use custom profile
//The container jvmFlags is working.
// I don't believe the below doFirst configuration is required.
// Need to test.
tasks.register("jibCustom") {
    group = "jib"
    doFirst {
        tasks.jib.configure {
            systemProperty("spring.profiles.active", "container", "jib")
        }
    }
    finalizedBy(tasks.jib)
}