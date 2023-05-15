plugins {
    java
}

tasks.register<JavaExec>("runServer") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("server.Server")
    standardInput = System.`in`
}