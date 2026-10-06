import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class PinterestBoards {

    /**
     * Especifica el formato
     * https://i.pinimg.com/736x/52/41/97/52419767f89cad080b8f95c100cf21dc.jpg
     * Lleva directamente a la foto original
     * https://i.pinimg.com/originals/52/41/97/52419767f89cad080b8f95c100cf21dc.jpg*/


    public static void main(String[] args) {
        String web = "https://www.pinterest.es/mdumitruvlad/lofi/";
        String path = "C:\\Users\\vlad1\\Desktop\\Lofi";
        downloadImages(web, path);

    }

    private static void downloadImages(String url, String savePath){
        try {
            URL carteles = new URL(url);
            String web = webToString(carteles);
            ArrayList<String> originals = getElementPos(web, "originals");
            originals.forEach(v ->{
                byte[] img = getImgFromLink(v);
                if (img != null) {
                    try {
                        saveImage(savePath, v, img);
                    } catch (IOException | IllegalArgumentException e) {
                        e.printStackTrace();
                    }
                }

            });


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String imageFileName(String link) {
        String path = java.net.URI.create(link).getPath();
        if (path == null) {
            throw new IllegalArgumentException("Image URL has no path");
        }
        String name = path.substring(path.lastIndexOf('/') + 1);
        if (name.isBlank() || name.equals(".") || name.equals("..") || name.contains("\\")) {
            throw new IllegalArgumentException("Image URL has no valid file name");
        }
        return name.replaceAll("[:*?\"<>|\\p{Cntrl}]", "_");
    }

    static void saveImage(String savePath, String link, byte[] image) throws IOException {
        java.nio.file.Path directory = java.nio.file.Path.of(savePath);
        String name = imageFileName(link);
        java.nio.file.Files.createDirectories(directory);
        java.nio.file.Files.write(directory.resolve(name), image);
    }

    public static String webToString(URL url) {
        try {
            return new String(readUrl(url), java.nio.charset.StandardCharsets.UTF_8);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }
    public static ArrayList<String> getElementPos(String web, String pattern) {
        java.util.Set<String> images = new java.util.LinkedHashSet<>();
        if (web == null || pattern == null || pattern.isEmpty()) {
            return new ArrayList<>();
        }
        String normalized = web.replace("\\/", "/");
        java.util.regex.Matcher values = java.util.regex.Pattern
                .compile("[\"']([^\"'<>]*)[\"']").matcher(normalized);
        while (values.find()) {
            String value = values.group(1).replace("&amp;", "&");
            if (!value.toLowerCase(java.util.Locale.ROOT)
                    .contains(pattern.toLowerCase(java.util.Locale.ROOT))) {
                continue;
            }
            try {
                java.net.URI uri = java.net.URI.create(value);
                String host = uri.getHost();
                if (("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                        && host != null && (host.equalsIgnoreCase("pinimg.com")
                        || host.toLowerCase(java.util.Locale.ROOT).endsWith(".pinimg.com"))
                        && uri.getPath() != null && uri.getPath().startsWith("/originals/")) {
                    images.add(value);
                }
            } catch (IllegalArgumentException ignored) {
                // Quoted HTML attributes and JSON keys are not necessarily URLs.
            }
        }
        return new ArrayList<>(images);
    }
    public static String getElement(String web, int pos){
        StringBuilder sb = new StringBuilder();
        while (web.charAt(pos)!='>'){
            sb.append(web.charAt(pos));
            pos++;
        }
        return sb.toString();
    }
    static byte[] readUrl(URL url) throws IOException {
        java.net.URLConnection connection = url.openConnection();
        connection.setConnectTimeout(15_000);
        connection.setReadTimeout(15_000);
        try (InputStream input = connection.getInputStream()) {
            return input.readAllBytes();
        }
    }

    public static byte[] getImgFromLink(String link) {
        try {
            return readUrl(new URL(link));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
