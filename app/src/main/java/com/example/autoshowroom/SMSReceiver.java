package com.example.autoshowroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class SMSReceiver extends BroadcastReceiver {
    public static final String SMS_TOKENIZE = "SMS_TOKENIZE";
    public static final String SMS_MSG = "SMS_MSG";

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (SmsMessage message : messages) {
            String msg = message.getDisplayMessageBody();

            // show message content in toast
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

            // broadcast message to be tokenized
            Intent tokenMsgIntent = new Intent();
            tokenMsgIntent.setAction(SMS_TOKENIZE);
            tokenMsgIntent.putExtra(SMS_MSG, msg);
            context.sendBroadcast(tokenMsgIntent);
        }
    }
}
