package com.spmisvt.codeservey.controller;

import com.spmisvt.codeservey.utils.DockerWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
@Slf4j
public class CompilerController {
    @PostMapping("python")
    public List<String> pythonCompiler(@RequestPart(value = "sourceCode", required = false) MultipartFile sourceCode) throws RuntimeException {
        String folder = "DockerFiles";
        createEntrypointForPython(folder);
        try {
            saveFile(sourceCode, folder + "/main.py");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
        String dockerImageName = String.format("compiler-id-%s", new Date().getTime());
        log.info("Build docker image");

        DockerWorker docker = new DockerWorker(dockerImageName);

        boolean status;

        try {
            status = docker.createImage(folder);
            if (status) {
                log.info("docker image created");
            } else {
                log.error("Error creating docker image");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<String> response;

        log.info("running docker");
        try {
           response = docker.runImage();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("delete docker image");
        try {
            status = docker.deleteImage();
            if (status) {
                log.info("docker container deleting");
            } else {
                log.error("Error deleting docker container");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    private void createEntrypointForPython(String folder) {
        String entryContent = """
                #!/usr/bin/env bash
                ulimit -s 100
                timeout --signal=SIGTERM 10 python3 main.py
                exit $?
                """;
        try (OutputStream os = new FileOutputStream(new File(folder + "/entrypoint.sh"))) {
            os.write(entryContent.getBytes(), 0, entryContent.length());
            log.info("entrypoint.sh file is created");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
    private void saveFile(MultipartFile file, String path) throws IOException {
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            Files.write(Paths.get(path), bytes);
            log.info("File is uploaded");
        }
    }

}
