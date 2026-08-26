import net.fabricmc.loom.LoomGradleExtension
import net.fabricmc.loom.api.LoomGradleExtensionAPI

plugins {
	id("net.fabricmc.fabric-loom")
	id("maven-publish")
	id("org.jetbrains.kotlin.jvm")
	id("net.fabricmc.fabric-loom-remap") version "1.14-SNAPSHOT"
}

version = "${property("mod_version")}+${sc.current.version}"
base.archivesName = property("archives_base_name") as String

repositories {

	maven("https://maven.wispforest.io/releases/")

	// since owo 0.13.0, jitpack is required for kdl4j
	maven("https://jitpack.io")

	maven("https://maven.terraformersmc.com/") {
		name = "Terraformers"
	}

	maven("https://maven.meteordev.org/releases") {
		name = "meteor-maven"
	}

	maven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")


	mavenCentral()
}

loom {
	splitEnvironmentSourceSets()


	mods {
		create("ping-offset-miner") {
			sourceSet(sourceSets.main.get())
			sourceSet(sourceSets.named("client").get())
		}
	}
}

fabricApi {
	configureDataGeneration {
		client.set(true)
	}
}

configurations {
	named("clientImplementation") {
		extendsFrom(configurations.getByName("implementation"))
	}
}

val devauthVersion = "1.2.2"


dependencies {
	runtimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.2")
	minecraft("com.mojang:minecraft:${sc.current.version}")

	// fabric api
	implementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_api_version")}")

	// fabric loader
	implementation("net.fabricmc:fabric-loader:${property("loader_version")}")

	// owo
	implementation("io.wispforest:owo-lib:${property("owo_version")}")
	include("io.wispforest:owo-sentinel:${property("owo_version")}")


	// orbit
	implementation("meteordevelopment:orbit:${property("orbit_version")}")
	annotationProcessor("meteordevelopment:orbit:${property("orbit_version")}")
	include("meteordevelopment:orbit:${property("orbit_version")}")

	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.processResources {
	val props = mapOf(
		"version" to project.version,
		"minecraft" to (project.findProperty("mod.mc_compat") ?: project.property("minecraft_version"))
	)

	inputs.properties(props)

	filesMatching("fabric.mod.json") {
		expand(props)
	}
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(25)
}

java {
	withSourcesJar()
}

tasks.jar {
	inputs.property("archivesName", base.archivesName)

	from("LICENSE") {
		rename { "${it}_${inputs.properties["archivesName"]}" }
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			artifactId = property("archives_base_name").toString()
			from(components["java"])
		}
	}
}

kotlin {
	jvmToolchain(25)
}