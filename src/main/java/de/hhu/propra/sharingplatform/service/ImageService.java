package de.hhu.propra.sharingplatform.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.MimeType;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

@Component
public class ImageService {

    @Autowired
    private HttpServletRequest request;

    public void store(MultipartFile file, String filename) {
        String contenttype = file.getContentType();
        if (contenttype.equals("image/png") || contenttype.equals("image/jpeg")) {
            try {
                File out = ResourceUtils.getFile("src/main/resources/static/images/" + filename);
                out.createNewFile();
                FileOutputStream fos = new FileOutputStream(out);
                fos.write(file.getBytes());
                fos.close();
            } catch (IOException exception) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "File could not be uploaded");
            }
        } else {
            System.out.println(file.getContentType());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "Filetype not allowed");
        }
    }

}
