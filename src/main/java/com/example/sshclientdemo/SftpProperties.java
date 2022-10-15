package com.example.sshclientdemo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "demo.sftp")
public class SftpProperties {

    private int port;
    private String host;
    private String username;
    private String password;
    private String destination;

}
