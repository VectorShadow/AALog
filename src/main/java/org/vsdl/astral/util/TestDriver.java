package org.vsdl.astral.util;

public class TestDriver {
    public static void main(String[] args) {
        AALogger logger = AALogger.getLogger();

        //logger.setLogLevel(LogLevel.INFO);
        logger.mapOriginID(1, "TestDriver");

        logger.log(1, LogLevel.INFO, "test");
        logger.log(-1, LogLevel.TRACE, "traceTest");
        logger.close();
    }
}
