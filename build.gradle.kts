import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

import org.gradle.api.plugins.ExtensionAware

import org.junit.platform.gradle.plugin.FiltersExtension
import org.junit.platform.gradle.plugin.EnginesExtension
import org.junit.platform.gradle.plugin.JUnitPlatformExtension

buildscript {
	repositories {
		mavenCentral()
        jcenter()
	}

    val kotlinVersion by extra { "1.2.50" }
    val springBootVersion by extra { "2.0.3.RELEASE" }

	dependencies {
		classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
		classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
		classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("org.junit.platform:junit-platform-gradle-plugin:1.0.0")
	}
}

plugins {
    kotlin("jvm").version("1.2.51")
    application
    java
    id("io.gitlab.arturbosch.detekt").version("1.0.0.RC8")
}

apply {
    plugin("kotlin")
    plugin("kotlin-spring")
    plugin("eclipse")
    plugin("org.springframework.boot")
    plugin("io.spring.dependency-management")
    plugin("org.junit.platform.gradle.plugin")
}

application {
    group = "ru.jmorozov"
    version = "0.0.1-SNAPSHOT"
    applicationName = "prod-calendar"
    mainClassName = "ru.jmorozov.prodkalendar.ProdKalendarApplicationKt"
}

configure<JavaPluginConvention> {
    setSourceCompatibility(1.8)
    setTargetCompatibility(1.8)
}

configure<JUnitPlatformExtension> {
    filters {
        engines {
            include("spek")
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.freeCompilerArgs = listOf("-Xjsr305=strict")
    kotlinOptions.jvmTarget = "1.8"
}

repositories {
	mavenCentral()
    jcenter()
}

dependencies {
	compile("org.springframework.boot:spring-boot-starter-web")
	compile("com.fasterxml.jackson.module:jackson-module-kotlin")
	compile("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	compile("org.jetbrains.kotlin:kotlin-reflect")
    compile("org.jsoup:jsoup:1.11.3")
    compile("org.apache.commons:commons-csv:1.4")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.+")
    compile("org.springframework.boot:spring-boot-starter-cache")
    compile("org.ehcache:ehcache")
    compile("javax.cache:cache-api")

	testCompile("org.springframework.boot:spring-boot-starter-test")
    testCompile("org.jetbrains.spek:spek-api:1.1.5")
    testRuntime("org.jetbrains.spek:spek-junit-platform-engine:1.1.5")
    testImplementation("org.amshove.kluent:kluent:1.40")

    detekt("io.gitlab.arturbosch.detekt:detekt-formatting:1.0.0.RC8")
}

// extension for configuration
fun JUnitPlatformExtension.filters(setup: FiltersExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(FiltersExtension::class.java).setup()
        else -> throw Exception("${this::class} must be an instance of ExtensionAware")
    }
}
fun FiltersExtension.engines(setup: EnginesExtension.() -> Unit) {
    when (this) {
        is ExtensionAware -> extensions.getByType(EnginesExtension::class.java).setup()
        else -> throw Exception("${this::class} must be an instance of ExtensionAware")
    }
}

detekt {
    version = "1.0.0.RC8"
    profile("main", Action {
        input = "src/main/kotlin"
        config = file("detekt.yml")
        filters = ".*/resources/.*,.*/tmp/.*"
        output = "reports"
        outputName = "detekt-report"
        baseline = "reports/baseline.xml"
    })
}