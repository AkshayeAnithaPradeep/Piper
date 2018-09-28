package com.illuminati.akshayeap.audioconverter;

import java.io.File;

public class ConverterThread extends Thread {

	static boolean value = false;
	String inputfile;

	public ConverterThread(String name) {
		inputfile = name;
	}

	public void run() {

		String outputfile;
		outputfile = inputfile + ".wav";
		Converter converter = new Converter(value, new Callback() {

			@Override
			public void oncallback(boolean var) {
				value = var;
			}
		});
		System.out.println("Converting " + inputfile);
		try {
			converter.ConvertFileToWAV(inputfile, outputfile);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (value != true)
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		value = false;
		System.out.println("Converted into " + outputfile);
	}

}
