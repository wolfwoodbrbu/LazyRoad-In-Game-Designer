package com.VeraLapsa.LRIGD;

import java.util.logging.Logger;

/**
 *
 * @author VeraLapsa
 */
public class Log {

    private static Logger log;

    public Log(LazyRoadInGameDesigner plugin) {
        log = plugin.getLogger();
    }

    public void Info(String msg) {
        log.info(" " + msg);
    }

    public void Warn(String msg) {
        log.warning(" " + msg);
    }

    public void Severe(String msg) {
        log.severe(" " + msg);
    }

    public void Debug(String msg) {
        log.info("[debug] " + msg);
    }
}
