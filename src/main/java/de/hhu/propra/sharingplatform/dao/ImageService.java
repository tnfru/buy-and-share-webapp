package de.hhu.propra.sharingplatform.dao;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.net.URL;

@Component
public class ImageService {

    private final String fileUploadsDir = "/uploads";

    public void store(MultipartFile file, String filename) {
        URL filrDir = ImageService.class
            .getClassLoader()
            .getResource(fileUploadsDir);
        System.out.println(file);
        System.out.println(filrDir);
        System.out.println(file.getContentType());
    }

    public Resource load(String filename) {
        return null;
    }

}
