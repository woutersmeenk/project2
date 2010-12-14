/*  Copyright 2010 Ben Ruijl, Wouter Smeenk

This file is part of project2

project2 is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 3, or (at your option)
any later version.

project2 is distributed in the hope that it will be useful, but
WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
General Public License for more details.

You should have received a copy of the GNU General Public License
along with project2; see the file LICENSE.  If not, write to the
Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
02111-1307 USA.

 */
package project2.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Writes JDK log messages to commons logging. Copied from:
 * http://wiki.apache.org/myfaces/Trinidad_and_Common_Logging
 */
public final class JavaLoggingToCommonLoggingRedirector {
    private static final Log LOG = LogFactory
            .getLog(JavaLoggingToCommonLoggingRedirector.class);
    private static final JDKLogHandler ACTIVE_HANDLER = new JDKLogHandler();

    /** private constructor */
    private JavaLoggingToCommonLoggingRedirector() {
    }

    /**
     * Activates this feature.
     */
    public static void activate() {
        try {
            final Logger rootLogger = LogManager.getLogManager().getLogger("");
            // remove old handlers
            for (final Handler handler : rootLogger.getHandlers()) {
                rootLogger.removeHandler(handler);
            }
            // add our own

            ACTIVE_HANDLER.setLevel(Level.ALL);
            rootLogger.addHandler(ACTIVE_HANDLER);
            rootLogger.setLevel(Level.ALL);
            // done, let's check it right away!!!

            LOG.info("activated: sending JDK log messages to Commons Logging");
        } catch (final Exception exc) {
            LOG.error("activation failed", exc);
        }
    }

    /**
     * Deactivates this feature.
     */
    public static void deactivate() {
        final Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.removeHandler(ACTIVE_HANDLER);

        LOG.info("dactivated");
    }

    /**
     * Redirects all the log call to commons logging
     */
    private static class JDKLogHandler extends Handler {
        /**
         * The cached logs
         */
        private final Map<String, Log> cachedLogs = new ConcurrentHashMap<String, Log>();

        /**
         * Get log from the cache
         * 
         * @param logName
         *            the log name
         * @return The log that belongs to the log name
         */
        private Log getLog(final String logName) {
            Log log = cachedLogs.get(logName);
            if (log == null) {
                log = LogFactory.getLog(logName);
                cachedLogs.put(logName, log);
            }
            return log;
        }

        @Override
        public void publish(final LogRecord record) {
            final Log log = getLog(record.getLoggerName());
            final String message = record.getMessage();
            final Throwable exception = record.getThrown();
            final Level level = record.getLevel();
            if (level == Level.SEVERE) {
                log.error(message, exception);
            } else if (level == Level.WARNING) {
                log.warn(message, exception);
            } else if (level == Level.INFO) {
                log.info(message, exception);
            } else if (level == Level.CONFIG) {
                log.debug(message, exception);
            } else {
                log.trace(message, exception);
            }
        }

        @Override
        public void flush() {
            // nothing to do
        }

        @Override
        public void close() {
            // nothing to do
        }
    }
}
