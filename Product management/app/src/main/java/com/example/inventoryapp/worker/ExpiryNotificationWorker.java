package com.example.inventoryapp.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.inventoryapp.R;
import com.example.inventoryapp.ui.MainActivity;
import com.example.inventoryapp.data.AppDatabase;
import com.example.inventoryapp.data.Product;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class ExpiryNotificationWorker extends Worker {

    public static final String KEY_PRODUCT_NAME = "key_product_name";
    private static final String CHANNEL_ID = "expiry_notification_channel";

    public ExpiryNotificationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String productNameRaw = getInputData().getString(KEY_PRODUCT_NAME);
        if (productNameRaw != null) {
            showNotification(productNameRaw);
            return Result.success();
        }

        // Periodic check logic
        AppDatabase db = AppDatabase.getInstance(getApplicationContext());
        List<Product> products = db.productDao().getAllSync();
        long now = System.currentTimeMillis();

        if (products != null) {
            for (Product p : products) {
                if (p.getExpiryDate() != null && p.getNotifyDays() != null) {
                    long notifyTime = p.getExpiryDate() - TimeUnit.DAYS.toMillis(p.getNotifyDays());
                    // Check if current time is past notifyTime but before expiry
                    if (now >= notifyTime && now <= p.getExpiryDate()) {
                        showNotification(p.getName());
                    }
                }
            }
        }

        return Result.success();
    }

    private void showNotification(String productName) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Expiry Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Notifications for product expiry");
            notificationManager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                productName.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Product Expiry Warning")
                .setContentText(productName + " is expiring soon!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify(productName.hashCode(), builder.build());
    }
}