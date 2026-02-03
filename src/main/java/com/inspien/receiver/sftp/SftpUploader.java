package com.inspien.receiver.sftp;

import com.inspien.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.integration.sftp.session.SftpRemoteFileTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class SftpUploader {

    private final SftpRemoteFileTemplate template;
    private final String remoteDir;

    public SftpUploader(SftpRemoteFileTemplate template,
                        @Value("${sftp.remote-dir}") String remoteDir) {
        this.template = template;
        this.remoteDir = remoteDir;
    }

    public void upload(Path localFile) {
        if (localFile == null || !Files.exists(localFile)) {
            throw ErrorCode.FILE_NOT_FOUND.exception();
        }

        template.execute(session -> {
            try {
                if (!session.exists(remoteDir)) {
                    session.mkdir(remoteDir);
                }

                String remotePath = remoteDir + "/" + localFile.getFileName();

                try (InputStream is = Files.newInputStream(localFile)) {
                    session.write(is, remotePath);
                }

                return null;
            } catch (Exception e) {
                throw ErrorCode.SFTP_UPLOAD_FAIL.exception();
            }
        });
    }
}

