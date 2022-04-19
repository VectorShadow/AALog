package org.vsdl.astral.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class AALogger {

    private String logDir = "./log";
    private String logFileName = "eventlog";

    private LogLevel logLevel = LogLevel.TRACE;

    private boolean echoToConsole = false;
    private boolean useOriginIDs = false;

    private boolean useTimestamps = true;

    private HashMap<Integer, String> originIDMap = new HashMap<>();

    private BufferedWriter out;

    private AALogger(){}

    public static AALogger getLogger() {
        return new AALogger();
    }

    public void log(LogLevel logLevel, String message) {
        if (useOriginIDs) {
            throw new IllegalArgumentException("Origin IDs are required for this logger.");
        }
        log(null, logLevel, message);
    }

    public void log(Integer originID, LogLevel logLevel, String message) {
        if (out == null) initialize();
        if (this.logLevel.compareTo(logLevel) < 0) return;
        String formattedMessage = formatMessage(originID, message);
        if (echoToConsole) System.out.println(formattedMessage);
        try {
            out.write(formattedMessage + "\n");
        } catch (IOException e) {
            throw new IllegalStateException("IOException while trying to write log message: " + e.getMessage());
        }
    }

    public void setLogDir(String path) {
        this.logDir = path;
    }

    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public void setLogLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
    }

    public void setUseTimestamps(boolean val) {
        useTimestamps = val;
    }

    public void setEchoToConsole(boolean val) {
        echoToConsole = val;
    }

    public void setOriginIDMap(HashMap<Integer, String> map) {
        useOriginIDs = true;
        originIDMap = map;
    }

    public void mapOriginID(Integer originID, String name) {
        useOriginIDs = true;
        originIDMap.put(originID, name);
    }

    public void initialize() {
        createDirectoryIfNotExists(logDir);
        out = openWriter(getLogFilePath(logDir, logFileName, useTimestamps));
    }

    public void close() {
        if (out == null) return;
        try {
            out.close();
        } catch (IOException e) {
            throw new IllegalStateException("IOException while trying to close fileWriter: " + e.getMessage());
        }
    }

    private String formatMessage(Integer originID, String rawMessage) {
        StringBuilder sb = new StringBuilder();
        if (useTimestamps) {
            sb.append("[");
            sb.append(getTimestampNow());
            sb.append("] ");
        }
        if (useOriginIDs) {
            String mappedName = originID == null ? "NULL origin" : originIDMap.get(originID);
            sb.append("<").append(mappedName == null ? "unmapped origin" : mappedName).append("> ");
        }
        sb.append(rawMessage);
        return sb.toString();
    }

    private static Path getLogFilePath(String directory, String filename, boolean useTimestamps) {
        return Paths.get(directory + "/" + filename + (useTimestamps ? System.currentTimeMillis() : "") + ".txt");
    }

    private static void createDirectoryIfNotExists(String path) {
        Path logDirectoryPath = Paths.get(path);
        if (!Files.exists(logDirectoryPath)) {
            try {
                Files.createDirectory(logDirectoryPath);
            } catch (IOException e) {
                System.out.println("IOException during directory creation: " + e.getMessage());
                System.exit(-1);
            }
        }
    }

    private static BufferedWriter openWriter(Path path) {
        try {
            return Files.newBufferedWriter(
                    path,
                    Charset.defaultCharset(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            throw new IllegalStateException("IOException during writer initialization: " + e.getMessage());
        }
    }

    private static String getTimestampNow() {
        return new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
    }
}
