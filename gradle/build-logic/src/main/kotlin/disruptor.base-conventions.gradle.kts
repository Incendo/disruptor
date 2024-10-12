plugins {
    id("org.incendo.cloud-build-logic")
    id("org.incendo.cloud-build-logic.spotless")
    `java-library`
}

indra {
    javaVersions {
        minimumToolchain(21)
        target(21)
        testWith().set(setOf(21))
    }
    checkstyle().set(libs.versions.checkstyle)
}

cloudSpotless {
    ktlintVersion = libs.versions.ktlint
}

spotless {
    java {
        importOrderFile(rootProject.file(".spotless/disruptor.importorder"))
    }
}

dependencies {
    // Common dependencies.
    api(libs.slf4j)
    api(libs.jspecify)
    api(libs.apiguardian)

    // Test dependencies
    testImplementation(libs.truth)
}
