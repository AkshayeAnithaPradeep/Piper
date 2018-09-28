package com.illuminati.akshayeap.feature_extraction;

public class Linspace {
    private double current;
    private final double end;
    private final double step;
    public Linspace(double start, double end, double totalCount) {
        this.current=start;
        this.end=end;
        this.step=(end - start) / (totalCount-1);
    }
    public boolean hasNext() {
        return current < (end + step/2); //MAY stop doubleing point error
    }
    public double getNextdouble() {
    	double ret=current;
        current+=step;
        return ret;
    }
}
