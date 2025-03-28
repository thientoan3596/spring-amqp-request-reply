tasks.register("bootAll") {
    dependsOn(
            ":gateway:bootJar",
            ":item-service:bootJar",
    )
}