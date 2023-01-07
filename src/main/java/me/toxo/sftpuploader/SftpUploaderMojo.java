package me.toxo.sftpuploader;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;

@Mojo(name = "upload", defaultPhase = LifecyclePhase.PACKAGE)
public class SftpUploaderMojo extends AbstractMojo {
    @Parameter(property = "host", required = true)
    String host;
    @Parameter(property = "user", required = true)
    String username;
    @Parameter(property = "password", required = true)
    String password;
    @Parameter(property = "port", defaultValue = "22", required = true)
    String port;
    @Parameter(property = "remotePath", required = true)
    String remotePath;
    @Parameter(property = "localPath", required = false)
    String localPath;
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;
    @Parameter(property = "FilePerms", required = false)
    String permissions;
    @Parameter(property = "FileGroupId", required = false)
    String fileGroupId;

    public void execute() throws MojoExecutionException, MojoFailureException {
//        getLog().info("Uploading to " + host + ":" + port + " as " + username);
        if (localPath == null || localPath.isEmpty()) {
            this.localPath = this.project.getBuild().getDirectory() + "\\" + this.project.getBuild().getFinalName() + ".jar";
        }
        getLog().info("Uploading from " + localPath);
        getLog().info("Uploading to " + remotePath);

        try {
            SSHClient client = connect();
            SFTPClient sftpClient = client.newSFTPClient();

            sftpClient.put(this.localPath, this.remotePath);

            if (this.permissions != null && !this.permissions.isEmpty() && isInteger(this.permissions)) {
                sftpClient.chmod(this.remotePath, Integer.parseInt(this.permissions, 8));
            }
            if (this.fileGroupId != null && !this.fileGroupId.isEmpty() && isInteger(this.fileGroupId)) {
                sftpClient.chgrp(this.remotePath, Integer.parseInt(this.fileGroupId));
            }

            getLog().info("Upload complete");
            sftpClient.close();
            client.disconnect();
            getLog().info("Disconnected");
        } catch (IOException e) {
            getLog().error("Error Uploading", e);
        }
    }

    private SSHClient connect() throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(this.host, Integer.parseInt(this.port));
        client.authPassword(this.username, this.password);
        return client;
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }
}
