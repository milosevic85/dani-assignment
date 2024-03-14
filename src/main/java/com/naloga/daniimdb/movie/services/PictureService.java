package com.naloga.daniimdb.movie.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

public class PictureService {
    private static final String picDirPath = "src/main/resources/pictures/";

    public static void savePic(Long imdbId, MultipartFile picFile) throws IOException {

        // bug fix to create a dir if it doesn't exist
        File dir = new File(picDirPath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        String picRoute = picDirPath + imdbId + getFileRouteExtension(picFile.getOriginalFilename());

        // I save the picture file
        picFile.transferTo(new File(picRoute));
    }

    private static String getFileRouteExtension(String f) {
        return f.substring(f.lastIndexOf('.'));
    }
}
