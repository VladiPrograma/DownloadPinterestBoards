import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Una vez tirada la web desde https://designopendata.wordpress.com/project-type/book/
 * se deberan filtrar los enlaces por "PERMALINK"
 * una vez abierto ese LINK deberemos leer la pagina y esta vez filtrar por "STRONG"
 *
 */

public class Test {
    public static void main(String args[]) throws IOException {



    }



    public static void openLinkInBrowser(String link){

        try {
            URI theURI = new URI(link);
            java.awt.Desktop.getDesktop().browse(theURI);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getHref(int pos, String web){
        int cont =1;
        String res = "";
        for (int i = pos; i < web.length(); i++) {
            char c = web.charAt(i);
            res+=c;
            if (c=='>') cont--;
            if (c=='<') cont++;
            if (cont==0) return res;
        }

        return "";
    }
    public static  ArrayList<String> getFullHref(ArrayList<Integer> pos, String web){
        ArrayList<String> list = new ArrayList<>();
        pos.forEach(p ->{
            list.add(getHref(p, web));
        });
        return list;
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
        System.out.println(result);
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

    public static ArrayList<Integer> getElementPos(String web, String pattern){
        ArrayList<Integer> list = new ArrayList<>();
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
                list.add(i-cont+1);
                cont=0;
            }

        }
        return list;
    }

    public static ArrayList<String> getLinks(ArrayList<Integer> pos, String web){
        ArrayList<String> list = new ArrayList<>();
        pos.forEach(p ->{
            list.add(getLink(p, web));
        });
        return list;
    }
    public static ArrayList<String> getLinks(Set<String>links){
        ArrayList<String> list = new ArrayList<>();
        links.forEach(p ->{
            list.add(getLink(p));
        });
        return list;
    }

    public static String getLink(int pos, String web){
        int cont =0;
        String res = "";
        for (int i = pos; i < web.length(); i++) {
            char c = web.charAt(i);
            if (cont==1&&c!='"'){
                res+=c;
            }
            if (c=='"'){cont++;}
            if (cont==2){ return res;}
            if (c=='>') break;
        }

        return getLinkNoWrap(pos, web);
    }
    public static String getLink( String href){
        int cont =0;
        String res = "";
        for (int i = 0; i < href.length(); i++) {
            char c = href.charAt(i);
            if (cont==1&&c!='"'){
                res+=c;
            }
            if (c=='"'){cont++;}
            if (cont==2){ return res;}
            if (c=='>') break;
        }
        return "";
    }
    public static String getLinkNoWrap(int pos, String web){
        int cont =0;
        String res = "";
        for (int i = pos; i < web.length(); i++) {
            char c = web.charAt(i);
            if (cont==1&&c!='>'){
                res+=c;
            }
            if (c=='='||c=='>'){cont++;}
            if (cont==2){ return res;}
        }
        return "";
    }

    public static Set<String> filterLinks(ArrayList<String> links, String pattern){
        Set<String> filtered = new HashSet<>();
        links.forEach(v ->{
            if (v.contains(pattern)){filtered.add(v);}
        });
        return filtered;
    }
    public static Set<String> filterLinks(ArrayList<String> links, String pattern, String pattern2){
        Set<String> filtered = new HashSet<>();
        links.forEach(v ->{
            if (v.contains(pattern)&&v.contains(pattern2)){filtered.add(v);}
        });
        return filtered;
    }

    public static void downloadBooks(Path path, String access)  {
        try {
        URL url = new URL(access);
        try (InputStream in = url.openStream()) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            // handle exception
        }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }


    public static String getNameFromLink(String link, char start, char end){
        String res= "";
        boolean flag = false;
        for (int i = link.length()-2; i >=0; i--) {
            char c = link.charAt(i);
            if (c==end) return reverseString(res);
            if (flag){
                res+= c;
            }
            if (c==start) flag =true;
        }
        return "";
    }
    public static String reverseString(String b){
        char[]reverse = new char[b.length()];

        for (int i = 0; i < b.length(); i++) {
            reverse[reverse.length-1-i]=b.charAt(i);
        }
        return new String(reverse);
    }

    public static void downloadAllPDFromInfoLibros() throws IOException {
        URL url = new URL("https://infolibros.org/libros-de-mitologia-pdf/");
        String web = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
        ArrayList<Integer> posHref = getElementPos(web, "href");
        ArrayList<String> links = getLinks(posHref, web);
        Set<String> filteredLinks = filterLinks(links, "pdf", "mitologia");
        filteredLinks.forEach(link ->{

            String carpet = getNameFromLink(link, '-','/');
            carpet = carpet.replace("libros-de-", "");
            carpet=carpet.replace("-gratis","");
            try {
                URL booksUrl = new URL(link);
                String booksWeb = new Scanner(booksUrl.openStream(), "UTF-8").useDelimiter("\\A").next();
                System.out.println();
                System.out.println(carpet);
                System.out.println();
                ArrayList<Integer> booksHref = getElementPos(booksWeb, "href");
                ArrayList<String> booksLink = getFullHref(booksHref, booksWeb);
                Set<String> bookSet = filterLinks(booksLink, "Libros_Boton_Dos", "drive");
                booksLink = getLinks(bookSet);
                booksLink.forEach(book ->{
                    System.out.println(book);
                    openLinkInBrowser(book);
                });

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


        });
    }
    public static void downloadAllPDFromOpenDataDesign(String site, String location){
        try {
            URL url = new URL(site);
            String web = new Scanner(url.openStream(), "UTF-8").useDelimiter("\\A").next();
            System.out.println(web);
            ArrayList<Integer> permalinkPos = getElementPos(web, "href");
            ArrayList<String> links = getLinks(permalinkPos, web);
            Set<String> set =filterLinks(links, "portfolio");
            set.forEach(link -> {
                try {
                    URL book = new URL(link);
                    String bookWeb = new Scanner(book.openStream(), "UTF-8").useDelimiter("\\A").next();

                    ArrayList<Integer> hrefsPos = getElementPos(bookWeb, "href");
                    ArrayList<String> bookLinks = getLinks(hrefsPos, bookWeb);
                    Set<String> filteredLinks = filterLinks(bookLinks, ".pdf");
                    filteredLinks.forEach( b -> {
                        String name = getNameFromLink(b, '.', '/');
                        Path path = Paths.get(location+"\\"+name+".pdf");
                        downloadBooks(path, b);
                    });
                    System.out.println(filteredLinks);

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
