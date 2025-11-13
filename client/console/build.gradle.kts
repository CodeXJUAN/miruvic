plugins {
    id("buildlogic.java-application-conventions")
}

dependencies {
    implementation(project(":utils"))
    implementation(project(":model"))
    implementation("com.github.freva:ascii-table:1.8.0")
    implementation("com.athaydes.rawhttp:rawhttp-core:2.6.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.20.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.17.2")
}

application {
    mainClass = "cat.uvic.teknos.dam.miruvic.client.ConsoleClient"
}