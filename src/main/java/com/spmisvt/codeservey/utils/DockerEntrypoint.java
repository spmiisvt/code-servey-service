package com.spmisvt.codeservey.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class DockerEntrypoint {

    public static void createPythonEntrypoint(String folder) {
        String entryContent = """
                #!/usr/bin/env bash
                ulimit -s 100
                timeout --signal=SIGTERM 10 python3 main.py < input.txt
                exit $?
                """;
        try (OutputStream os = new FileOutputStream(new File(folder + "/entrypoint.sh"))) {
            os.write(entryContent.getBytes(), 0, entryContent.length());
            log.info("entrypoint.sh file is created");
        } catch (IOException ex) {
            log.error(ex.getMessage());
        }
    }
}
