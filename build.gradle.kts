plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.3.1"
}

group = "com.vfcastro.dev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.5-R0.1-SNAPSHOT")
    implementation("com.zaxxer:HikariCP:6.3.0")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks {
    runServer {
        minecraftVersion("1.21")
    }

    processResources {
        val props = mapOf("version" to version)
        inputs.properties(props)
        filteringCharset = "UTF-8"
        filesMatching("paper-plugin.yml") {
            expand(props)
        }
    }

    register<Copy>("copyPluginJar") {
        dependsOn(named("shadowJar"))
        from(layout.buildDirectory.dir("libs"))
        include("${project.name}-${project.version}-all.jar")
        into(file(System.getProperty("user.home") + "/Projects/minecraft/servers/1.25/plugins"))
    }

    build {
        dependsOn("shadowJar")
        finalizedBy("copyPluginJar")
    }

    shadowJar {
        archiveClassifier.set("all")
    }
}
