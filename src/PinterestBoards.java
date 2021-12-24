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

    public static String webToString(URL url){
        //Instantiating the URL class
        try {
            //Retrieving the contents of the specified page
            Scanner sc = new Scanner(url.openStream());
            //Instantiating the StringBuffer class to hold the result
            StringBuffer sb = new StringBuffer();
            while(sc.hasNext()) {
                sb.append(sc.next());
                //System.out.println(sc.next());
            }
            //Retrieving the String from the String Buffer object
            String result = sb.toString();
            //Removing the HTML tags
            result = result.replaceAll("<[^>]*>", "");
            return result;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
    public static ArrayList<String> getElementPos(String web, String pattern){
        ArrayList<String> list = new ArrayList<>();
        char[] w = web.toLowerCase().toCharArray();
        char[] p = pattern.toCharArray();
        int cont=0;

        for (int i = 0; i < w.length; i++) {

            if (p[cont]==w[i]){
                cont++;
            }else {
                cont=0;
            }

            if (cont==p.length){
                int pos =i-cont+1;
                char c=web.charAt(pos);
                while (c != '"'){
                 pos--;
                 c=web.charAt(pos);
                }
                pos++;
                c = web.charAt(pos);
                StringBuilder sb = new StringBuilder();
                while (c!='"'){
                    sb.append(c);
                    pos++;
                    c = web.charAt(pos);
                }
                if (sb.toString().contains("originals")&&sb.toString().contains("pinimg")){
                    list.add(sb.toString());
                }
                cont=0;
            }

        }
        return list;
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
