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
    implementation("org.springframework:spring-web")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation(project(":common-lib"))
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
tasks.bootJar{
    archiveFileName.set("item-service.jar")
}