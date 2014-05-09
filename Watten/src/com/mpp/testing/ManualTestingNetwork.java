package com.mpp.testing;

import java.util.Random;

public class ManualTestingNetwork {

	public static void main(String[] args) {
		
//		new Thread() {
//			@Override 
//			public void run() {
//				Server.start();
//			}
//		}.start();

		int i = 0;
		while(i < 2) {
			new Thread() {
				@Override 
				public void run() {
					ClientTest c = new ClientTest();
					c.startManualInput();
				}
			}.start();
			
			Random rand = new Random();
			try {
				Thread.sleep(rand.nextInt(2000));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}

//		new Thread() {
//			@Override 
//			public void run() {
//				ClientForConsole c = new ClientForConsole();
//				c.startManualInput();
//			}
//		}.start();

	}
}
