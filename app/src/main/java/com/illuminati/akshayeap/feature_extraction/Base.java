package com.illuminati.akshayeap.feature_extraction;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;


public class Base {
	
	int samplerate;
	// samplerate: the samplerate of the signal we are working with.
	double winlen=0.025;
	// the length of the analysis window in seconds. Default is 0.025s (25 milliseconds)
	double winstep=0.01;
	// the step between successive windows in seconds. Default is 0.01s (10 milliseconds)
	double numcep=13;
	// the number of cepstrum to return, default 13 
	double nfilt=26;
	// the number of filters in the filterbank, default 26.
	double nfft=512;
	// the FFT size. Default is 512.
	double lowfreq=0;
	// lowest band edge of mel filters. In Hz, default is 0.
	double highfreq;
	//  highest band edge of mel filters. In Hz, default is samplerate/2
	double preemph=0.97;
	// apply preemphasis filter with preemph as coefficient. 0 is no filter. Default is 0.97.
	double ceplifter=22;
	// apply a lifter to final cepstral coefficients. 0 is no lifter. Default is 22.
	double[] energy;
	// value is the energy in each frame (total energy, unwindowed)
	SignalProcessing processing=new SignalProcessing();
	
	public double[] extractMFCC(File signalfile) throws IOException {
		/*AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(signalfile);
		AudioFormat format = audioInputStream.getFormat();*/
		/*samplerate=(int)format.getSampleRate();
		highfreq=samplerate/2;*/
		double[] signal=convertsignal(signalfile);
		double[][] feat=fbank(signal);
		feat=logIt(feat);
		double[][] feat1=dct(feat);
		// Take only 13 columns
		feat1=lifter(feat1);
		for (int i = 0; i < feat1.length; i++) {
			feat1[i][0]=Math.log(energy[i]);
		}
		double[] result=new double[13];
		for (int i = 0; i < 13; i++) {
			double sum=0;
			for (int j = 0; j < feat1.length; j++) {
				sum+=feat1[j][i];
				
			}
			result[i]=sum/feat1.length;
		}
		return result;
	}
	
	private double[][] fbank(double[] signal) {
		// Compute Mel-filterbank energy features from an audio signal.
		// Returns a numpy array of size (NUMFRAMES by nfilt) containing features. 
		//Each row holds 1 feature vector.
		signal=processing.preemphasis(signal, 0.97);
		double[][] frames=processing.framesig(signal, winlen*samplerate, winstep*samplerate);
		double[][] pspec=processing.powsec(frames, nfft);
		energy=sumOf(pspec);
		double[][] fb=get_filterbanks();
		double[][] fbT=new double[fb[0].length][fb.length];
		for (int i = 0; i < fbT.length; i++) {
			for (int j = 0; j < fbT[0].length; j++) {
				fbT[i][j]=fb[j][i];
			}
		}
		double[][] feat=new double[pspec.length][fbT[0].length];
		feat=matrixMult(pspec,fbT);
		for (int i = 0; i < feat.length; i++) {
			for (int j = 0; j < feat[0].length; j++) {
				if(feat[i][j]==0) feat[i][j]=Math.ulp(1.0);
			}
		}
		return feat;
	}
	
	public double[][] logIt(double[][] signal) {
		
		for (int i = 0; i < signal.length; i++) {
			for (int j = 0; j < signal[0].length; j++) {
				signal[i][j]=Math.log(signal[i][j]);
			}
		}
		return signal;
		
	}
	
	public double[][] dct(double[][] signal) {
		
		int N=26;
		double[][] DCT=new double[signal.length][13];
		for (int i = 0; i < signal.length; i++) {
			for (int j = 0; j < 13; j++) {
				double sum=0;
				double f=0;
				for (int k = 0; k < N; k++) {
					sum+=signal[i][k]*Math.cos(Math.PI*j*(2*k+1)/(2*N));
				}
				if(j==0) f=Math.sqrt(1/(4.0*N));
				else f=Math.sqrt(1/(2.0*N));
				DCT[i][j]=sum*2*f;
			}
		}
		return DCT;
	}
	
	public double[][] get_filterbanks() {
		double nfilt=26;
		double lowmel=hz2mel(lowfreq);
		double highmel=hz2mel(highfreq);
		Linspace linspace=new Linspace(lowmel, highmel, nfilt+2);
		double[] melpoints=new double[(int)(nfilt+2)];
		double[] bin=new double[melpoints.length];
		for (int i = 0; i<melpoints.length; i++) {
			melpoints[i]=linspace.getNextdouble();
			bin[i]=Math.floor((nfft+1)*mel2hz(melpoints[i])/samplerate);
		}
		double[][] fbank=new double[(int)nfilt][(int)(Math.floor(nfft/2))+1];
		for (int j = 0; j < nfilt; j++) {
			for (int i = (int)bin[j]; i < bin[j+1]; i++) {
				fbank[j][i]=(i - bin[j]) / (bin[j+1]-bin[j]);
			}
			for (int i = (int)bin[j+1]; i < bin[j+2]; i++) {
				fbank[j][i]=(bin[j+2]-i) / (bin[j+2]-bin[j+1]);
			}
		}
		return fbank;
	} 
	
	
	public double[] convertsignal(File signalfile) throws  IOException {
		/*AudioInputStream audioInputStream=AudioSystem.getAudioInputStream(signalfile);
		ByteArrayOutputStream out=new ByteArrayOutputStream();

		int read;
		byte[] buff = new byte[2048];
		while ((read=audioInputStream.read(buff, 0, 2048)) > 0)
		{
		    out.write(buff, 0, read);
		}
		out.flush();
		byte[] audioBytes = out.toByteArray();
		
		short[] audios = new short[audioBytes.length / 2]; // will drop last byte if odd number
	    ByteBuffer bb = ByteBuffer.wrap(audioBytes);
	    bb.order(ByteOrder.LITTLE_ENDIAN);
	    for (int i = 0; i < audios.length; i++) {
	        audios[i] = bb.getShort();
	    }
	    
	    double[] signal=new double[audios.length];
	    for (int i = 0; i < audios.length; i++) {
			signal[i]=audios[i];
		}
	    return signal;*/
		return null;
	}
	
	public double[][] lifter(double[][] cepstra) {
		double[][] out=new double[cepstra.length][cepstra[0].length];
		for (int i = 0; i < out.length; i++) {
			for (int j = 0; j < out[0].length; j++) {
				out[i][j]=cepstra[i][j]*(1+(11*Math.sin(Math.PI*j/22)));
			}
		}
		return out;
	}
	
	public double[] sumOf(double[][] array) {
		double[] energy=new double[array.length];
		for (int i = 0; i < array.length; i++) {
			energy[i]=0;
			for (int j = 0; j < array[0].length; j++) {
				energy[i]+=array[i][j];
			}
			if(energy[i]==0) energy[i]=Math.ulp(1.0);
		}
		return energy;
	}
	
	public double[][] matrixMult(double[][] a, double[][] b) {
		double[][] output=new double[a.length][b[0].length];
		for (int i = 0; i < output.length; i++) {
			for (int j = 0; j < output[0].length; j++) {
				output[i][j]=0;
				for (int j2 = 0; j2 < b.length; j2++) {
					output[i][j]+=a[i][j2]*b[j2][j];
				}
			}
		}
		return output;
	}
	
	public double hz2mel(double hz) {
		 return 2595 * Math.log10(1+hz/700.);
	}
	
	public double mel2hz(double mel) {
		 return 700*(Math.pow(10,(mel/2595.0))-1);
	}

}
