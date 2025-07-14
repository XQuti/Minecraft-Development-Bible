plugins {
    java
    id("org.springframework.boot") version "3.5.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("com.github.ben-manes.versions") version "0.52.0"
    id("org.owasp.dependencycheck") version "12.1.0"
    checkstyle
}

group = "io.xquti"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_24
    targetCompatibility = JavaVersion.VERSION_24
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    
    // Security enhancements
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    
    // SECURITY: OWASP HTML Sanitizer for XSS prevention
    implementation("com.googlecode.owasp-java-html-sanitizer:owasp-java-html-sanitizer:20220608.1")
    
    // JWT dependencies
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    
    // PostgreSQL driver
    runtimeOnly("org.postgresql:postgresql")
    
    // Development tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    
    // OpenAPI/Swagger documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")
    
    // Elasticsearch
    implementation("org.springframework.boot:spring-boot-starter-data-elasticsearch")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    

    
    // Explicit Mockito and Byte Buddy versions for Java 24 compatibility
    testImplementation("org.mockito:mockito-core:5.18.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.18.0")
    testImplementation("net.bytebuddy:byte-buddy:1.17.6")
    testImplementation("net.bytebuddy:byte-buddy-agent:1.17.6")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/java.text=ALL-UNNAMED",
        "--add-opens", "java.desktop/java.awt.font=ALL-UNNAMED",
        "--enable-native-access=ALL-UNNAMED"
    )
    systemProperty("java.awt.headless", "true")
    maxHeapSize = "2g"
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf(
        "-Xlint:deprecation",
        "-Xlint:unchecked",
        "-parameters"
    ))
    options.encoding = "UTF-8"
}

// Configure Checkstyle
checkstyle {
    toolVersion = "10.12.4"
    configFile = file("config/checkstyle/checkstyle.xml")
    configProperties = mapOf(
        "suppressionFile" to file("config/checkstyle/checkstyle-suppressions.xml").absolutePath
    )
    isIgnoreFailures = true
    maxWarnings = 100
    maxErrors = 10
}

tasks.named("checkstyleMain") {
    group = "verification"
    description = "Run Checkstyle analysis for main source set"
}

tasks.named("checkstyleTest") {
    group = "verification"
    description = "Run Checkstyle analysis for test source set"
}

// Configure OWASP Dependency Check
dependencyCheck {
    format = "ALL"
    suppressionFile = "config/dependency-check-suppressions.xml"
    failBuildOnCVSS = 8.0f // Only fail on critical vulnerabilities
    analyzers {
        assemblyEnabled = false
        nuspecEnabled = false
        nodeEnabled = false
        nodeAuditEnabled = false
    }
    nvd {
        apiKey = System.getenv("NVD_API_KEY") ?: ""
        delay = if (System.getenv("NVD_API_KEY").isNullOrEmpty()) 16000 else 0
        validForHours = 24
    }
    skipConfigurations = listOf("developmentOnly", "testImplementation", "testRuntimeOnly")
}