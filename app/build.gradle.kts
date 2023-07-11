import com.github.gradle.node.pnpm.task.PnpmTask
import de.undercouch.gradle.tasks.download.Download
import java.util.regex.Pattern

plugins {
    java
    id("gg.jte.gradle") version "3.0.0"
    id("com.github.node-gradle.node") version "5.0.0"
    id("de.undercouch.download") version "5.4.0"
}

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.apache.pdfbox:pdfbox:2.0.27")
    implementation("org.apache.pdfbox:pdfbox-tools:2.0.27")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")
    implementation("commons-io:commons-io:2.13.0")

    implementation("info.picocli:picocli:4.7.4")
    implementation("org.ethelred:ethelred_util:0.4")
    implementation("gg.jte:jte:3.0.1")
    implementation("org.jsoup:jsoup:1.15.4")
    compileOnly("io.soabase.record-builder:record-builder-core:35")
    annotationProcessor("io.soabase.record-builder:record-builder-processor:35")

    implementation("org.slf4j:slf4j-api:2.0.3")
    runtimeOnly("org.logevents:logevents:0.4.3")
    jteGenerate("gg.jte:jte-models:3.0.1")
    implementation("gg.jte:jte-models:3.0.1")

    implementation("com.github.SvenWoltmann:color-thief-java:master-SNAPSHOT") // jitpack build from https://github.com/SvenWoltmann/color-thief-java
    implementation("org.jsoup:jsoup:1.15.4")
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            // Use JUnit Jupiter test framework
            useJUnitJupiter("5.9.1")
        }
    }
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(20))
    }
}

jte {
    generate()
    jteExtension("gg.jte.models.generator.ModelExtension")
    packageName.set("org.ethelred.cuneiform.templates")
}

node {
    download.set(true)
    version.set("18.12.1")
}

val generateCss by tasks.registering(PnpmTask::class) {
    inputs.files("package.json", "tailwind.config.js")
    inputs.files(fileTree("src/main/jte"), fileTree("src/main/css"))
    outputs.file("${buildDir}/assets/style.css")
    dependsOn("pnpmInstall")
    args.set("exec tailwindcss -i src/main/css/input.css -o ${buildDir}/assets/style.css".split(Pattern.compile("\\s+")))
}

val baseDownloadUrl = "https://www.warhammer-community.com/wp-content/uploads"
val downloadPaths = listOf(
    "/2023/06/oF1iWIkNsvlUHByM.pdf",
    "/2023/06/KBvH5h3oY5QREpmG.pdf",
    "/2023/06/76CPCqo7msJIHqzx.pdf",
    "/2023/06/dLZIlatQJ3qOkGP7.pdf",
    "/2023/06/20OdtEKVLiE4H6Zo.pdf",
    "/2023/06/EE2Pdickp8sNe1NX.pdf",
    "/2023/06/H5pO90rzYSAY6dHG.pdf",
    "/2023/06/YWdVWS6bgzMSMsNo.pdf",
    "/2023/06/BrBEfwS94zTuHrZq.pdf",
    "/2023/06/ARyMPKx2JXprseBC.pdf",
    "/2023/06/kQ4OfkQB5G05ZNX4.pdf",
    "/2023/06/xOjVS3Asx2QJ13lk.pdf",
    "/2023/06/Ozcq0k1WInJbmhZV.pdf",
    "/2023/06/D2jWk0bUnG9zY6Gb.pdf",
    "/2023/06/TE5lPwmnUDrITuGM.pdf",
    "/2023/06/vkzQ2IBbrrCVNzz3.pdf",
    "/2023/06/4czxZwZf5cZCT7dk.pdf",
    "/2023/06/riFjIh9OeKg6AbLZ.pdf",
    "/2023/06/5I1cNt3t71dfd3jh.pdf",
    "/2023/06/2iVljh64k0hWMKsO.pdf",
    "/2023/06/JhAjl9vv4BcigNO9.pdf",
    "/2023/06/VdyiNhPdt8ehmIh6.pdf",
    "/2023/06/csv0IuVvYQAndBJE.pdf",
    "/2023/06/iiq5IN0DVsqWxFxh.pdf",
    "/2023/06/YC40Fxov5FhbXFRl.pdf",
    "/2023/06/BcWghehxrgeCmkN8.pdf",
    "/2023/06/C6o7G0zjRSxCUvhK.pdf",
    "/2023/06/u61I5H9K5r9oNsXZ.pdf",
    "/2023/06/uVN1M55L0U3dQeWZ.pdf",
    "/2023/06/L8FE4F808oEwCq9T.pdf",
    "/2023/06/NRqB9dxmiQDjknNV.pdf",
)

val downloadPdfs by tasks.registering(Download::class) {
    src {
        downloadPaths.map { baseDownloadUrl + it }
    }
    dest("$buildDir/pdfs")
    onlyIfModified(true)
    useETag(true)
    outputs.dir(dest)
}

val generatePages by tasks.registering(JavaExec::class) {
    dependsOn(downloadPdfs)
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass.set("com.ordoacerbus.cuneiform.App")
    args("--output-dir", "$buildDir/pages")
    if (project.hasProperty("skipImages")) {
        args("--html")
    }
    args(downloadPdfs.get().outputFiles.map { it.toString() })
    outputs.dir("$buildDir/pages")
}

val prepareDeploy by tasks.registering(Copy::class) {
    destinationDir = file("$buildDir/deploy")
    outputs.dir(destinationDir)
    from(generateCss) {
        into("assets")
    }
    from("src/main/resources/public")
    from(generatePages)
}

val remoteHost = "cuneiform"
val remotePath = "cuneiform.ordoacerbus.com"

val deploy by tasks.registering(Exec::class) {
    dependsOn(prepareDeploy)
    inputs.dir(prepareDeploy.get().destinationDir)
    commandLine("rsync", "-avz")
    args(prepareDeploy.get().destinationDir.listFiles().map {it.toString()})
    args("${remoteHost}:${remotePath}")
}