package com.inspien;

import java.nio.charset.Charset;
import java.util.Base64;

public class DataUtil {
    private static final Charset EUC_KR = Charset.forName("EUC-KR");

    public static String decodeEucKr(String base64) {
        byte[] bytes = Base64.getDecoder().decode(base64);
        return new String(bytes, EUC_KR);
    }
}
