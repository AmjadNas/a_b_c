package android.course.a_b_c.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootStartUpReciever extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent service = new Intent(context, NotifyierService.class);
        context.startService(service);

    }

}
