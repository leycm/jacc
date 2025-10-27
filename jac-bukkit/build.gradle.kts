dependencies {
    implementation(project(":api"))
    implementation(project(":common"))
    implementation(libs.leyneck)
    implementation(libs.leyflux)

    compileOnly(libs.spigot)

    compileOnly(libs.jetanno)
}