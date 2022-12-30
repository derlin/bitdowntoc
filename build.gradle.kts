plugins {
    kotlin("multiplatform") version "1.7.21"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
}

group = "ch.derlin"
version = "1.1.1-SNAPSHOT" // x-release-please-version

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
            duplicatesStrategy = DuplicatesStrategy.WARN
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


gitProperties {
    // It is currently not possible to read properties files from JS, so only do this in JVM
    gitPropertiesResourceDir.set(project.buildDir.resolve("processedResources/jvm/main"))
    keys = listOf("git.build.version", "git.branch", "git.commit.id", "git.commit.message.short", "git.commit.time", "git.dirty")
}
// Avoid the warning:
// > Gradle detected a problem with the following location: '/Users/lucy/git/bitdowntoc-multi/build/processedResources/jvm/main'.
// > Reason: Task ':jvmProcessResources' uses this output of task ':generateGitProperties' without declaring an explicit or
// > implicit dependency. This can lead to incorrect results being produced, depending on what order the tasks are executed.
tasks.named("generateGitProperties").configure { dependsOn(tasks.named("jvmProcessResources")) }

tasks.register("bitdowntoc") {
    dependsOn("generateGitProperties", "jvmJar", "jsBrowserDistribution")
}
