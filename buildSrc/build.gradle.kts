plugins {
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

gradlePlugin {
    plugins {
        create("msjava") {
            id = "dev.magicspells.msjava"
            implementationClass = "dev.magicspells.gradle.MSJavaPlugin"
        }
        create("mspaperweight") {
            id = "dev.magicspells.mspaperweight"
            implementationClass = "dev.magicspells.gradle.MSPaperweight"
        }
    }
}
