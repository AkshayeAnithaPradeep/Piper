package com.illuminati.akshayeap.feature_extraction;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.illuminati.akshayeap.feature_extraction.Callback;

public class CSVwriter{

	private Callback callback1;
	private boolean value;
	
	public CSVwriter(boolean val, Callback callback) {
		callback1=callback;
		value=val;
	}
	
	public void writeToCSV(String filename, String genre, double[] array, File file) throws IOException {
		/*File file=new File("output.csv");*/
		FileWriter fileWriter=new FileWriter(file,true);
		for (int i = 0; i < array.length; i++) {
			fileWriter.append("MFCC"+(i+1));
			fileWriter.append(',');
		}
		fileWriter.append("Genre");
		fileWriter.append('\n');
		for (int i = 0; i < array.length; i++) {
			fileWriter.append(Double.toString(array[i]));
			fileWriter.append(',');
		}
		fileWriter.append(genre);
		fileWriter.append('\n');
		fileWriter.close();
		callback1.oncallback(true);
		System.out.println("Finished writing "+filename+" to "+file.getName());
	}

}
