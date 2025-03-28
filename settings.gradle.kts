rootProject.name = "amqp-request-reply"
include("common-lib",  "gateway", "item-service")
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
    plugins {
        id("org.gradle.plugin.management") version "0.13.1"
    }
}
