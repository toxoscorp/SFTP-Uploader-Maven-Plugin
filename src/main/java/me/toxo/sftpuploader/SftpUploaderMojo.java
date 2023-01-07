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
    String user;
    @Parameter(property = "password", required = true)
    String password;
    @Parameter(property = "port", defaultValue = "22", required = true)
    String port;
    @Parameter(property = "remotepath", required = true)
    String remotepath;
    @Parameter(property = "buildpath")
    String buildpath;
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject project;
    @Parameter(property = "fileperms")
    String fileperms;
    @Parameter(property = "filegroupid")
    String filegroupid;

    public void execute() throws MojoExecutionException, MojoFailureException {
//        getLog().info("Uploading to " + host + ":" + port + " as " + username);
        if (buildpath == null || buildpath.isEmpty()) {
            this.buildpath = this.project.getBuild().getDirectory() + "\\" + this.project.getBuild().getFinalName() + ".jar";
        }
        getLog().info("Uploading from " + buildpath);
        getLog().info("Uploading to " + this.remotepath);

        try {
            if (this.fileperms != null) {
                Integer.parseInt(this.fileperms, 8);
            }
            if (this.filegroupid != null) {
                Integer.parseInt(this.filegroupid);
            }

            SSHClient client = connect();
            SFTPClient sftpClient = client.newSFTPClient();

            sftpClient.put(this.buildpath, this.remotepath);

            if (this.fileperms != null) {
                sftpClient.chmod(this.remotepath, Integer.parseInt(this.fileperms, 8));
                getLog().info("Set permissions to " + this.fileperms);
            }
            if (this.filegroupid != null) {
                sftpClient.chgrp(this.remotepath, Integer.parseInt(this.filegroupid));
                getLog().info("Set group id to " + this.filegroupid);
            }

            getLog().info("Upload complete");
            sftpClient.close();
            client.disconnect();
            getLog().info("Disconnected");
        } catch (IOException e) {
            getLog().error("Error Uploading", e);
        } catch (NumberFormatException e) {
            throw new MojoExecutionException("Error parsing permissions or group id", e);
        }
    }

    private SSHClient connect() throws IOException {
        SSHClient client = new SSHClient();
        client.addHostKeyVerifier(new PromiscuousVerifier());
        client.connect(this.host, Integer.parseInt(this.port));
        client.authPassword(this.user, this.password);
        return client;
    }
}
