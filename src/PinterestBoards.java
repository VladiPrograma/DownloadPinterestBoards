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
                String name = getImgName(v);
                String extension = getImgExtension(v);
                System.out.println(name+extension);
                byte[] img = getImgFromLink(v);
                if (img!=null){
                    try {
                        FileOutputStream fos = new FileOutputStream(savePath+"\\"+name+extension);
                        fos.write(img);
                        fos.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
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

    private static String getImgExtension(String link) {
        StringBuilder sb = new StringBuilder();
        int cont =0;
        for (int i = link.length()-1; i >=0 ; i--) {
            char c = link.charAt(i);
            if (c=='.'){ return  sb.reverse().toString();}
            sb.append(c);

        }
        return null;
    }

    private static String getImgName(String link) {
        StringBuilder sb = new StringBuilder();
        int cont =0;
        for (int i = link.length()-1; i >=0 ; i--) {
            char c = link.charAt(i);
            if (c=='/'){ return  sb.reverse().toString();}
            if (c=='.'){cont++;}
            if (cont==1){ sb.append(c);}

        }
        return null;
    }

    public static String webToString(URL url) {
        try (InputStream input = url.openStream()) {
            return new String(input.readAllBytes(), java.nio.charset.StandardCharsets.UTF_8);
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
    public static byte[] getImgFromLink(String link){
        try {
            URL url = new URL(link);
            InputStream in = new BufferedInputStream(url.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1 != (n = in.read(buf))) {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            return  out.toByteArray();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
