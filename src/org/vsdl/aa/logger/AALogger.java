package org.vsdl.aa.logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AALogger {

    private String logDir = "./log";
    private String logFileName = "eventlog";

    private LogLevel logLevel = LogLevel.TRACE;
    private boolean useTimestamps = true;
    private boolean echoToConsole = false;

    //todo - HashMap of originIDs to service names

    private BufferedWriter out;

    private AALogger(){}

    public static AALogger getLogger() {
        return new AALogger();
    }

    //todo - origin ID
    public void log(LogLevel logLevel, String message) {
        if (out == null) initialize();
        if (this.logLevel.compareTo(logLevel) < 0) return;
        //todo - properly format the message based on originID and timeStamp preference
        if (useTimestamps) message = "<" + getTimestampNow() + "> " + message;
        if (echoToConsole) System.out.println(message);
        try {
            out.write(message + "\n");
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
