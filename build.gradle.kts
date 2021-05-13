plugins {
    kotlin("multiplatform") version "1.5.0"
}

group = "doist"
version = "1.0"


repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
    }
    js(IR) {
        browser {
            commonWebpackConfig {
                cssSupport.enabled = false
            }
            testTask {
                useKarma {
                    useChromiumHeadless()
                }
            }
        }
    }
    ios()
    macosX64()
    mingwX64()

    val linuxHost = System.getProperty("os.name") == "Linux"
    if (linuxHost) {
        // Disable cross-compilation for Linux, akin to macOS and Windows.
        linuxX64 {
            val main by compilations.getting
            val interop by main.cinterops.creating {
                defFile = project.file("src/linuxX64Interop/cinterop/uninorm.def")
            }

            binaries {
                executable()
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
        val jvmMain by getting
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
        val jsMain by getting
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
        val nativeDarwinMain by creating {
            dependsOn(commonMain)
        }
        val iosArm64Main by getting {
            dependsOn(nativeDarwinMain)
        }
        val iosX64Main by getting {
            dependsOn(nativeDarwinMain)
        }
        val macosX64Main by getting {
            dependsOn(nativeDarwinMain)
        }
        val mingwX64Main by getting
        if (linuxHost) {
            val linuxX64Main by getting
        }
    }
}
