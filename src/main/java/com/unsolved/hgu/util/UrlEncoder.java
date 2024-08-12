package com.unsolved.hgu.util;


import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class UrlEncoder {
    public static String encodeURL(String url) {
        return URLEncoder.encode(url, StandardCharsets.UTF_8);
    }
}
