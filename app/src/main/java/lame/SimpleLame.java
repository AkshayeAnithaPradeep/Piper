package lame;

/**
 * Created by Akshaye AP on 11-02-2017.
 */


public class SimpleLame {

    public native static void init(int inSamplerate, int outChannel,
                                   int outSamplerate, int outBitrate, int quality);

    public native static int encode(short[] buffer_l, short[] buffer_r,
                                    int samples, byte[] mp3buf);

    public native static int flush(byte[] mp3buf);

    public native static void close();
}
