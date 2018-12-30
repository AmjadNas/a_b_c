package android.course.books_312316433_313601130.Services;

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
