plugins {
    kotlin("multiplatform") version "2.0.21"
    id("com.gorylenko.gradle-git-properties") version "2.4.1"
    id("org.gradlewebtools.minify") version "2.1.1"
    `maven-publish`
}

group = "ch.derlin"
version = "2.2.1-SNAPSHOT" // x-release-please-version

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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
    js(IR) {
        moduleName = "bitdowntoc" // name of the generated .js file
        browser {
            binaries.executable()
            distribution {
                outputDirectory = file("$projectDir/build/web")
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
                implementation("com.github.ajalt.clikt:clikt:5.0.1")
            }
            resources.setSrcDirs(resources.srcDirs.plus(project.layout.buildDirectory.dir("version")))
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit5"))
                implementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
                runtimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
                implementation("com.willowtreeapps.assertk:assertk-jvm:0.28.1")
            }
        }
        val jsMain by getting {
            // build/css populated via minification, see minification task
            resources.setSrcDirs(resources.srcDirs.plus("$projectDir/build/css"))
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

// Minify the CSS
// For it to work: (a) move the CSS file outside of resources (so it isn't copied to the prod bundle),
// (b) add the minification output task to the jsMain.resources.srcSets, (c) ensure the build/css output
// is generated *before* JS process resources.
minification {
    css {
        srcDir = project.file("$projectDir/src/jsMain/css")
        dstDir = project.file("$projectDir/build/css")
    }
}
tasks.named("jsProcessResources") {
    dependsOn += "cssMinify"
}


gitProperties {
    // It is currently not possible to read properties files from JS, so only do this in JVM
    gitPropertiesResourceDir.set(project.layout.buildDirectory.dir("version"))
    keys = listOf(
        "git.build.version",
        "git.branch",
        "git.commit.id",
        "git.commit.message.short",
        "git.commit.time",
        "git.dirty"
    )
}
tasks.named("jvmProcessResources").configure { dependsOn("generateGitProperties") }

tasks.register("bitdowntoc") {
    dependsOn("generateGitProperties", "jvmJar", "jsBrowserDistribution")
}

publishing {
    publications {
        create<MavenPublication>("default") {
            //from(components["java"])
        }
    }

    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/derlin/bitdowntoc")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
