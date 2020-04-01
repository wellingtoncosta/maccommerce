import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.71")
        classpath("org.flywaydb:flyway-gradle-plugin:6.0.4")
    }
}

plugins {
    application
    kotlin("jvm") version "1.3.71"
    id("org.flywaydb.flyway") version "6.0.4"
    id("org.unbroken-dome.test-sets") version "2.2.1"
}

group = "br.com.maccommerce"
version = "0.0.1"

repositories {
    jcenter()
    mavenCentral()
}

application {
    mainClassName = "br.com.maccommerce.storeservice.app.AppKt"
}

sourceSets {
    main {
        java {
            srcDirs("src/main/commons")
        }
    }
}

testSets {
    create("integrationTest")
}

val integrationTestImplementation = configurations["integrationTestImplementation"]
    .extendsFrom(configurations["testImplementation"])

val integrationTestRuntimeOnly = configurations["integrationTestRuntimeOnly"]
    .extendsFrom(configurations["testRuntimeOnly"])

tasks {
    withType<Jar> {
        manifest {
            attributes(
                mapOf(
                    "Main-Class" to application.mainClassName
                )
            )
        }

        from(configurations.runtimeClasspath.map { configuration ->
            configuration.asFileTree.map {
                if(it.isDirectory) it else zipTree(it)
            }
        })

        archiveFileName.set("${project.name}.jar")

        // removing signed files from jar
        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
    }

    withType<Test> {
        useJUnitPlatform { includeEngines("spek2") }
    }

    withType<KotlinCompile> {
        kotlinOptions.jvmTarget = "1.8"
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
    implementation("org.http4k:http4k-core:3.200.0")
    implementation("org.http4k:http4k-server-jetty:3.200.0")
    implementation("org.http4k:http4k-format-jackson:3.200.0")
    implementation("org.jetbrains.exposed:exposed:0.17.7")
    implementation("org.koin:koin-core:2.0.1")
    implementation("com.zaxxer:HikariCP:3.4.1")
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.postgresql:postgresql:42.2.8")
    implementation("org.flywaydb:flyway-core:6.1.0")
    implementation("io.azam.ulidj:ulidj:1.0.0")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
    testImplementation("io.mockk:mockk:1.9.3")
    testImplementation("io.zonky.test:embedded-postgres:1.2.6")
    testImplementation("org.junit.platform:junit-platform-engine:1.5.2")
    testImplementation("org.spekframework.spek2:spek-dsl-jvm:2.0.8") {
        exclude(group = "org.jetbrains.kotlin")
    }
    testRuntimeOnly("org.spekframework.spek2:spek-runner-junit5:2.0.8") {
        exclude(group = "org.junit.platform")
        exclude(group = "org.jetbrains.kotlin")
    }

    integrationTestImplementation("org.koin:koin-test:2.0.1")
    integrationTestImplementation("org.http4k:http4k-client-apache:3.200.0")
}
