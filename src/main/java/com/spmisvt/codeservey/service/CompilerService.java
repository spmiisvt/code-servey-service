package com.spmisvt.codeservey.service;

import com.spmisvt.codeservey.utils.DockerEntrypoint;
import com.spmisvt.codeservey.utils.DockerWorker;
import com.spmisvt.codeservey.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class CompilerService {

    public List<String> compile(MultipartFile sourceCodeFile, MultipartFile inputDataFile) {
        String folder = "DockerFiles";
        DockerEntrypoint.createPythonEntrypoint(folder);
        try {
            FileUtil.saveFile(sourceCodeFile, folder + "/main.py");
            FileUtil.saveFile(inputDataFile, folder + "/input.txt");
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
            FileUtil.deleteFiles();
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


}
