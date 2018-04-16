package com.pylon.emarketpos.Utils;

public class Formatter {

    private byte[] mFormat;

    public Formatter(){
        mFormat = new byte[]{27,33,0};
    }

    public byte[] get(){
        return mFormat;
    }

    public Formatter bold(){
        mFormat[2] = ((byte) (0x8 | mFormat[2]));
        return this;
    }

    public Formatter small(){
        mFormat[2] = ((byte) (0x1 | mFormat[2]));
        return this;
    }

    public Formatter height(){
        mFormat[2] = ((byte) (0x10 | mFormat[2]));
        return this;
    }

    public static byte[] rightAlign(){
        return new byte[]{0x1B, 'a', 0x02};
    }

    public static byte[] leftAlign(){
        return new byte[]{0x1B, 'a', 0x00};
    }

    public static byte[] centerAlign(){
        return new byte[]{0x1B, 'a', 0x01};
    }
}