package com.spmisvt.codeservey.controller;

import com.spmisvt.codeservey.service.CompilerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@Slf4j
public class CompilerController {
    private final CompilerService compilerService;
    public CompilerController(CompilerService compilerService) {
        this.compilerService = compilerService;
    }
    @PostMapping(value = "python")
    public List<String> pythonCompiler(
            @RequestPart(value = "sourceCodeFile") MultipartFile codeFile,
            @RequestPart(value = "inputDataFile", required = false) MultipartFile inputDataFile
    ) throws RuntimeException {
        return compilerService.compile(codeFile, inputDataFile);
    }
}
