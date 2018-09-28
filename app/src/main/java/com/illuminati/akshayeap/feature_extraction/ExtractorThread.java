package com.illuminati.akshayeap.feature_extraction;

import com.illuminati.akshayeap.piper.GenreFragment;

import java.io.File;

public class ExtractorThread extends Thread {

	static boolean value = false;
	String inputfile;
    private GenreFragment mGenreFragment;

	public ExtractorThread(String name, GenreFragment genreFragment) {

        inputfile = name;
        mGenreFragment = genreFragment;
	}

	public void run() {
		Base base = new Base();
		File outputfile = new File("mfcc.csv");

		File file = new File(inputfile);
		System.out.println("Calculating MFCC  for " + inputfile);
		double[] mfcc = null;
		try {
			mfcc = base.extractMFCC(file);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*System.out.println("Writing " + inputfile);
		CSVwriter csVwriter = new CSVwriter(value, new Callback() {

			@Override
			public void oncallback(boolean var) {
				value = var;

			}
		});
		try {
			csVwriter.writeToCSV(inputfile, "rock", mfcc, outputfile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		while (value != true)
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		value = false;
		System.out.println("Writing Finished");*/
        //mGenreFragment.setResult("done");
	}

}
