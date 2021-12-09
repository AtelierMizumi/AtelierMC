import io.papermc.paperweight.util.constants.*
import io.papermc.paperweight.util.Git

plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.1.0" apply false
    id("io.papermc.paperweight.patcher") version "1.3.2-SNAPSHOT"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/") {
        content { onlyForConfigurations(PAPERCLIP_CONFIG) }
    }
}

dependencies {
    remapper("net.fabricmc:tiny-remapper:0.7.0:fat")
    decompiler("net.minecraftforge:forgeflower:1.5.498.22")
    paperclip("io.papermc:paperclip:3.0.2")
}

allprojects {
    apply(plugin = "java")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
    }
}

subprojects {
    tasks.withType<JavaCompile> {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }
        tasks.withType<Javadoc> {
            options.encoding = Charsets.UTF_8.name()
        }
        tasks.withType<ProcessResources> {
            filteringCharset = Charsets.UTF_8.name()
        }

    repositories {
        mavenCentral()
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://oss.sonatype.org/content/groups/public/")
        maven("https://papermc.io/repo/repository/maven-public/")
        maven("https://ci.emc.gs/nexus/content/groups/aikar/")
        maven("https://repo.aikar.co/content/groups/aikar")
        maven("https://repo.md-5.net/content/repositories/releases/")
        maven("https://hub.spigotmc.org/nexus/content/groups/public/")
        maven("https://jitpack.io")
    }
}

val pufferfishDir = layout.projectDirectory.dir("Pufferfish")
val initSubmodules by tasks.registering {
    outputs.upToDateWhen { false }
    doLast {
        Git(layout.projectDirectory)("submodule", "update", "--init").executeOut()
    }
}


paperweight {
    serverProject.set(project(":atelier-server"))

    remapRepo.set("https://maven.fabricmc.net/")
    decompileRepo.set("https://files.minecraftforge.net/maven/")

    upstreams {
        register("pufferfish") {
            upstreamDataTask {
                dependsOn(initSubmodules)
                projectDir.set(pufferfishDir)
            }

            patchTasks {
                register("api") {
                    upstreamDir.set(pufferfishDir.dir("pufferfish-api"))
                    patchDir.set(layout.projectDirectory.dir("patches/api"))
                    outputDir.set(layout.projectDirectory.dir("atelier-api"))
                }
                register("server") {
                    upstreamDir.set(pufferfishDir.dir("pufferfish-server"))
                    patchDir.set(layout.projectDirectory.dir("patches/server"))
                    outputDir.set(layout.projectDirectory.dir("atelier-server"))
                    importMcDev.set(true)
                }
            }
        }
    }
}