package com.chronix.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends Activity {
    static final String URL = "https://yoandarz.github.io/chronix/";
    WebView web;

    @Override
    public void onCreate(@Nullable Bundle b) {
        super.onCreate(b);

        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.rgb(17, 24, 39));
        getWindow().setNavigationBarColor(Color.rgb(17, 24, 39));

        FrameLayout frame = new FrameLayout(this);
        web = new WebView(this);
        frame.addView(web, new FrameLayout.LayoutParams(-1, -1));
        setContentView(frame);

        ViewCompat.setOnApplyWindowInsetsListener(frame, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
                            | WindowInsetsCompat.Type.displayCutout()
            );
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);

        web.setWebViewClient(new WebViewClient());
        web.setWebChromeClient(new WebChromeClient());
        web.addJavascriptInterface(new NativeBridge(), "ChronixNativeAndroid");

        web.loadUrl(
                getIntent().getBooleanExtra("openAlert", false)
                        ? URL + "?alert=1"
                        : URL
        );
    }

    @Override
    protected void onNewIntent(Intent i) {
        super.onNewIntent(i);
        setIntent(i);

        if (i.getBooleanExtra("openAlert", false) && web != null) {
            web.loadUrl(URL + "?alert=1");
        }
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) {
            web.goBack();
        } else {
            super.onBackPressed();
        }
    }

    class NativeBridge {
        @JavascriptInterface
        public String syncState(String json) {
            try {
                AlarmScheduler.saveAndSchedule(MainActivity.this, json);
                return "ok";
            } catch (Exception e) {
                return "error: " + e.getMessage();
            }
        }

        @JavascriptInterface
        public void requestAlarmPermissions() {
            runOnUiThread(() -> {
                if (
                        Build.VERSION.SDK_INT >= 33
                                && ActivityCompat.checkSelfPermission(
                                        MainActivity.this,
                                        Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.POST_NOTIFICATIONS},
                            20
                    );
                }

                if (Build.VERSION.SDK_INT >= 31) {
                    AlarmManager am =
                            (AlarmManager) getSystemService(ALARM_SERVICE);

                    if (!am.canScheduleExactAlarms()) {
                        try {
                            startActivity(
                                    new Intent(
                                            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
                                            Uri.parse("package:" + getPackageName())
                                    )
                            );
                        } catch (Exception ignored) {
                        }
                    }
                }
            });
        }

        @JavascriptInterface
        public String testAlarm60s() {
            try {
                AlarmScheduler.test60(MainActivity.this);
                return "ok";
            } catch (Exception e) {
                return "error";
            }
        }

        @JavascriptInterface
        public String snoozeAlarm(int minutes) {
            try {
                AlarmScheduler.snooze(MainActivity.this, minutes);
                return "ok";
            } catch (Exception e) {
                return "error";
            }
        }
    }
}
