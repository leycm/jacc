dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(libs.leyneck)
    implementation(libs.leyflux)


    compileOnly(libs.jetanno)

    compileOnly(libs.slf4j)
    compileOnly(libs.logback)
    compileOnly("org.apache.logging.log4j:log4j-api:2.21.0")
    compileOnly("org.apache.logging.log4j:log4j-core:2.21.0")

    compileOnly(libs.spigot)
}