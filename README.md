# VersionInheritancePlugin

[![Build Status](http://ci.inventivetalent.org/job/VersionInheritancePlugin/badge/icon)](http://ci.inventivetalent.org/job/VersionInheritancePlugin/)

Maven plugin to inherit the parent version in modules

## Usage
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.inventivetalent</groupId>
            <artifactId>version-inheritance-maven-plugin</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <configuration>
                <parentPom>./pom.xml</parentPom><!-- Path to the parent pom.xml to get the version from -->
                <modulePoms>
                    <!-- Paths to the module pom.xml to copy the version to  -->
                    <pom>./module1/pom.xml</pom>
                    <pom>./module2/pom.xml</pom>
                </modulePoms>
            </configuration>
        </plugin>
    </plugins>
</build>
<pluginRepositories>
    <pluginRepository>
        <id>inventive-repo</id>
        <url>https://repo.inventivetalent.org/content/groups/public/</url>
    </pluginRepository>
</pluginRepositories>
<modules>
    <module>module1</module>
    <module>module2</module>
</modules>
```

* [Example Project](https://github.com/InventivetalentDev/VersionInheritancePlugin-Test)
