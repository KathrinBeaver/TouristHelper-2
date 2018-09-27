package com.hse.touristhelper.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Created by Alex on 09.03.2016.
 */
public final class FileUtil {
    private static final String LOG_TAG = FileUtil.class.getSimpleName();

    public static void copyStream(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
        in.close();
        out.close();
    }

    public static void remove(File dir) {
        if (dir.isDirectory()) {
            for (File child : dir.listFiles()) {
                remove(child);
            }
        }
        dir.delete();
    }

    public static void deleteAll(File f) {
        if (f.isDirectory()) {
            for (File child : f.listFiles()) {
                deleteAll(child);
            }

            f.delete();
        }
    }

    public static byte[] toByteArray(InputStream is) throws IOException {
        final ByteArrayOutputStream data = new ByteArrayOutputStream();
        final byte[] buffer = new byte[16384];
        int nRead;
        while ((nRead = is.read(buffer, 0, buffer.length)) != -1) data.write(buffer, 0, nRead);
        return data.toByteArray();
    }

    public static void write(byte[] data, File file) throws IOException {
        final OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
        os.write(data);
        os.close();
    }

    /**
     * Size of the buffer to read/write data
     */
    private static final int BUFFER_SIZE = 4096;

    static public void unzip(String zipFile, String to, FilenameFilter filter) throws ZipException, IOException {
        int BUFFER = 2048;
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        //removing extention name
        String newPath = to;

        Enumeration<? extends ZipEntry> zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            if (entry.getName().contains("mode")) {
                long size = entry.getSize();
            }
            String currentEntry = entry.getName();
            File destFile = new File(newPath, currentEntry);
            File destinationParent = destFile.getParentFile();

            if (!filter.accept(destinationParent, destFile.getName())) {
                continue;
            }

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));

                if (entry.getName().contains("mode")) {
                    Log.e("mode", entry.getName());
                    Log.e("mode", entry.getSize() + "");
                    Log.e("mode", destFile.toString());
                }
                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);

                copyStream(is, dest);
            }
        }
    }

    public static File getFileFromAssets(Context context, String fileName) {

        try {
            AssetManager am = context.getAssets();
            InputStream inputStream = am.open(fileName);
            File f = new File(Environment.getExternalStorageDirectory() + File.separator + "chunker");
            OutputStream outputStream = new FileOutputStream(f);
            byte buffer[] = new byte[2048];
            int length = 0;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return f;
        } catch (IOException e) {
            Log.e("Exceptio", e.toString());
        }

        return null;
    }

    private FileUtil() {
    }
}
