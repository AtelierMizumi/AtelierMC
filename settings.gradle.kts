pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://papermc.io/repo/repository/maven-public/")
    }
}

rootProject.name = "ateliermc"
for (name in listOf("AtelierMC-API", "AtelierMC-Server")) {
    val projName = name.toLowerCase(java.util.Locale.ENGLISH)
    include(projName)
    findProject(":$projName")!!.projectDir = file(name)
}
