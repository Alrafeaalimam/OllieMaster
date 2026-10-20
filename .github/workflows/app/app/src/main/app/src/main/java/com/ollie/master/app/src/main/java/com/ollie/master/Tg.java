package com.ollie.master;
import java.net.*; import java.io.*; import java.util.*;
public class Tg {
    public static final String BOT = "6827943201:AAH_q75mHxBZWMfGg9P8ihOnL2DVzPnwRDk";
    public static final String CHAT = "679473843";
    public static String send(String text) {
        try {
            URL url = new URL("https://api.telegram.org/bot" + BOT + "/sendMessage");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST"); c.setDoOutput(true);
            String d = "chat_id=" + CHAT + "&text=" + URLEncoder.encode(text, "UTF-8");
            c.getOutputStream().write(d.getBytes("UTF-8"));
            InputStream is = c.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder(); String line;
            while ((line = br.readLine()) != null) sb.append(line);
            br.close(); c.disconnect();
            return sb.toString();
        } catch (Exception e) { return "{\"ok\":false}"; }
    }
    public static void sendPhoto(String path, String caption) {
        try {
            String boundary = "*****";
            URL url = new URL("https://api.telegram.org/bot" + BOT + "/sendPhoto");
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setRequestMethod("POST"); c.setDoOutput(true);
            c.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            OutputStream os = c.getOutputStream();
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"chat_id\"\r\n\r\n" + CHAT + "\r\n");
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"caption\"\r\n\r\n" + caption + "\r\n");
            dos.writeBytes("--" + boundary + "\r\n");
            dos.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\"img.jpg\"\r\n");
            dos.writeBytes("Content-Type: image/jpeg\r\n\r\n");
            FileInputStream fis = new FileInputStream(path);
            byte[] buffer = new byte[4096]; int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) dos.write(buffer, 0, bytesRead);
            fis.close();
            dos.writeBytes("\r\n--" + boundary + "--\r\n");
            dos.flush(); dos.close();
            c.getResponseCode(); c.disconnect();
        } catch (Exception e) {}
    }
}
