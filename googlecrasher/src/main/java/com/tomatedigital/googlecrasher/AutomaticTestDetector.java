package com.tomatedigital.googlecrasher;

import static android.content.Context.ACCOUNT_SERVICE;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.HttpsURLConnection;

public class AutomaticTestDetector {

    private static final ExecutorService threadPool = Executors.newSingleThreadExecutor();
    private static double result = -1.0;

    @SuppressLint("HardwareIds")
    public static boolean isEmulator(@NonNull final Context c) {


        Field[] tmp = Sensor.class.getDeclaredFields();
        Set<Field> fields = new HashSet<>();
        for (Field f : tmp)
            if (!Modifier.isStatic(f.getModifiers()) && f.getType() == String.class) {
                f.setAccessible(true);
                fields.add(f);
            }

        for (Sensor s : ((SensorManager) c.getSystemService(Context.SENSOR_SERVICE)).getSensorList(Sensor.TYPE_ALL)) {
            for (Field f : fields) {
                try {
                    String vendor = (String) f.get(s);
                    if (vendor != null) {
                        vendor = vendor.toLowerCase();
                        if (vendor.contains("bluestacks") || vendor.contains("tiantianvm") || vendor.contains("genymotion") || vendor.contains("goldfish") || vendor.contains("ttvm_hdragon") || vendor.contains("vbox"))
                            return true;
                    }
                } catch (IllegalAccessException ignore) {

                }
            }
        }


        final String manufacturer = Build.MANUFACTURER.toLowerCase();
        final String model = Build.MODEL.toLowerCase();
        final String hardware = Build.HARDWARE.toLowerCase();
        final String product = Build.PRODUCT.toLowerCase();
        final String brand = Build.BRAND.toLowerCase();
        final String fingerprint = Build.FINGERPRINT.toLowerCase();
        return
                model.contains("google_sdk")
                        || model.contains("droid4x")
                        || model.contains("emulator")
                        || model.contains("android sdk built for x86")
                        || model.equals("sdk")
                        || model.contains("tiantianvm")
                        || model.contains("andy")
                        || model.contains(" android sdk built for x86_64")
                        || manufacturer.contains("unknown")
                        || manufacturer.contains("genymotion")
                        || manufacturer.contains("andy")
                        || manufacturer.contains("mit")
                        || manufacturer.contains("tiantianvm")
                        || hardware.contains("goldfish")
                        || hardware.contains("vbox86")
                        || hardware.contains("nox")
                        || hardware.contains("ttvm_x86")
                        || product.contains("sdk")
                        || product.contains("andy")
                        || product.contains("ttvm_hdragon")
                        || product.contains("droid4x")
                        || product.contains("nox")
                        || product.contains("vbox86p")

                        || Build.BOARD.toLowerCase().contains("nox")
                        || Build.BOOTLOADER.toLowerCase().contains("nox")


                        || Build.SERIAL.toLowerCase().contains("nox")
                        || brand.contains("unknown")
                        || brand.contains("genymotion")
                        || brand.contains("andy")
                        || brand.contains("nox")
                        || brand.contains("mit")
                        || brand.contains("tiantianvm")
                        || Build.FINGERPRINT.startsWith("unknown")
                        || fingerprint.contains("generic")
                        || fingerprint.contains("andy")
                        || fingerprint.contains("ttvm_hdragon")
                        || fingerprint.contains("vbox86p");
    }


    @WorkerThread
    public static String getIpAddress() throws IOException {
        StringBuilder ip = new StringBuilder();

        HttpsURLConnection conn = (HttpsURLConnection) new URL("https://api.ipify.org").openConnection();
        conn.getResponseCode();
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        byte[] buffer = new byte[256];
        int lenght;
        while ((lenght = conn.getInputStream().read(buffer)) > 0)
            ip.append(new String(buffer, 0, lenght));

        conn.getInputStream().close();

        return ip.toString();
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void forceCrash() {
        "".charAt(new Random().nextInt());
    }

    public static void isGoogleRunning(@NonNull final Context c, @NonNull final GoogleRunningListener listener) {
        if (result == -1.0)
            threadPool.submit(() -> {
                double tmp = 0.0;

                // The package name of the app that has installed your app
                final String installer = c.getPackageManager().getInstallerPackageName(c.getPackageName());

                if (isEmulator(c) && installer == null || installer.trim().equalsIgnoreCase("null"))
                    tmp += 0.1;

                if ("unknown".equalsIgnoreCase(Build.MANUFACTURER) && "GCE x86 phone".equalsIgnoreCase(Build.MODEL))
                    tmp += 0.3;

                if ("true".equals(Settings.System.getString(c.getContentResolver(), "firebase.test.lab")))
                    tmp += 0.3;

                for (Account acc : ((AccountManager) c.getSystemService(ACCOUNT_SERVICE)).getAccounts()) {
                    if (acc.name.toLowerCase().endsWith("@cloudtestlabaccounts.com"))
                        tmp += 0.3;
                }

                if (((WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE)).getConnectionInfo().getSSID().startsWith("\"wl-ftl-mt"))
                    tmp += 0.3;

                try {
                    String ip = getIpAddress();
                    if (ip.startsWith("108.177.1.") || ip.startsWith("108.177.2.") || ip.startsWith("108.177.3.") || ip.startsWith("108.177.4.") || ip.startsWith("108.177.5.") || ip.startsWith("108.177.6.") || ip.startsWith("108.177.7.") || ip.startsWith("108.177.8.") || ip.startsWith("108.177.9.") || ip.startsWith("108.177.10."))
                        tmp += 0.3;
                } catch (IOException ignore) {
                }

                //google always connects to wifi
                ConnectivityManager connection = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
                int i = 0;
                for (NetworkInfo net : connection.getAllNetworkInfo()) {
                    if (net.getState() == NetworkInfo.State.CONNECTED && net.getType() != ConnectivityManager.TYPE_WIFI)
                        tmp = 0.0;
                }

                AutomaticTestDetector.result = tmp;
                listener.onResult(tmp);
            });
        else
            listener.onResult(result);
    }

    public interface GoogleRunningListener {

        /**
         * @param result value between 0 - 1 represent the probability of being google running
         */
        void onResult(final double result);

    }
}
