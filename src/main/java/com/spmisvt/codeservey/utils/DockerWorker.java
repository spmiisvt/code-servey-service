package com.spmisvt.codeservey.utils;

import com.spmisvt.codeservey.controller.Result;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class DockerWorker {
    private String[] command;
    String dockerName;

    public DockerWorker(String dockerName) {
        this.dockerName = dockerName;
    }

    public boolean createImage(String folder) throws IOException, InterruptedException {
        this.command = new String[]{"docker", "image", "build", folder, "-t", this.dockerName};
        ProcessBuilder builder = new ProcessBuilder(this.command);
        Process process = builder.start();
        return process.waitFor() == 0;
    }
    public List<String> runImage() throws IOException, InterruptedException {
        this.command = new String[]{"docker", "run", "--rm", this.dockerName};
        ProcessBuilder builder = new ProcessBuilder(this.command);
        Process process = builder.start();

        List<String> response = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String result = null;
        while((result = reader.readLine()) != null) {
            response.add(result);
        }
        if (process.waitFor() != 0) {
            throw new IOException();
        }
        return response;
    }

    public boolean deleteImage() throws IOException, InterruptedException {
        this.command = new String[]{"docker", "rmi", "-f", this.dockerName};
        ProcessBuilder builder = new ProcessBuilder(this.command);
        Process process = builder.start();
        return process.waitFor() == 0;
    }
}
