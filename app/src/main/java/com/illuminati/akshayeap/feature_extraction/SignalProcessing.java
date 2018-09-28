package com.illuminati.akshayeap.feature_extraction;

import org.apache.commons.lang3.ArrayUtils;

public class SignalProcessing {
	
	public int round_half_up(double num) {
		int res;
		double frac;
		res=(int)(num);
		frac=Math.abs(num-res);
		if(num>0) {
			if(frac>=0.5)
				res+=1;
		}
		else {
			if(frac>=0.5)
				res-=1;
		}
		return res;
	}
	
	public double[] preemphasis(double[] signal, double coeff) {
		double[] signalnew=new double[signal.length];
		for (int i = 0; i < signalnew.length; i++) {
			signalnew[i]=signal[i];
		}
		for (int i = 1; i < signal.length; i++) {
			signalnew[i]-=coeff*signal[i-1];
		}
		return signalnew;
	}
	
	public double[][] framesig(double[] signal, double frame_len1, double frame_step ) {
		//Frame a signal into overlapping frame
		// Rreturns an array of frames. Size is NUMFRAMES by frame_len.
		int slen=signal.length;
		int numframes;
		int frame_len = round_half_up(frame_len1);
		frame_step = round_half_up(frame_step);
	    if(slen <= frame_len)
	        numframes = 1;
	    else
	        numframes = 1 + (int)(Math.ceil((1.0*slen - frame_len)/frame_step));
	    int padlen = (int)((numframes-1)*frame_step + frame_len);
	    double[] zeros=new double[padlen - slen];
	    for (int i = 0; i < zeros.length; i++) {
			zeros[i]=0;
		}
	    double[] padsignal=ArrayUtils.addAll(signal, zeros);
	    
	    double[][] indices1=new double[numframes][frame_len];
	    for (int i = 0; i < numframes; i++) {
	    	for (int j = 0; j < frame_len; j++) {
				indices1[i][j]=j;
			}
		}
	    double[][] indices2=new double[numframes][frame_len];
	    for (int i = 0; i < frame_len; i++) {
			for (int j = 0,k=0; j < numframes; j++) {
				indices2[j][i]=k;
				k=(int)(k+frame_step);
			}
		}
	    double[][] indices=new double[numframes][frame_len];
	    for (int i = 0; i < numframes; i++) {
			for (int j = 0; j < frame_len; j++) {
				indices[i][j]=indices1[i][j]+indices2[i][j];
			}
		}
	    int newindices[][]=new int[numframes][frame_len];
	    for (int i = 0; i < numframes; i++) {
			for (int j = 0; j < frame_len; j++) {
				newindices[i][j]=(int)indices[i][j];
			}
	    }
	    double[][] frames=new double[numframes][frame_len];
	    for (int i = 0; i < numframes; i++) {
			for (int j = 0; j < frame_len; j++) {
				frames[i][j]=padsignal[newindices[i][j]];
			}
		}
	    return frames;
	}
	
	public double[][] magspec(double[][] frames, double NFFT) {
		FFT.Complex[] input=new FFT.Complex[(int)NFFT];
		double[][] output=new double[frames.length][257];
		for (int i = 0; i < frames.length; i++) {
			for (int j = 0; j < input.length; j++) {
				if(j<frames[0].length) input[j]=new FFT.Complex(frames[i][j], 0);
				else input[j]=new FFT.Complex(0, 0);	
			}
			FFT.Complex[] fft=FFT.Complex.fft(input);
			for (int j = 0; j <= fft.length/2; j++) {
				output[i][j]=Math.sqrt(fft[j].im()*fft[j].im()+fft[j].re()*fft[j].re());
			}
		}
		return output;
	}
	
	public double[][] powsec(double[][] frames, double NFFT) {
		
		double mult=1/NFFT;
		double[][] outframes=magspec(frames, NFFT);
		for (int i = 0; i < outframes.length; i++) {
			for (int j = 0; j < outframes[0].length; j++) {
				outframes[i][j]=outframes[i][j]*outframes[i][j]*mult;
			}
		}
		return outframes;
		
	}
}
