package no.nordicsemi.android.mesh.utility;

import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Khoa Nguyen on 8/20/18.
 * Copyright (c) 2018. All rights reserved.
 * Email: khoantt91@gmail.com
 */

@SuppressWarnings("unused")
public class DebugLog {

    private final static int LOGGER_ENTRY_MAX_LEN = 3000 /* (4 * 1024) */;

    private static String className;
    private static String methodName;
    private static int lineNumber;
    private static int count = 0;
    private static String logs = "";

    private static String COLOR_LOG_ERROR = "\uD83E\uDD2C \uD83E\uDD2C \uD83E\uDD2C \uD83E\uDD2C \uD83E\uDD2C \uD83E\uDD2C   " + LogColor.INSTANCE.getRED_BOLD();
    private static String COLOR_LOG_WARNING = "\uD83D\uDC23 \uD83D\uDC23 \uD83D\uDC23 \uD83D\uDC23 \uD83D\uDC23 \uD83D\uDC23   " + LogColor.INSTANCE.getYELLOW_BOLD();
    private static String COLOR_LOG_DEBUG = LogColor.INSTANCE.getCYAN_BRIGHT();

    private final static boolean appendLogFile = false;
    private final static boolean IS_DEBUG = true;

    private DebugLog() {
        /* Protect from instantiations */
    }

    private static synchronized void appenLog(String log) {
        if (count >= 400) {
            logs = "";
            count = 0;
        }
        count++;
        logs = log + logs;
        if (appendLogFile)
            appendLogFile(log);
    }

    public static void onLowMemory() {
        logs = "";
        count = 0;
    }

    private static String createLog(String log, String colorLogDebug) {
        return "\n" + colorLogDebug + "[" + methodName + ":" + lineNumber + "]" + log + "\n ";
    }

    private static void getMethodNames(StackTraceElement[] sElements) {
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void e(String message) {
        if (!IS_DEBUG)
            return;

        // Throwable instance must be created before any methods
        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(message, COLOR_LOG_ERROR);
        appenLog(log + "\n");

        // show full log
        int left, right = 0;
        int lenght = log.length();
        while (lenght != 0) {
            left = right;
            if (lenght >= LOGGER_ENTRY_MAX_LEN) {
                right += LOGGER_ENTRY_MAX_LEN;
                lenght -= LOGGER_ENTRY_MAX_LEN;
            } else {
                right = log.length();
                lenght = 0;
            }
            try {
                Log.e(className, log.substring(left, right));
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public static void i(String message) {
        if (!IS_DEBUG)
            return;

        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(message, COLOR_LOG_DEBUG);
        appenLog(log + "\n");

        // show full log
        int left, right = 0;
        int lenght = log.length();
        while (lenght != 0) {
            left = right;
            if (lenght >= LOGGER_ENTRY_MAX_LEN) {
                right += LOGGER_ENTRY_MAX_LEN;
                lenght -= LOGGER_ENTRY_MAX_LEN;
            } else {
                right = log.length();
                lenght = 0;
            }
            try {
                Log.i(className, log.substring(left, right));
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public static void d(String message) {
        if (!IS_DEBUG)
            return;

        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(message, COLOR_LOG_DEBUG);

        appenLog(log + "\n");

        // show full log
        int left, right = 0;
        int length = log.length();
        while (length != 0) {
            left = right;
            if (length >= LOGGER_ENTRY_MAX_LEN) {
                right += LOGGER_ENTRY_MAX_LEN;
                length -= LOGGER_ENTRY_MAX_LEN;
            } else {
                right = log.length();
                length = 0;
            }
            try {
                Log.d(className, log.substring(left, right));
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public static void v(String message) {
        if (!IS_DEBUG)
            return;

        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(message, COLOR_LOG_DEBUG);

        // show full log
        int left, right = 0;
        int length = log.length();
        while (length != 0) {
            left = right;
            if (length >= LOGGER_ENTRY_MAX_LEN) {
                right += LOGGER_ENTRY_MAX_LEN;
                length -= LOGGER_ENTRY_MAX_LEN;
            } else {
                right = log.length();
                length = 0;
            }
            try {
                Log.v(className, log.substring(left, right));
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public static void w(String message) {
        if (!IS_DEBUG)
            return;

        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(message, COLOR_LOG_WARNING);

        // show full log
        int left, right = 0;
        int length = log.length();
        while (length != 0) {
            left = right;
            if (length >= LOGGER_ENTRY_MAX_LEN) {
                right += LOGGER_ENTRY_MAX_LEN;
                length -= LOGGER_ENTRY_MAX_LEN;
            } else {
                right = log.length();
                length = 0;
            }
            try {
                Log.w(className, log.substring(left, right));
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public static void wtf(String message) {
        if (!IS_DEBUG)
            return;

        getMethodNames(new Throwable().getStackTrace());
        String log = createLog(message, COLOR_LOG_DEBUG);

        // show full log
        int left, right = 0;
        int length = log.length();
        while (length != 0) {
            left = right;
            if (length >= LOGGER_ENTRY_MAX_LEN) {
                right += LOGGER_ENTRY_MAX_LEN;
                length -= LOGGER_ENTRY_MAX_LEN;
            } else {
                right = log.length();
                length = 0;
            }
            try {
                Log.wtf(className, log.substring(left, right));
            } catch (StringIndexOutOfBoundsException e) {
                e.printStackTrace();
            }
        }
    }

    public static void debugJson(Object object) {
        debugJson("debugJson:", object);
    }

    @SuppressWarnings("SameParameterValue")
    private static void debugJson(String titleLog, Object object) {
        if (!IS_DEBUG)
            return;
        try {
            e(titleLog + new Gson().toJson(object));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static void appendLogFile(Object text) {
        File logFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Home.file");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(String.valueOf(text));
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
