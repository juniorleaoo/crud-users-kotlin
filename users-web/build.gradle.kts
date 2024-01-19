import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.21"
	kotlin("plugin.spring") version "1.9.21"
	kotlin("plugin.jpa") version "1.9.21"
}

group = "com.crud"
version = "0.0.1-SNAPSHOT"
val junitVersion = "5.8.1"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.junit.jupiter:junit-jupiter:$junitVersion")
	testImplementation("org.junit.jupiter:junit-jupiter-params:$junitVersion")
	runtimeOnly("org.postgresql:postgresql")
	runtimeOnly("com.oracle.database.jdbc:ojdbc11")
	implementation("org.flywaydb:flyway-core:10.6.0")
	implementation("org.flywaydb:flyway-database-oracle:10.6.0")

	testImplementation("org.testcontainers:testcontainers:1.19.3")
	testImplementation("org.testcontainers:postgresql:1.19.3")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
