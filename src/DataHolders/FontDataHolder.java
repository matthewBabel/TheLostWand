package DataHolders;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Holds all fonts used in the program. Fonts are called with static methods
 * using there font title and point size.
 *
 * @author Matthew Babel
 */
public class FontDataHolder {

    // pt sans bold
    public static Font getPtSans35() {
        return makeFont("fonts/PT_Sans-Web-Bold.ttf", Font.BOLD, 35);
    }

    public static Font getPtSans130() {
        return makeFont("fonts/PT_Sans-Web-Bold.ttf", Font.BOLD, 130);
    }

    public static Font getPtSans170() {
        return makeFont("fonts/PT_Sans-Web-Bold.ttf", Font.BOLD, 170);
    }

    public static Font getPtSans180() {
        return makeFont("fonts/PT_Sans-Web-Bold.ttf", Font.BOLD, 180);
    }

    public static Font getPtSans200() {
        return makeFont("fonts/PT_Sans-Web-Bold.ttf", Font.BOLD, 200);
    }
    
    //pt sans bold in plain
    public static Font getPtSans60Plain() {
        return makeFont("fonts/PT_Sans-Web-Bold.ttf", Font.PLAIN, 60);
    }

    public static Font getPtSans70Plain() {
        return makeFont("fonts/PT_Sans-Web-Bold.ttf", Font.PLAIN, 70);
    }

    //pt sans regular
    public static Font getPtSansReg37() {
        return makeFont("fonts/PT_Sans-Web-Regular.ttf", Font.PLAIN, 37);
    }

    public static Font getPtSansReg60() {
        return makeFont("fonts/PT_Sans-Web-Regular.ttf", Font.PLAIN, 60);
    }

    // deja vu sans
    public static Font getDejaVu48Plain() {
        return makeFont("fonts/DejaVuSans.ttf", Font.PLAIN, 48);
    }
    
    public static Font getDejaVu50Plain() {
        return makeFont("fonts/DejaVuSans.ttf", Font.PLAIN, 50);
    }

    // lato black
    public static Font getLato60Plain() {
        return makeFont("fonts/Lato-Black.ttf", Font.PLAIN, 60);
    }

    /**
     * Makes font with given fileAddress, style and size
     *
     * @param fileAddress
     * @param style
     * @param size
     * @return the created font
     */
    public static Font makeFont(String fileAddress, int style, int size) {
        
        InputStream fontStream = ClassLoader.getSystemResourceAsStream(fileAddress);
        //InputStream fontStream = ClassLoader.getSystemResourceAsStream(fileAddress);

        Font font;
        try {
            font = Font.createFont(Font.TRUETYPE_FONT, fontStream);
            font = font.deriveFont(style, size);
            return font;
        } catch (FontFormatException | IOException e) {
            System.err.println("Font is null  makeFont : StatsCenter");
            return null;
        }
    }
}
