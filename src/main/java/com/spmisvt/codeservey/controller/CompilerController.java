package com.spmisvt.codeservey.controller;

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
    public List<Result> pythonCompiler(@RequestPart(value = "sourceCode", required = false) MultipartFile sourceCode) {
        String folder = "DockerFiles";
        createEntrypointForPython(folder);
        try {
            saveFile(sourceCode, folder + "/main.py");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }

        String dockerImageName = "compiler" + new Date().getTime();
        log.info("Build docker image");

        String[] buildCommand = new String[] {"docker", "image", "build", folder, "-t", dockerImageName};

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(buildCommand);
            Process processBuild = processBuilder.start();
            if (processBuild.waitFor() == 0) {
                log.info("docker image created");
            } else {
                log.error("Error creating docker image");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<Result> response= new ArrayList<>();

        log.info("running docker");
        try {
            String[] runCommand = new String[] {"docker", "run", "--rm", dockerImageName};
            ProcessBuilder processRunner = new ProcessBuilder(runCommand);
            Process runner = processRunner.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(runner.getInputStream()));
            String result = null;
            while((result = reader.readLine()) != null) {
                response.add(new Result(result));
            }

            if (runner.waitFor() == 0) {
                log.info("docker container running");
            } else {
                log.error("Error running docker container");
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        log.info("delete docker image");
        try {
            String[] runCommand = new String[] {"docker", "rmi", "-f", dockerImageName};
            ProcessBuilder processDeleter = new ProcessBuilder(runCommand);
            Process runner = processDeleter.start();
            if (runner.waitFor() == 0) {
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
