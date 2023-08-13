import java.net.URI




pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = URI("https://maven.neshan.org/artifactory/public-maven") }
        maven { url =  URI("https://oss.sonatype.org/content/repositories/ksoap2-android-releases/") }
    }
}

rootProject.name = "Amir Chat"
include(":app")
include(":emojikeyboard2")
