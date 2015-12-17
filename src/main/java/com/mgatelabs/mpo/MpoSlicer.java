package com.mgatelabs.mpo;

import com.google.common.collect.Lists;
import com.sun.istack.internal.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.List;

/**
 * Created by Michael Glen Fuller JR on 12/16/15.
 * SideBySideTools For M-Gate Labs
 * Copyright 2015
 */
public class MpoSlicer {

    public static byte[] BYTE1 = { (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE1 };
    public static byte[] BYTE2 = { (byte)0xFF, (byte)0xD8, (byte)0xFF, (byte)0xE0 };

    @NotNull
    public static List<MpoSlicerItem> slice(@NotNull final File mpoFile) {
        List<MpoSlicerItem> slices = Lists.newArrayList();

        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;

        try {
            fileInputStream = new FileInputStream(mpoFile);
            bufferedInputStream = new BufferedInputStream(fileInputStream);

            int lastIndex = -1;
            int fileLength = (int) mpoFile.length();
            int index = 0;
            int offset = 256;
            byte [] temp = new byte[4];

            while (true) {
                if (index >= fileLength) {

                    break;
                }
                int len = bufferedInputStream.read(temp);
                if (len == 4) {
                    offset -= len;
                    if (matches(temp, BYTE1) || matches(temp, BYTE2)) {
                        if (lastIndex != -1) {
                            //System.out.println("Last: " + lastIndex + " - Index: " + index);
                            slices.add(new MpoSlicerItem(lastIndex, (index - lastIndex)));
                        }
                        lastIndex = index;
                    }
                    bufferedInputStream.skip(offset);
                    offset = 256;
                    index += offset;
                } else {
                    break;
                }
            }

            if (lastIndex != -1 && lastIndex < fileLength - 4) {
                //System.out.println("Last: " + lastIndex + " - Total: " + fileLength);
                slices.add(new MpoSlicerItem(lastIndex, (fileLength - lastIndex)));
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            return slices;
        } finally {
            Closer.close(bufferedInputStream);
        }

        return slices;
    }

    public static boolean matches (byte [] a, byte [] b) {
        if (a.length != b.length) {
            return false;
        }
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    public static class MpoSlicerItem {
        private final int offset;
        private final int length;

        public MpoSlicerItem(int offset, int length) {
            this.offset = offset;
            this.length = length;

            //System.out.println("Out: " + offset + " - Len: " + length);
        }

        public int getOffset() {
            return offset;
        }

        public int getLength() {
            return length;
        }
    }



}
