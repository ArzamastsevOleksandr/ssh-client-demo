package com.example.sshclientdemo;

import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.api.model.Ports;
import net.schmizz.sshj.sftp.RemoteResourceInfo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
class SshClientDemoApplicationTests {

    @TempDir
    static File tempDirectory;

    static File tempFileToUpload;

    @Autowired
    SftpOperations sftpOperations;
    @Autowired
    SftpProperties sftpProperties;

    @Container
    static final GenericContainer<?> SFTP = new GenericContainer<>("atmoz/sftp:latest")
            .withExposedPorts(22)
            .withCreateContainerCmdModifier(cmd -> cmd.withHostConfig(
                    new HostConfig().withPortBindings(new PortBinding(Ports.Binding.bindPort(22), new ExposedPort(22)))
            ))
            .withCommand("user:pass:::home");

    @BeforeAll
    static void start() {
        SFTP.start();
        tempFileToUpload = new File(tempDirectory, "tempFileToUpload.txt");
    }

    @AfterAll
    static void stop() {
        SFTP.stop();
    }

    @Test
    void uploadFileToSftpAndVerifyItsPresence() throws IOException {
        // given
        Files.write(tempFileToUpload.toPath(), List.of("Some", "dumb", "content"));
        assertThat(sftpOperations.listSftpResources()).isEmpty();
        // when
        sftpOperations.upload(tempFileToUpload.toPath());
        // then
        List<String> resourceNames = sftpOperations.listSftpResources()
                .stream()
                .map(RemoteResourceInfo::getName)
                .toList();

        assertThat(resourceNames)
                .hasSize(1)
                .containsExactlyInAnyOrder(tempFileToUpload.getName());
    }

}
