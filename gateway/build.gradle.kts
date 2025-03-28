import org.gradle.kotlin.dsl.*
plugins {
    id("java")
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
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
    maven { url = uri("https://repo.spring.io/snapshot") }
}
dependencies {
    implementation(platform(libs.spring.cloud.dependencies))
    implementation("org.springframework.cloud:spring-cloud-starter-stream-rabbit")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    implementation( project(":common-lib"))
}
tasks.bootJar{
    archiveFileName.set("gateway.jar")
}