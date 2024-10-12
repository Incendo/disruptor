plugins {
    id("disruptor.base-conventions")
    id("disruptor.publishing-conventions")
}

dependencies {
    api(projects.disruptor.core)
    api(libs.feign.core)

    testImplementation(libs.wiremock)
    testImplementation(libs.feign.java11)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
