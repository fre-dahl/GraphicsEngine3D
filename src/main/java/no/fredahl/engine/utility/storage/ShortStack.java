package no.fredahl.engine.utility.storage;

/**
 *
 * specific use-case stack.
 * no checks for out of bounds. initial cap is 16.
 * will shrink back to 16 if it previously has reached
 * a cap of 128 and is currently empty.
 *
 * @author Frederik Dahl
 * 29/08/2021
 */


public class ShortStack {

    private short[] s = new short[0x10];
    private short t = -1;

    public void push(short i) {
        s[++t] = i;
        if (t == s.length-1) {
            short[] a = s;
            s = new short[a.length<<1];
            System.arraycopy(a,0,s,0,a.length);
        }
    }
    public short pop() {
        short r = s[t--];
        if (t<0)
            if (s.length >= 0x80)
                s = new short[0x10];
        return r;
    }

    public int size() {return t+1;}

    public boolean isEmpty() {return t<0;}
}
