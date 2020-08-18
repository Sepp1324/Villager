package at.sepp.mods.villagers.api.util;

import net.minecraft.client.Minecraft;

import java.io.*;
import java.util.List;
import java.util.Properties;

public class FileUtil {
    private InputStream is;

    private String rawData;

    public FileUtil(String filename) throws FileNotFoundException {
        //File inFile = new File((Minecraft.func_71410_x()).field_71412_D, filename);
        //this.is = new FileInputStream(inFile);
    }

    public FileUtil(InputStream is) {
        this.is = is;
    }

    public String getRawData() {
        return this.rawData;
    }

    public void read(List<String> names, Properties prop) throws IOException {
        if (this.is == null)
            throw new IOException("There is nothing to read from ...");
        BufferedReader reader = new BufferedReader(new InputStreamReader(this.is));
        StringBuilder builder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            String name, value;
            if (line.trim().indexOf('#') == 0 || line.trim().indexOf('!') == 0)
                continue;
            builder.append(line + System.getProperty("line.separator"));
            int index = line.indexOf('=');
            if (index > 0) {
                name = line.substring(0, index).trim();
                value = line.substring(++index).trim();
            } else {
                name = line.trim();
                value = "";
            }
            names.add(name);
            prop.setProperty(name, loadConvert(value));
            this.rawData = builder.toString();
        }
    }

    private String loadConvert(String theString) {
        int len = theString.length();
        StringBuffer outBuffer = new StringBuffer(len);
        for (int x = 0; x < len; ) {
            char aChar = theString.charAt(x++);
            if (aChar == '\\') {
                aChar = theString.charAt(x++);
                if (aChar == 'u') {
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = theString.charAt(x++);
                        switch (aChar) {
                            case '0':
                            case '1':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                value = (value << 4) + aChar - 48;
                                break;
                            case 'a':
                            case 'b':
                            case 'c':
                            case 'd':
                            case 'e':
                            case 'f':
                                value = (value << 4) + 10 + aChar - 97;
                                break;
                            case 'A':
                            case 'B':
                            case 'C':
                            case 'D':
                            case 'E':
                            case 'F':
                                value = (value << 4) + 10 + aChar - 65;
                                break;
                            default:
                                throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        }
                    }
                    outBuffer.append((char)value);
                    continue;
                }
                if (aChar == 't') {
                    aChar = '\t';
                } else if (aChar == 'r') {
                    aChar = '\r';
                } else if (aChar == 'n') {
                    aChar = '\n';
                } else if (aChar == 'f') {
                    aChar = '\f';
                }
                outBuffer.append(aChar);
                continue;
            }
            outBuffer.append(aChar);
        }
        return outBuffer.toString();
    }
}