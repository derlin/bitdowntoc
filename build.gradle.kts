plugins {
    kotlin("multiplatform") version "1.7.21"
}

group = "ch.derlin"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
        }
        withJava()

        // https://stackoverflow.com/a/61433514
        val jvmJar by tasks.getting(org.gradle.jvm.tasks.Jar::class) {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            doFirst {
                manifest {
                    attributes["Main-Class"] = "ch.derlin.bitdowntoc.MainKt"
                }
                from(configurations.getByName("runtimeClasspath").map { if (it.isDirectory) it else zipTree(it) })
            }
        }
    }
    js(LEGACY) {
        moduleName = "bitdowntoc" // name of the generated .js file
        browser {
            binaries.executable()
            distribution {
                directory = file("$projectDir/build/web")
            }
        }
    }

    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.github.ajalt.clikt:clikt:3.5.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter-api:5.9.1")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.1")
                implementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

tasks.register("bitdowntoc") {
    dependsOn("jvmJar", "jsBrowserDistribution")
}
