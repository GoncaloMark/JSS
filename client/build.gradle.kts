plugins {
    java
}

tasks.register<JavaExec>("runClient") {
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("client.Client")
    standardInput = System.`in`
}