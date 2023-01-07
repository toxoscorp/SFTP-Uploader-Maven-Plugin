# SFTP-Uploader - maven plugin

### **TESTED AND MADE ON WINDOWS. FOR A BEST MACOS EXPERIENCE USE THIS PROJECT ([sftpupload](https://github.com/Mrredstone5230/sftp-upload))**

this maven plugin allows to upload build artefact to a remote sftp server.

to use plugin on your project, you need to add the following repo to your pom.xml:

```xml
<pluginRepositories>
    <pluginRepository>
        <id>redserv-repo</id>
        <url>https://repo.redserv.net/repository/maven-public/</url>
    </pluginRepository>
</pluginRepositories>
```
and the following plugin to your build->plugins tag :  (Don't forget to replace the info in the  {} with your own)
```xml
<plugin>
        <groupId>me.toxo</groupId>
        <artifactId>sftpuploader</artifactId>
        <version>1.0</version>

        <executions>
            <execution>
                <id>SFTP uploading</id>
                <phase>package</phase>
                <goals>
                    <goal>upload</goal>
                </goals>
                <configuration>
                    <remotepath>{path to remote/name of the file}</remotepath>
                    <host>{sftp.host-address}</host>
                    <user>{sftp.username}</user>
                    <password>{sftp.password}</password>
                </configuration>
            </execution>
        </executions>
    </plugin>
```

if you want to use a custom port, file permissions, file group id or a custom build path you can add the following to the configuration tag:
```xml
<configuration>
    <port>{sftp.port}</port>
    <fileperms>{file permissions}</fileperms>
    <filegroupid>{file group id}</filegroupid>
    <buildpath>{path to build file to upload (the .jar)}</buildpath>
</configuration>
```

#### Example:
```xml
<pluginRepositories>
    <pluginRepository>
        <id>redserv-repo</id>
        <url>https://repo.redserv.net/repository/maven-public/</url>
    </pluginRepository>
</pluginRepositories>

<plugin>
<groupId>me.toxo</groupId>
<artifactId>sftpuploader</artifactId>
<version>1.0</version>

<executions>
    <execution>
        <id>SFTP uploading</id>
        <phase>package</phase>
        <goals>
            <goal>upload</goal>
        </goals>
        <configuration>
            <remotepath>/path/to/the/remote/example.jar</remotepath>
            <host>0.0.0.0</host>
            <user>{sftp.username}</user>
            <password>{sftp.password}</password>
            <port>33</port>
            <fileperms>0770</fileperms>
            <filegroupid>1001</filegroupid>
            <buildpath>/path/to/the/build/file/example-1.0-SNAPSHOT.jar</buildpath>
        </configuration>
    </execution>
</executions>
</plugin>
```

I also recommend the usage of the `properties-maven-plugin` plugin to add login info in an external config file (that you would .gitignore)