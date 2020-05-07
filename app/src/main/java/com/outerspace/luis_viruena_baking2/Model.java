package com.outerspace.luis_viruena_baking2;

// TODO: REMOVE THE CLASS COMPLETELY
public class Model {
    private static int i = 0;
    private static String[] videoUrlArray = new String[] {
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffd974_-intro-creampie/-intro-creampie.mp4",
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdc33_-intro-brownies/-intro-brownies.mp4",
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffddf0_-intro-yellow-cake/-intro-yellow-cake.mp4",
            "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdae8_-intro-cheesecake/-intro-cheesecake.mp4",
    };

    public static String getCurrentVideoUrl() {
        return videoUrlArray[i];
    }

    public static String getNextVideoUrl() {
        if(++i >= videoUrlArray.length) i = 0;
        return videoUrlArray[i];
    }
}
