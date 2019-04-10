package Files;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the text file containing the high scores of the game.
 *
 * @author Matt Babel
 */
public class HighScoreFileMngr {

    private final List<String> FILEDATA;
    private final String FILEPATH;

    public HighScoreFileMngr(String file) {
        FILEPATH = file;
        FILEDATA = new ArrayList<>();

        setUp();
    }

    /**
     * Read from high scores file and add lines to fileData.
     */
    private void setUp() {
        try {

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(this.getClass().getResourceAsStream('/'+FILEPATH)));

            // getClass().getResourceAsStream("/" + FILEPATH)));
            //HighScoreFileMngr.class.getResourceAsStream(FILEPATH), "UTF-8"));
            String line;
            while ((line = br.readLine()) != null) {
                FILEDATA.add(line);
            }
            br.close();
        } catch (UnsupportedEncodingException ex) {
            System.out.println("Unsupported encoding exception");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found exception");
        } catch (IOException ex) {
            System.out.println("input output exception");
        }
    }

    /**
     * Add a line to the file data, sort the line into the correct place, 0 is
     * the largest.
     *
     * @param s line to be added to file data
     */
    public void addLine(String s) {
        int score = getNum(s);
        boolean added = false;

        for (int i = 0; i < FILEDATA.size(); i++) {
            if (score > getNum(FILEDATA.get(i))) {
                FILEDATA.add(i, s);
                added = true;
                break;
            }
        }

        if (!added) {
            FILEDATA.add(s);
        }
    }

    /**
     * Gets number at the beginning of a string by looking for first space in
     * string.
     *
     * @param string given string
     * @return the number
     */
    private int getNum(String s) {
        int n = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '_') {
                String s2 = s.substring(0, i);
                
                try {
                n = Integer.parseInt(s2);
                } catch(NumberFormatException e) {
                    System.out.println("found exception");
                }
                break;
            }
        }
        return n;
    }

    /**
     * Will return the place of this score in the current high score file but
     * does not add score to file.
     *
     * @param score the score
     * @return the place in the high score list
     */
    public int getPlace(int score) {
        for (int i = 0; i < FILEDATA.size(); i++) {
            if (score > getNum(FILEDATA.get(i))) {
                return i + 1;
            }
        }
        return FILEDATA.size() + 1;
    }

    /**
     * Get the line of data at an index.
     *
     * @param i index
     * @return the line of data at index
     */
    public String getLine(int i) {
        return FILEDATA.get(i);
    }

    /**
     * Get number of lines of data.
     *
     * @return number of liens of data
     */
    public int getSize() {
        return FILEDATA.size();
    }

    /**
     * output all lines of data to console.
     */
    public void outputLines() {
        FILEDATA.stream().forEach((s) -> {
            System.out.println(s);
        });
    }

    /**
     * saves the data to its own text file.
     */
    public void save() {
        File dataFile = null;
        File tempFile = null;

        String className = this.getClass().getName().replace('.', '/');
        String classJar = this.getClass().getResource("/" + className + ".class").toString();
        if (!classJar.startsWith("jar:")) {
            dataFile = new File(this.getClass().getResource('/'+FILEPATH).getFile());
            tempFile = new File(this.getClass().getResource("/data/myTempFile.txt").getFile());
            
        } else {
            InputStream stream = ClassLoader.getSystemResourceAsStream("/" + FILEDATA);
            URL path = getClass().getProtectionDomain().getCodeSource().getLocation();

            dataFile = new File(this.getClass().getResource('/'+FILEPATH).getFile());           
            tempFile = new File(this.getClass().getResource("/data/myTempFile.txt").getFile());
        }
        try {
            FileOutputStream file = new FileOutputStream(tempFile);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(FILEDATA);
            out.close();
            file.close();
            
            
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(tempFile), "UTF-8"));

            for (String line : FILEDATA) {
                bw.write(line);
                bw.newLine();
           }

            bw.close();
            dataFile.delete();
            tempFile.renameTo(dataFile);
            tempFile.createNewFile();
        } catch (IOException ex) {
            System.out.println(tempFile.toString());
            System.out.println(dataFile.toString());
        }
    }
}
