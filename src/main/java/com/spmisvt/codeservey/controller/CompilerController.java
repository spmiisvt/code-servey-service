package com.spmisvt.codeservey.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@RestController
@Slf4j
public class CompilerController {
    @PostMapping("python")
    public void pythonCompiler(@RequestPart(value = "sourceCode", required = false) MultipartFile sourceCode) {
        String entryContent = "#!/usr/bin/env bash\n" +
                "ulimit -s 100" +
                "timeout --signal=SIGTERM 10 python3 main.py\n" +
                "exit $?\n";
        try {
            OutputStream os = new FileOutputStream(new File("entry_temp/entrypoint.sh"));
            os.write(entryContent.getBytes(), 0, entryContent.length());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
