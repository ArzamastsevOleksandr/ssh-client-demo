package com.example.sshclientdemo;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SftpOperations {

    public static final String SFTP_SERVER_SEPARATOR = "/";

    private final SftpProperties sftpProperties;

    @SneakyThrows
    public void upload(Path from) {
        try (SSHClient ssh = new SSHClient()) {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(sftpProperties.getHost(), sftpProperties.getPort());
            log.info("Connected to the SFTP server on host: {}, port: {}",
                    sftpProperties.getHost(), sftpProperties.getPort());
            try {
                ssh.authPassword(sftpProperties.getUsername(), sftpProperties.getPassword());
                log.info("Authenticated with the SFTP server on host: {}, port: {}",
                        sftpProperties.getHost(), sftpProperties.getPort());
                try (SFTPClient sftp = ssh.newSFTPClient()) {
                    sftp.put(from.toString(), SFTP_SERVER_SEPARATOR + sftpProperties.getDestination());
                    log.info("Uploaded to the SFTP server on host: {}, port: {} the file: {}",
                            sftpProperties.getHost(), sftpProperties.getPort(), from);
                }
            } finally {
                ssh.disconnect();
                log.info("Disconnected from the SFTP server on host: {}, port: {}",
                        sftpProperties.getHost(), sftpProperties.getPort());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public List<RemoteResourceInfo> listSftpResources() {
        try (SSHClient ssh = new SSHClient()) {
            ssh.addHostKeyVerifier(new PromiscuousVerifier());
            ssh.connect(sftpProperties.getHost(), sftpProperties.getPort());
            log.info("Connected to the SFTP server on host: {}, port: {}",
                    sftpProperties.getHost(), sftpProperties.getPort());
            try {
                ssh.authPassword(sftpProperties.getUsername(), sftpProperties.getPassword());
                log.info("Authenticated with the SFTP server on host: {}, port: {}",
                        sftpProperties.getHost(), sftpProperties.getPort());
                try (SFTPClient sftp = ssh.newSFTPClient()) {
                    return sftp.ls(SFTP_SERVER_SEPARATOR + sftpProperties.getDestination());
                }
            } finally {
                ssh.disconnect();
                log.info("Disconnected from the SFTP server on host: {}, port: {}",
                        sftpProperties.getHost(), sftpProperties.getPort());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
