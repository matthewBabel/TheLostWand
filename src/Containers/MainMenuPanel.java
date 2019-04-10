package Containers;

import DataHolders.ColorDataHolder;
import DataHolders.FontDataHolder;
import DataHolders.ImageDataHolder;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.TimerTask;
import javax.swing.JComponent;
import javax.swing.JLabel;
import java.util.Timer;

/**
 * The start up screen that will take the user to other screens.
 *
 * @author Matthew Babel
 */
public class MainMenuPanel extends JPanel {

    
    private BufferedImage image;
    private final Frame FR;
    
    private Timer t;
    private final Dimension imageLocation;
    
    /**
     * Make the buttons that go to the game panel, options panel, and high
     * score panel as well as a title label and exit button.
     *
     * @param frame refrence to frame object
     */
    public MainMenuPanel(Frame frame) {      
        JComponent[] comp = new JComponent[5];
        initLabel(comp);
        initButtons(comp, frame);
        makeScreen(comp);
        
        
        FR = frame;
        imageLocation = new Dimension(FR.FRAMESIZE.width-400, 
                FR.FRAMESIZE.height+400);
        image = ImageDataHolder.getGlobImage();
        
        setUpTimer();
        this.setBackground(ColorDataHolder.getBackgroundColor());
    }

    /**
     * Set up a timer that moves a glob across the screen.
     */
    private void setUpTimer() {
        t = new Timer();
                
        TimerTask paint = new TimerTask() {
            @Override
            public void run() {
                imageLocation.height-=5;
                
                if (imageLocation.height < -400) {
                    imageLocation.height = FR.FRAMESIZE.height+400;
                    
                    if (imageLocation.width == FR.FRAMESIZE.width-400) {
                        imageLocation.width = 75;
                    } else {
                        imageLocation.width = FR.FRAMESIZE.width-400;
                    }
                }       
                repaint();
            }
        };
        
        t.schedule(paint, 30, 30);
    }
   
    private void initLabel(JComponent[] c) {
        final JLabel titleLbl = new JLabel("The Lost Wand");
        titleLbl.setFont(FontDataHolder.getPtSans180());
        titleLbl.setForeground(new Color(162, 77, 8));
        titleLbl.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        c[0] = titleLbl;
    }

    private void initButtons(JComponent[] c, Frame fr) {
        final Font REG = FontDataHolder.getPtSans70Plain();
        JButton playBtn = new JButton("PLAY!");
        playBtn.setFont(REG);
        playBtn.setMaximumSize(new Dimension(450, 250));
        playBtn.setAlignmentX(CENTER_ALIGNMENT);
        playBtn.addActionListener((java.awt.event.ActionEvent e) -> {
            fr.startGame();
        });

        JButton hsBtn = new JButton("High Scores");
        hsBtn.setFont(REG);
        hsBtn.setMaximumSize(new Dimension(450, 150));
        hsBtn.setAlignmentX(CENTER_ALIGNMENT);
        hsBtn.addActionListener((java.awt.event.ActionEvent e) -> {
            fr.showHighScore();
        });

        JButton optionsBtn = new JButton("Controls & Options");
        optionsBtn.setFont(REG);
        optionsBtn.setMaximumSize(new Dimension(650, 150));
        optionsBtn.setAlignmentX(CENTER_ALIGNMENT);
        optionsBtn.addActionListener((java.awt.event.ActionEvent e) -> {
            fr.options();
        });

        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(REG);
        exitBtn.setMaximumSize(new Dimension(250, 100));
        exitBtn.setAlignmentX(CENTER_ALIGNMENT);
        exitBtn.setAlignmentY(BOTTOM_ALIGNMENT);
        exitBtn.addActionListener((java.awt.event.ActionEvent e) -> {
            System.exit(0);
        });

        c[1] = playBtn;
        c[2] = hsBtn;
        c[3] = optionsBtn;
        c[4] = exitBtn;
    }

    private void makeScreen(JComponent[] c) {
        BoxLayout boxLayout = new BoxLayout(this, BoxLayout.Y_AXIS);

        this.setLayout(boxLayout);
        this.add(c[0]);
        this.add(c[1]);
        this.add(Box.createRigidArea(new Dimension(450, 50)));
        this.add(c[2]);
        this.add(Box.createRigidArea(new Dimension(450, 50)));
        this.add(c[3]);
        this.add(Box.createRigidArea(new Dimension(450, 50)));
        this.add(c[4]);
        
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(image, imageLocation.width, imageLocation.height, this);
    }
}
