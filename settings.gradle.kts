pluginManagement {
	repositories {

		maven("https://maven.fabricmc.net/") {
			name = "Fabric"
		}
		mavenCentral()
		gradlePluginPortal()

	}
	plugins {
		id("org.jetbrains.kotlin.jvm") version "2.3.10"
		id("net.fabricmc.fabric-loom") version "1.17.19"
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.9.7"
}

stonecutter {
	// ...
	// Configuration goes here

	create(rootProject) {
		versions("26.1", "26.2")
	}
}