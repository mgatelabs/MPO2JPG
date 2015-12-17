package com.mgatelabs.mpo;

import java.io.*;

/**
 * Created by Michael Glen Fuller JR on 12/16/15.
 * SideBySideTools For M-Gate Labs
 * Copyright 2015
 */
public class Slicer {

    public static boolean sliceFile(File input, File output, int offset, int length) {

        FileInputStream fileInputStream = null;
        BufferedInputStream bufferedInputStream = null;

        FileOutputStream fileOutputStream = null;
        BufferedOutputStream bufferedOutputStream = null;

        try {

            fileInputStream = new FileInputStream(input);
            bufferedInputStream = new BufferedInputStream(fileInputStream);

            fileOutputStream = new FileOutputStream(output);
            bufferedOutputStream = new BufferedOutputStream(fileOutputStream);

            byte [] temp = new byte [1024];
            int read = 0;
            if (offset > 0) {
                fileInputStream.skip(offset);
            }

            while (read < length) {
                int maxSize = 1024;
                if (read + 1024 >= length) {
                    maxSize = length - read;
                }
                int len = bufferedInputStream.read(temp, 0, maxSize);
                if (len > 0) {
                    bufferedOutputStream.write(temp, 0, len);
                    read += len;
                } else {
                    break;
                }
            }
            bufferedOutputStream.flush();
            return true;
        } catch (Exception ex) {
          ex.printStackTrace();
        } finally {
            Closer.close(bufferedInputStream);
            Closer.close(bufferedOutputStream);
        }

        return false;
    }

}
