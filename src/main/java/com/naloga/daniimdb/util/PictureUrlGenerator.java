package com.naloga.daniimdb.util;

public class PictureUrlGenerator {
    public static String generatePictureUrl(Long imdbId) {
        String homeUrl = "/pictures/";
        String picName = imdbId + ".jpg";

        return homeUrl + picName;
    }
}
