import org.incendo.cloudbuildlogic.city

plugins {
    id("org.incendo.cloud-build-logic.publishing")
}

indra {
    github("Incendo", "disruptor") {
        ci(true)
    }
    mitLicense()

    configurePublications {
        pom {
            developers {
                city()
            }
        }
    }
}
