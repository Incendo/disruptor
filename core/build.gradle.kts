plugins {
    id("disruptor.base-conventions")
    id("disruptor.publishing-conventions")
}

dependencies {
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform)
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
