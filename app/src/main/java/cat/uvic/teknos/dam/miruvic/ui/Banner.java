package cat.uvic.teknos.dam.miruvic.ui;

import cat.uvic.teknos.dam.miruvic.ui.exceptions.BannerException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Banner {
    public static void show() {
        try {
            var path = Paths.get(Banner.class.getResource("/banner.txt").toURI());
            var banner = Files.readString(path);

            System.out.println(banner);
        } catch (URISyntaxException | IOException e) {
            throw new BannerException(e);
        }
    }

}
