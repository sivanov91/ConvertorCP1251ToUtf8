package ru.myproject;

import java.io.*;
import java.nio.charset.*;
import java.util.zip.GZIPOutputStream;

/**
 * Created by dellix on 31.03.15.
 */

public class ConvertCp1251ToUtf8 {
    private static final int BYTE_ORDER_MARK = 1024;

    public static void main(String[] argv) {

        if (argv.length < 2){
            System.out.println("Use:\n\tProgramName.jar InputFile OutputFile");
            return;
        }

        String inputFile = argv[0];
        String outputFile = argv[1];

        if (!(new File(inputFile).exists())) {
            System.out.println(String.format("File '%s' does not exists", inputFile));
            return;
        }

        if (inputFile.equals(outputFile)){
            System.out.println("Input and output file do not coincide");
            return;
        }


        Charset windows1252 = Charset.forName("windows-1251");

        try (InputStream in = new FileInputStream(new File(inputFile));
             Reader reader = new InputStreamReader(in, windows1252);
             OutputStream out = new FileOutputStream(outputFile);
             Writer writer = new OutputStreamWriter(out, StandardCharsets.UTF_8)) {
            writer.write(BYTE_ORDER_MARK);
            char[] buffer = new char[1024];
            int read;
            while ((read = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, read);
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
            return;
        }

        if (new File(outputFile).length() / 1000000 > 10) {

            byte[] buffer = new byte[1024];

            try {

                FileInputStream in = new FileInputStream(outputFile);
                GZIPOutputStream gzos = new GZIPOutputStream(new FileOutputStream(outputFile + ".gz"));

                int len;
                while ((len = in.read(buffer)) > 0) {
                    gzos.write(buffer, 0, len);
                }

                in.close();
                gzos.finish();
                gzos.close();

                new File(outputFile).delete();


            } catch (IOException ex) {
                ex.printStackTrace();
                return;
            }
        }


    }
}
