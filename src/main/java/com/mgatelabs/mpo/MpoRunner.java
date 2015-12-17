package com.mgatelabs.mpo;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Michael Glen Fuller JR on 12/16/15.
 * SideBySideTools For M-Gate Labs
 * Copyright 2015
 */
public class MpoRunner {

    public  static  void  main (String [] args) {

        System.out.println("MPO2SBS 0.1 - By @mgatelabs");

        File path = new File("."), temp1, temp2;

        File [] files = path.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".mpo");
            }
        });

        System.out.println("Found " + files.length + " *.mpo files.");

        try {

            temp1 = File.createTempFile("sbs", "left");
            temp2 = File.createTempFile("sbs", "right");
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }

        for (File mpo: files) {
            System.out.println("Processing: " + mpo.getName());
            List<MpoSlicer.MpoSlicerItem> slices = MpoSlicer.slice(mpo);
            if (slices.size() == 2) {
                String namePrefix = mpo.getName();
                namePrefix = namePrefix.substring(0, namePrefix.lastIndexOf("."));
                try {
                    //File sbsFile = new File(namePrefix + ".sbs");
                    //if (!sbsFile.exists()) {
                    //    sbsFile.createNewFile();
                    //}
                    MpoSlicer.MpoSlicerItem item = slices.get(0);
                    Slicer.sliceFile(mpo, temp1, item.getOffset(), item.getLength());

                    item = slices.get(1);
                    Slicer.sliceFile(mpo, temp2, item.getOffset(), item.getLength());

                    // load source images
                    BufferedImage image = ImageIO.read(temp1);
                    BufferedImage overlay = ImageIO.read(temp2);

                    // create the new image, canvas size is the max. of both image sizes
                    int w = Math.max(image.getWidth(), overlay.getWidth());
                    int h = Math.max(image.getHeight(), overlay.getHeight());
                    BufferedImage combined = new BufferedImage(w * 2, h, BufferedImage.TYPE_INT_RGB);



                    // paint both images, preserving the alpha channels
                    Graphics g = combined.createGraphics();
                    g.drawImage(image, 0, 0, null);
                    g.drawImage(overlay, image.getWidth(), 0, null);
                    g.dispose();

                    Iterator iter = ImageIO.getImageWritersByFormatName("jpeg");
                    ImageWriter writer = (ImageWriter)iter.next();
                    ImageWriteParam iwp = writer.getDefaultWriteParam();

                    iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    iwp.setCompressionQuality(0.95f);

                    File file = new File("./"+namePrefix + ".jpg");
                    FileImageOutputStream output = new FileImageOutputStream(file);
                    writer.setOutput(output);
                    IIOImage image2 = new IIOImage(combined, null, null);
                    writer.write(null, image2, iwp);
                    writer.dispose();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
