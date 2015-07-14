package com.zyitong.AppStore.tools;

import java.io.IOException;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.helpers.LogLog;

public class AppLogConfigurator {
	private Level rootLevel = Level.DEBUG;
	private String filePattern = "%d - [%p::%c::%t] - %m%n";
	private String logCatPattern = "%m%n";
	private String fileName = "MobGuard.log";
	private int maxBackupSize = 5;
	private long maxFileSize = 512 * 1024;
	private boolean immediateFlush = true;
	private boolean useLogCatAppender = true;
	private boolean useFileAppender = true;
	private boolean resetConfiguration = true;
	private boolean internalDebugging = false;
	
	public AppLogConfigurator() {
	}

	public void configure() {
		final Logger root = Logger.getRootLogger();
		
		if(isResetConfiguration()) {
			LogManager.getLoggerRepository().resetConfiguration();
		}

		LogLog.setInternalDebugging(isInternalDebugging());
		
		if(isUseFileAppender()) {
			configureFileAppender();
		}
		
		if(isUseLogCatAppender()) {
			configureLogCatAppender();
		}
		
		root.setLevel(getRootLevel());
	}

	public void setLevel(final String loggerName, final Level level) {
		Logger.getLogger(loggerName).setLevel(level);
	}

	
	public Level getRootLevel() {
		return rootLevel;
	}

	public void setRootLevel(final Level level) {
		this.rootLevel = level;
	}

	public String getFilePattern() {
		return filePattern;
	}

	public void setFilePattern(final String filePattern) {
		this.filePattern = filePattern;
	}

	public String getLogCatPattern() {
		return logCatPattern;
	}

	public void setLogCatPattern(final String logCatPattern) {
		this.logCatPattern = logCatPattern;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(final String fileName) {
		this.fileName = fileName;
	}

	public int getMaxBackupSize() {
		return maxBackupSize;
	}

	public void setMaxBackupSize(final int maxBackupSize) {
		this.maxBackupSize = maxBackupSize;
	}

	public long getMaxFileSize() {
		return maxFileSize;
	}

	public void setMaxFileSize(final long maxFileSize) {
		this.maxFileSize = maxFileSize;
	}

	public boolean isImmediateFlush() {
		return immediateFlush;
	}

	public void setImmediateFlush(final boolean immediateFlush) {
		this.immediateFlush = immediateFlush;
	}

	public boolean isUseFileAppender() {
		return useFileAppender;
	}

	public void setUseFileAppender(final boolean useFileAppender) {
		this.useFileAppender = useFileAppender;
	}

	public boolean isUseLogCatAppender() {
		return useLogCatAppender;
	}

	public void setUseLogCatAppender(final boolean useLogCatAppender) {
		this.useLogCatAppender = useLogCatAppender;
	}

	public void setResetConfiguration(boolean resetConfiguration) {
		this.resetConfiguration = resetConfiguration;
	}

	public boolean isResetConfiguration() {
		return resetConfiguration;
	}

	public void setInternalDebugging(boolean internalDebugging) {
		this.internalDebugging = internalDebugging;
	}

	public boolean isInternalDebugging() {
		return internalDebugging;
	}
	

	private void configureFileAppender() {
		final Logger root = Logger.getRootLogger();
		final RollingFileAppender rollingFileAppender;
		final Layout fileLayout = new PatternLayout(getFilePattern());

		try {
			rollingFileAppender = new RollingFileAppender(fileLayout, getFileName());
		} catch (final IOException e) {
			throw new RuntimeException("Exception configuring log system", e);
		}

		rollingFileAppender.setMaxBackupIndex(getMaxBackupSize());
		rollingFileAppender.setMaximumFileSize(getMaxFileSize());
		rollingFileAppender.setImmediateFlush(isImmediateFlush());

		root.addAppender(rollingFileAppender);
	}
	
	private void configureLogCatAppender() {
		final Logger root = Logger.getRootLogger();
		final Layout logCatLayout = new PatternLayout(getLogCatPattern());
		final LogCatAppender logCatAppender = new LogCatAppender(logCatLayout);

		root.addAppender(logCatAppender);
	}
}
