package org.vsdl.aa.logger;

public class TestDriver {
    public static void main(String[] args) {
        AALogger logger = AALogger.getLogger();

        logger.setLogLevel(LogLevel.INFO);

        logger.log(LogLevel.INFO, "test");
        logger.log(LogLevel.TRACE, "traceTest");
        logger.close();
    }
}
