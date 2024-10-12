import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("disruptor.base-conventions")
    id("disruptor.publishing-conventions")
    alias(libs.plugins.spring.plugin.boot)
}

plugins.apply("io.spring.dependency-management")

tasks.named<BootJar>("bootJar") {
    enabled = false
}

dependencies {
    api(projects.disruptor.core)
    implementation(libs.spring.boot.autoconfigure)

    testImplementation(libs.spring.boot.starter.test)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
