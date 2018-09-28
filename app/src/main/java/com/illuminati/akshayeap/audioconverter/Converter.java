package com.illuminati.akshayeap.audioconverter;

import android.os.Environment;

import com.acrcloud.rec.sdk.IACRCloudListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javazoom.jl.decoder.Decoder;
import javazoom.jl.decoder.Header;
import javazoom.jl.decoder.Obuffer;
import javazoom.jl.decoder.OutputChannels;

/**
 *
 * @author Akshaye AP
 */
public class Converter {

	private Callback callback1;
	private boolean value;

	public Converter(boolean val, Callback callback) {
		callback1 = callback;
		value = val;
	}

    /*public native String fpCalc(String[] args);
    // declare this in your java class (in this example, MainActivity)*/


    public void ConvertFileToWAV(String inputPath, String outputPath) {
        /*System.loadLibrary("fpcalc");

        String[] args = {"-md5", "some_filename.mp3"};
        *//*String result = fpCalc(args);*//*
*/
    }
	/*public void ConvertFileToWAV(String inputPath, String outputPath) throws Exception {

		AudioFileFormat inFileFormat;
		File inFile;
		File outFile;
		try {
			inFile = new File(inputPath);
			outFile = new File(outputPath);
		} catch (NullPointerException ex) {
			System.out.println("Error: one of the ConvertFileToWAV" + " parameters is null!");
			return;
		}
		try {
			// query file type

			inFileFormat = AudioSystem.getAudioFileFormat(inFile);
			if (inFileFormat.getType() != AudioFileFormat.Type.WAVE) {
				// inFile is not WAV, so let's try to convert it.
				AudioInputStream inFileAIS = AudioSystem.getAudioInputStream(inFile);
				// inFileAIS.reset(); // rewind
				System.out.println(AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, inFileAIS));
				if (AudioSystem.isFileTypeSupported(AudioFileFormat.Type.WAVE, inFileAIS)) {
					// inFileAIS can be converted to AIFF.
					// so write the AudioInputStream to the
					// output file.
					AudioSystem.write(inFileAIS, AudioFileFormat.Type.WAVE, outFile);
					System.out.println("Successfully made WAV file, " + outFile.getPath() + ", from "
							+ inFileFormat.getType() + " file, " + inFile.getPath() + ".");
					inFileAIS.close();
					callback1.oncallback(true);
					return; // All done now
				} else {
					System.out.println("Warning: WAV conversion of " + inFile.getPath()
							+ " is not currently supported by AudioSystem.");
				}
			} else {
				System.out.println("Input file " + inFile.getPath() + " is WAV." + " Conversion is unnecessary.");
				AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(inFile);
				javax.sound.sampled.AudioFormat format = audioInputStream.getFormat();
				long frames = audioInputStream.getFrameLength();
				double durationInSeconds = (frames+0.0) / format.getFrameRate();
                String newfile= Environment.getExternalStorageDirectory() +
                        File.separator + "Piper"+File.separator+"new.wav";
				if(durationInSeconds>60) {
					copyAudio(inputPath, newfile, (int)durationInSeconds/2, 30,0);
				}
				else
					copyAudio(inputPath, newfile, 0, (int)durationInSeconds,0);
				
				System.out.println("Edited into new.wav");
			}
		} catch (UnsupportedAudioFileException e) {
			System.out.println("Error: " + inFile.getPath() + " is not a supported audio file type! Converting again");
			AltConvertFileToWAV(inputPath, outputPath);
			callback1.oncallback(true);
			return;
		} catch (IOException e) {
			System.out.println("Error: failure attempting to read " + inFile.getPath() + "!");
			return;
		}
		callback1.oncallback(true);
		return;
	}

	public void AltConvertFileToWAV(String inputPath, String outputPath) throws Exception {
		File file = new File(inputPath);
		InputStream inputStream = new FileInputStream(file);
        Decoder.Params params=new Decoder.Params();
		params.setOutputChannels(OutputChannels.BOTH);
		javazoom.jl.converter.Converter converter = new javazoom.jl.converter.Converter();
		converter.convert(inputPath, outputPath, new javazoom.jl.converter.Converter.ProgressListener() {
            @Override
            public void converterUpdate(int i, int i1, int i2) {
                if(i== javazoom.jl.converter.Converter.ProgressListener.UPDATE_CONVERT_COMPLETE)
                {
                    System.out.println("Conversion Complete1");
                }

            }

            @Override
            public void parsedFrame(int i, Header header) {

            }

            @Override
            public void readFrame(int i, Header header) {

            }

            @Override
            public void decodedFrame(int i, Header header, Obuffer obuffer) {

            }

            @Override
            public boolean converterException(Throwable throwable) {
                return false;
            }
        });
		//System.out.println("Output channles:"+params.getOutputChannels().getChannelCount());
		for (int i = 0; i < 1000000; i++) {
			
		}
        System.out.println(outputPath);
        File outputfile=new File(outputPath);
		AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(outputfile);
		javax.sound.sampled.AudioFormat format = audioInputStream.getFormat();
		long frames = audioInputStream.getFrameLength();
		double durationInSeconds = (frames+0.0) / format.getFrameRate();
		if(durationInSeconds>60) {
			copyAudio(outputPath, "new.wav", (int)durationInSeconds/2, 30,1);	
		}
		else
			copyAudio(outputPath, "new.wav", 0, (int)durationInSeconds, 1);
		
		System.out.println("Edited into new.wav");
	}

	public static void copyAudio(String sourceFileName, String destinationFileName, int startSecond,
			int secondsToCopy, int source) {
		AudioInputStream inputStream = null;
		AudioInputStream shortenedStream = null;
		try {
			File file = new File(sourceFileName);
			AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(file);
			javax.sound.sampled.AudioFormat format = fileFormat.getFormat();
			System.out.println("Output channels:"+format.getChannels());
			inputStream = AudioSystem.getAudioInputStream(file);
			int bytesPerSecond = format.getFrameSize() * (int) format.getFrameRate();
			System.out.println("Frame rate:"+format.getFrameRate()+" Sample rate: "+format.getSampleRate());
			inputStream.skip(startSecond * bytesPerSecond);
			long framesOfAudioToCopy = secondsToCopy * (int) format.getFrameRate();
			
			File destinationFile = new File(destinationFileName);
			javax.sound.sampled.AudioFormat audioFormat=new javax.sound.sampled.AudioFormat(format.getEncoding(), format.getSampleRate()*2, format.getSampleSizeInBits(), 1, format.getFrameSize(), format.getFrameRate(), format.isBigEndian());
			if(source==1 && format.getChannels()!=1) {
				shortenedStream = new AudioInputStream(inputStream, audioFormat, framesOfAudioToCopy);
			}
			else shortenedStream = new AudioInputStream(inputStream, format, framesOfAudioToCopy);
			AudioSystem.write(shortenedStream, fileFormat.getType(), destinationFile);
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (inputStream != null)
				try {
					inputStream.close();
				} catch (Exception e) {
					System.out.println(e);
				}
			if (shortenedStream != null)
				try {
					shortenedStream.close();
				} catch (Exception e) {
					System.out.println(e);
				}
		}
	}*/
}