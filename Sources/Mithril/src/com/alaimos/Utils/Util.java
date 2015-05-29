package com.alaimos.Utils;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class Util {

    public static void downloadFile(String source, String dest) throws Exception {
        URL sourceUrl = new URL(source);
        URLConnection yc = sourceUrl.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        BufferedWriter wr = new BufferedWriter(new FileWriter(dest, false));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            wr.write(inputLine);
            wr.newLine();
        }
        wr.close();
        in.close();
    }

    public static File downloadToTemp(String source) throws Exception {
        URL sourceUrl = new URL(source);
        URLConnection yc = sourceUrl.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
        File out = File.createTempFile("ifpath", ".out");
        out.deleteOnExit();
        BufferedWriter wr = new BufferedWriter(new FileWriter(out, false));
        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            wr.write(inputLine);
            wr.newLine();
        }
        wr.close();
        in.close();
        return out;
    }


}
