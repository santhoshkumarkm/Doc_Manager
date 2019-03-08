package com.listener;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.utilities.AddWordsTask;

public class AppContextListener implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

		// Your code here
		System.out.println("Listener has been shutdown");

	}

	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) {

		// Your code here
		System.out.println("Listener initialized.");

		Timer timer = new Timer();
		AddWordsTask addWordsTask = new AddWordsTask();
		timer.scheduleAtFixedRate(addWordsTask, 10000, 10000);
	}
}