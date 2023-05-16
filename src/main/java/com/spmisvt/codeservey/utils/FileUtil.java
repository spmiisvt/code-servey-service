package com.spmisvt.codeservey.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    private static List<String> files = new ArrayList<>();
    public static void saveFile(MultipartFile file, String path) throws IOException {
        if (!file.isEmpty()) {
            byte[] bytes = file.getBytes();
            files.add(path);
            Files.write(Paths.get(path), bytes);
        }
    }
    public static void deleteFiles() throws IOException {
        for (String file : files) {
            Files.deleteIfExists(Paths.get(file));
        }
    }
}
