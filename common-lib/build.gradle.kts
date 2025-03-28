import org.gradle.kotlin.dsl.*
plugins {
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    id("java")
}
group = "org.thluon.java"
version = "1.0-SNAPSHOT"
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
    }
}
repositories {
    mavenCentral()
}
dependencies {
    implementation(platform(libs.spring.cloud.dependencies))
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
