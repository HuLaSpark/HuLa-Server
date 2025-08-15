<?xml version="1.0" encoding="UTF-8"?>
<project xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <groupId>${pg.groupId}</groupId>
        <artifactId>${projectPrefix}-${serviceName}</artifactId>
        <version>${pg.version}</version>
        <relativePath>../pom.xml</relativePath>
    </parent>

    <modelVersion>4.0.0</modelVersion>
    <artifactId>${projectPrefix}-${serviceName}-facade</artifactId>
    <name>${r"${"}project.artifactId${r"}"}</name>
    <description>${pg.description}服务-facade层</description>

    <packaging>pom</packaging>

    <modules>
        <module>${projectPrefix}-${serviceName}-api</module>
        <module>${projectPrefix}-${serviceName}-boot-impl</module>
        <module>${projectPrefix}-${serviceName}-cloud-impl</module>
    </modules>
</project>
