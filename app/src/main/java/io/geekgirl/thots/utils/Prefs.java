package io.geekgirl.thots.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Field;

import io.geekgirl.thots.R;


public class Prefs {

    public static final String IS_CONNECTED = "isConnected";
    public static final String CURRENT_USER = "user";
    public static final String USER_UID = "user_uid";
    public static final String USER_EMAIL = "user_email";
    public static final String USER_USERNAME = "user_username";
    private static final String TAG = Prefs.class.getSimpleName();

    public static boolean hasPref(String key, Context ctx) {
        return ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).contains(key);
    }

    public static void removePref(String key, Context ctx) {
        DebugLog.d("removePref[" + key + "]  ");
        ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).edit().remove(key);
    }

    public static void setBooleanPref(String key, boolean value, Context ctx) {
        if (ctx == null) {
            return;
        }
        DebugLog.d("setPref[" + key + "]  " + value);
        SharedPreferences.Editor edit = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).edit();
        edit.putBoolean(key, value).commit();
    }

    public static boolean getBooleanPref(String key, Context ctx) {
        if (ctx == null) {
            return false;
        }
        boolean value = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).getBoolean(key, false);
        DebugLog.d("getPref[" + key + "]  " + value);
        return value;
    }


    public static void setPref(String key, String value, Context ctx) {
        if (ctx == null) {
            return;
        }
        DebugLog.d("setPref[" + key + "]  " + value);
        if (value != null) {
            SharedPreferences.Editor edit = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).edit();
            edit.putString(key, value).apply();
        }
    }

    public static String getPref(String key, Context ctx) {
        if (ctx == null) {
            return null;
        }
        String value = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).getString(key, null);
        DebugLog.d("getPref[" + key + "]  " + value);
        return value;
    }

    public static void setPrefObject(Object obj, Context ctx) {

        DebugLog.d("setPref[" + obj.getClass().getSimpleName() + "]");
        for (Field f : obj.getClass().getFields()) {
            try {

                if (f.getType() == String.class) {
                    if (f.get(obj) != null) {
                        String value = f.get(obj) + "";
                        String name = f.getName();
                        String className = obj.getClass().getSimpleName();
                        SharedPreferences.Editor edit = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).edit();
                        edit.putString(className + "_" + name, value).commit();
                        DebugLog.d("set [" + className + "_" + name + "]" + f.get(obj));
                    }
                } else if (f.getType() == double.class) {
                    if (f.get(obj) != null) {
                        String value = String.valueOf(f.get(obj));
                        String name = f.getName();
                        String className = obj.getClass().getSimpleName();
                        SharedPreferences.Editor edit = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).edit();
                        edit.putString(className + "_" + name, value).commit();
                        DebugLog.d("set [" + className + "_" + name + "]" + f.get(obj));
                    }
                } else if (f.getType() == int.class) {
                    if (f.get(obj) != null) {
                        String value = String.valueOf(f.get(obj));
                        String name = f.getName();
                        String className = obj.getClass().getSimpleName();
                        SharedPreferences.Editor edit = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).edit();
                        edit.putString(className + "_" + name, value).commit();
                        DebugLog.d("set [" + className + "_" + name + "]" + f.get(obj));
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public static void clear(Context ctx) {
        if (ctx == null) {
            return;
        }
        ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).edit().clear().commit();
    }


    public static Object getPrefObject(Class<?> cl, Context ctx) {

        try {
            String className = cl.getSimpleName();
            DebugLog.d("getPref[" + className + "]");
            Object obj = cl.newInstance();
            for (Field f : cl.getFields()) {
                String value = ctx.getSharedPreferences(ctx.getResources().getString(R.string.app_name), Context.MODE_PRIVATE).getString(className + "_" + f.getName(), null);
                if ((value != null) && (value.length() > 0) && (f.getType() == String.class)) {

                    f.set(obj, value.replace("null", ""));
                    DebugLog.d("get [" + className + "_" + f.getName() + "]" + value);
                } else if ((value != null) && (value.length() > 0) && (f.getType() == double.class)) {

                    f.set(obj, Double.valueOf(value.replace("null", "")));
                    DebugLog.d("get [" + className + "_" + f.getName() + "]" + Double.valueOf(value.replace("null", "")));
                } else if ((value != null) && (value.length() > 0) && (f.getType() == int.class)) {

                    f.set(obj, Integer.parseInt(value.replace("null", "")));
                    DebugLog.d("get [" + className + "_" + f.getName() + "]" + Integer.parseInt(value.replace("null", "")));
                }
            }

            return obj;
        } catch (Exception e) {
            DebugLog.d("Error[" + cl + "]:" + e.getLocalizedMessage() + " ctx:" + ctx);
        }

        try {
            return cl.newInstance();
        } catch (Exception e) {
        }

        return null;
    }

}
