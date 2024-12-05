plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("engine.Core") // 메인 클래스 이름
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "engine.Core" // 메인 클래스 경로
    }
    from("res") {
        into("")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
}

tasks.test {
    useJUnitPlatform()
}

// Gradle이 src 디렉토리를 소스 루트로 인식하도록 설정
java {
    sourceSets["main"].java.srcDirs("src")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        jvmTarget = "17" // JVM 타겟을 Java 17로 설정
    }
}

tasks.test {
    useJUnitPlatform()
}

