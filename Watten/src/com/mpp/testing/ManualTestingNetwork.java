package com.mpp.testing;

import com.mpp.network.Client;
import com.mpp.network.Server;

public class ManualTestingNetwork {

	public static void main(String[] args) {
		
		new Thread() {
			@Override 
			public void run() {
				Server.start();
			}
		}.start();

		new Thread() {
			@Override 
			public void run() {
				Client c = new Client();
				c.startManualInput();
			}
		}.start();

	}
}
