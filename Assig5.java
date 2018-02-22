
/* Eric Hunzeker cs0401 1060 lab wednesday 2pm reed armstrong

this is a painting program that you can alter shapes and colors and patterns. also this 
works with saving and opening files. the program can start fresh and reset everything
to defaults.

*/





import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.print.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Assig5
{
    public static String software = "Mosaic Art 1.0";
    private Mosaic m;
    private DrawPanel thePanel; 	// DrawPanel is a subclass of JPanel
                                                                    // See details below.
    private JPanel buttonPanel;
    private JFrame theWindow;
    private JButton paintIt, eraseIt, editIt;

    // ArrayList of Mosaic to store the individual shapes.  Note that
    // since Mosaic is the superclass of both MCircle and MSquare, both
    // shapes can be stored in this ArrayList
    private ArrayList<Mosaic> chunks;

    private double X, Y, startX, startY;
    private double newSize;
    private Color newColor, color1, color2;
    private int selected;

    private boolean painting, erasing, shapeTwist, sq, cr, colorTwist, editing, recolorThis, resizeThis, saved;
    private String currFile, fName;
    
    private JPopupMenu popup;
    private JMenuBar theBar;
    private JMenu fileMenu, defaultMenu, effectsMenu;
    private JMenuItem endProgram, saveAs, printScene, setColor, setSize, setShape, twistShape, twistColor, circle, square, stopTwistShape,
            newPaint, open, save, saveJPG, stopTwistColor, recolor, resize, reshape, undo;

    public Assig5()
    {
        theWindow = new JFrame(software);
        theWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        thePanel = new DrawPanel(600, 600);
        newSize = 15;
        newColor = Color.RED;
        color2 = null;
        currFile = null;

        selected = -1;
        painting = false;
        erasing = false;
        editing = false;
        shapeTwist = false;
        colorTwist = false;
        recolorThis = false;
        resizeThis = false;
        saved = false;
        cr = false;
        sq = false;
        paintIt = new JButton("Paint");
        eraseIt = new JButton("Erase");
        editIt = new JButton("Edit");
        
        ActionListener bListen = new ButtonListener();
        paintIt.addActionListener(bListen);
        eraseIt.addActionListener(bListen);
        editIt.addActionListener(bListen);
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1,2));
        buttonPanel.add(paintIt);
        buttonPanel.add(eraseIt);
        buttonPanel.add(editIt);
        
        theWindow.add(buttonPanel, BorderLayout.SOUTH);
        theWindow.add(thePanel, BorderLayout.NORTH);

        theBar = new JMenuBar();
        theWindow.setJMenuBar(theBar);
        fileMenu = new JMenu("File");
        defaultMenu = new JMenu("Defaults");
        effectsMenu = new JMenu("Effects");
        setShape = new JMenu("Set Shape");
        
        popup = new JPopupMenu();
        
        theBar.add(fileMenu);
        theBar.add(defaultMenu);
        theBar.add(effectsMenu);
        
        newPaint = new JMenuItem("New");
        open = new JMenuItem("Open");
        save = new JMenuItem("Save");
        saveAs = new JMenuItem("Save As");
        saveJPG = new JMenuItem("Save As JPG");
        undo = new JMenuItem("Undo");
        printScene = new JMenuItem("Print");
        endProgram = new JMenuItem("Exit");
        setColor = new JMenuItem("Set Color");
        setSize = new JMenuItem("Set Size");
        recolor = new JMenuItem("Recolor");
        resize = new JMenuItem("Resize");
        reshape = new JMenuItem("Reshape");
        
        twistShape = new JMenuItem("Start Twisting Shapes");
        stopTwistShape = new JMenuItem("Stop Twisting Shapes");
        
        twistColor = new JMenuItem("Set Twisting Colors");
        stopTwistColor = new JMenuItem("Stop Twisting Colors");
        square = new JMenuItem("Square");
        circle = new JMenuItem("Circle");
        
        fileMenu.add(newPaint);
        fileMenu.add(open);
        fileMenu.add(save);
        fileMenu.add(saveAs);
        fileMenu.add(saveJPG);
        fileMenu.add(undo);
        fileMenu.add(printScene);
        fileMenu.add(endProgram);
        defaultMenu.add(setColor);
        defaultMenu.add(setSize);
        defaultMenu.add(setShape);
        effectsMenu.add(twistShape);
        effectsMenu.add(twistColor);
        setShape.add(square);
        setShape.add(circle);
        popup.add(recolor);
        popup.add(resize);
        popup.add(reshape);
        
        saveAs.addActionListener(bListen);
        printScene.addActionListener(bListen);
        endProgram.addActionListener(bListen);
        setColor.addActionListener(bListen);
        setSize.addActionListener(bListen);
        square.addActionListener(bListen);
        circle.addActionListener(bListen);
        twistShape.addActionListener(bListen);
        twistColor.addActionListener(bListen);
        stopTwistShape.addActionListener(bListen);
        stopTwistColor.addActionListener(bListen);
        newPaint.addActionListener(bListen);
        open.addActionListener(bListen);
        save.addActionListener(bListen);
        saveJPG.addActionListener(bListen);
        recolor.addActionListener(bListen);
        resize.addActionListener(bListen);
        reshape.addActionListener(bListen);
        undo.addActionListener(bListen);
        

        theWindow.pack();
        theWindow.setVisible(true);
    }

    private class DrawPanel extends JPanel
    {
        private int prefwid, prefht;

        // Initialize the DrawPanel by creating a new ArrayList for the images
        // and creating a MouseListener to respond to clicks in the panel.
        public DrawPanel(int wid, int ht)
        {
            prefwid = wid;
            prefht = ht;

            chunks = new ArrayList<Mosaic>();

            // Add MouseListener to this JPanel to respond to the user
            // pressing the mouse.  In your assignment you will also need a
            // MouseMotionListener to respond to the user dragging the mouse.
            addMouseListener(new MListen());
            addMouseMotionListener(new MListen());  
        }

        // This method allows a window that encloses this panel to determine
        // how much space the panel needs.  In particular, when the "pack()"
        // method is called from an outer JFrame, this method is called
        // implicitly and the result determines how much space is needed for
        // the JPanel
        public Dimension getPreferredSize()
        {
            return new Dimension(prefwid, prefht);
        }

        // This method is responsible for rendering the images within the
        // JPanel.  You should not have to change this code.
        public void paintComponent (Graphics g)       
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            for (int i = 0; i < chunks.size(); i++)
            {
                chunks.get(i).draw(g2d);
            }
        }

        // Add a new Mosaic and repaint.  The repaint() method call requests
        // that the panel be redrawn.  Make sure that you call repaint()
        // after changes to your scenes so that the changes are actually
        // exhibited in the display.
        public void add(Mosaic m)
        {
            chunks.add(m);
            repaint();
        }

        // Remove the Mosaic at index i and repaint
        public void remove(int i)
        {
            if (chunks.size() > i)
                    chunks.remove(i);
            repaint();
        }

        // Select a Mosaic that contains the point (x, y).  Note that this
        // is using the contains() method of the Mosaic class, which in turn
        // is checking within the underlying RectangularShape of the object.
        public int select(double x, double y)
        {
            for (int i = 0; i < chunks.size(); i++)
            {
                if (chunks.get(i).contains(x, y))
                {
                    return i;
                }
            }
            return -1;
        }
    }

    // Save the images within the window to a file.  Run this program to see the 
    // format of the saved file.
    public void saveImages()
    {
            try
            {   if(currFile != null && currFile.contains(".txt"))
                {
                    PrintWriter P = new PrintWriter(new File(currFile));
                    P.println(chunks.size());
                    for (int i = 0; i < chunks.size(); i++)
                    {
                        P.println(chunks.get(i).saveFile());
                    }
                    saved = true;
                    P.close();
                }
            }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(theWindow, "I/O Problem - File not Saved");
            }
    }
    
    public void openImages() throws IOException
    {
        try
        {
            fName = JOptionPane.showInputDialog("Enter the name of the file");
            File inFile = new File(fName);
            Scanner fileIn = new Scanner(inFile);
            int storage = Integer.parseInt(fileIn.nextLine());
            
            for(int i = 0; i < storage; i++)
            {
                String[] s; //stores stuff before color
                String[] s2;
                String temp = fileIn.nextLine(); 
                if(temp.contains("Circle"))
                {
                    s = temp.split(","); //gathers a plathora of info such as shape, size, and coordinates
                    temp = temp.substring(temp.lastIndexOf(",") + 1); //gets rid of last comma to commence the reading of the colors
                    s2 = temp.split(":"); //reads in the colors (happily)
                    
                    Color c = new Color(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]), Integer.parseInt(s2[2])); //creates a new color based on rgb values
                    
                    m = new MCircle(Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]), c); //mimmicks the shape and all of its attributes from the txt file
                    
                    theWindow.setTitle(software + " - " + fName); //sets the windows file name
                    chunks.add(m); 
                }
                else
                {
                    s = temp.split(",");
                    temp = temp.substring(temp.lastIndexOf(",") + 1);
                    s2 = temp.split(":");
                    
                    Color c = new Color(Integer.parseInt(s2[0]), Integer.parseInt(s2[1]), Integer.parseInt(s2[2]));
                    
                    m = new MSquare(Double.parseDouble(s[1]), Double.parseDouble(s[2]), Double.parseDouble(s[3]), c);
                    
                    theWindow.setTitle(software + " - " + fName);
                    chunks.add(m);
                }
                    
            }
            thePanel.repaint();
        }
        
        catch(IOException o)
        {
            JOptionPane.showMessageDialog(null, "File not found");
        }
    }
    
    

    // Listener for some buttons.  Note that the JMenuItems are also listened
    // for here.  Like JButtons, JMenuItems also generate ActionEvents when
    // they are clicked on.  You will need to add more JButtons and JMenuItems
    // to your program and the logic of handling them will also be more
    // complex.  See details in the Assignment 5 specifications.
    private class ButtonListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            if(e.getSource() == newPaint)
            {
                int choose = 1;
                if(!saved)
                {
                    
                        choose = JOptionPane.showConfirmDialog(null, "Save Scene?", "Choose Yes or No", JOptionPane.YES_NO_OPTION, 0);
                    
                    
                    if(choose == JOptionPane.YES_OPTION)
                    {
                        try
                        {
                            currFile = JOptionPane.showInputDialog(theWindow,"Enter new file name");
                            saveImages();
                        }
                        catch(NullPointerException n)
                        {
                            JOptionPane.showMessageDialog(null, "File not saved");
                        }
                    }
                    
                }
                
                //reset to defualt values
                theWindow.setTitle(software);
                chunks.clear();
                thePanel.removeAll();
                thePanel.repaint();
                newSize = 15;
                newColor = Color.RED;
                color1 = null;
                color2 = null;
                currFile = null;

                selected = -1;
                painting = false;
                erasing = false;
                editing = false;
                shapeTwist = false;
                colorTwist = false;
                recolorThis = false;
                resizeThis = false;
                saved = false;
                cr = false;
                sq = false;
                paintIt.setForeground(Color.BLACK);
                eraseIt.setForeground(Color.BLACK);
                editIt.setForeground(Color.BLACK);
                effectsMenu.add(twistShape, 0);
                effectsMenu.remove(stopTwistShape);
                effectsMenu.add(twistColor);
                effectsMenu.remove(stopTwistColor);
                
                
            }
            if (e.getSource() == paintIt)
            {
                painting = true;
                paintIt.setForeground(Color.RED);
                erasing = false;
                eraseIt.setForeground(Color.BLACK);
                editing = false;
                editIt.setForeground(Color.BLACK);
            }
            else if (e.getSource() == eraseIt)
            {
                painting = false;
                paintIt.setForeground(Color.BLACK);
                erasing = true;
                eraseIt.setForeground(Color.RED);
                editing = false;
                editIt.setForeground(Color.BLACK);
            }
            else if (e.getSource() == editIt)
            {
                painting = false;
                paintIt.setForeground(Color.BLACK);
                erasing = false;
                eraseIt.setForeground(Color.BLACK);
                editing = true;
                editIt.setForeground(Color.RED);
            }
            
            else if (e.getSource() == saveAs)
            {
                try
                {
                    currFile = JOptionPane.showInputDialog(theWindow,"Enter new file name");
                    saveImages();
                }
                catch(NullPointerException n)
                {
                    JOptionPane.showMessageDialog(null, "File not saved");
                }
                        
                
                if(currFile != null && currFile.contains(".txt"))
                {
                    theWindow.setTitle(software + " - " + currFile);
                    saved = true;
                }
            }
            else if(e.getSource() == save)
            {
                if(currFile == null) //didnt type stuff in? whats up? were gonna store this as untitled.txt since a lot of programs like to do that as well
                    currFile = "untitled.txt";
                saveImages();
                theWindow.setTitle(software + " - " + currFile);
            }
            else if (e.getSource() == endProgram)
            {
                if(!saved)
                {
                    int choose = JOptionPane.showConfirmDialog(null, "Save Scene?", "Choose Yes or No", JOptionPane.YES_NO_OPTION, 0);

                    if(choose == 0)
                    {
                        currFile = JOptionPane.showInputDialog(theWindow,"Enter new file name");
                        saveImages();
                    }
                }
                
                System.exit(0);
            }
            else if (e.getSource() == printScene)
            {
                Printable thePPanel = new thePrintPanel(thePanel); 
                PrinterJob job = PrinterJob.getPrinterJob();
                job.setPrintable(thePPanel);
                boolean ok = job.printDialog();
                if (ok) 
                {
                    try {
                    job.print();
                    } 
                
                    catch (PrinterException ex) {
            /* The job did not successfully complete */
                    }
                }
            }
            if(e.getSource() == twistShape)
            {
                shapeTwist = true;
                
                effectsMenu.add(stopTwistShape, 0); //change back and forth between stop and start in menu
                effectsMenu.remove(twistShape);
            }
            if(e.getSource() == stopTwistShape)
            {
                shapeTwist = false;
                
                effectsMenu.add(twistShape, 0); 
                effectsMenu.remove(stopTwistShape);
            }
            
            if(e.getSource() == circle)
            {
                cr = true;
                sq = false;
                
                if(colorTwist) //for all of these, if defaults are altered while twisting is on, twisting turns off
                {
                    colorTwist = false;
                    effectsMenu.add(twistColor);
                    effectsMenu.remove(stopTwistColor);
                }
                if(shapeTwist)
                {
                    shapeTwist = false;
                    effectsMenu.add(twistShape, 0);
                    effectsMenu.remove(stopTwistShape);
                }
            }
            else if(e.getSource() == square)
            {
                
                cr = false;
                sq = true;
                
                if(colorTwist)
                {
                    colorTwist = false;
                    effectsMenu.add(twistColor);
                    effectsMenu.remove(stopTwistColor);
                }
                if(shapeTwist)
                {
                    shapeTwist = false;
                    effectsMenu.add(twistShape, 0);
                    effectsMenu.remove(stopTwistShape);
                }
            }
            
            if(e.getSource() == setSize)
            {
                try
                {
                    newSize = Integer.parseInt(JOptionPane.showInputDialog("Enter New Default Size"));
                }
                catch(NullPointerException n)
                {
                    
                }
                catch(NumberFormatException i)
                {
                    JOptionPane.showMessageDialog(null, "Size not changed - invalid size");
                }
                
                if(colorTwist)
                {
                    colorTwist = false;
                    effectsMenu.add(twistColor);
                    effectsMenu.remove(stopTwistColor);
                }
                if(shapeTwist)
                {
                    shapeTwist = false;
                    effectsMenu.add(twistShape, 0);
                    effectsMenu.remove(stopTwistShape);
                }
            }
            if(e.getSource() == setColor)
            {
                newColor = JColorChooser.showDialog(null, "Choose a color", Color.RED);
                if(colorTwist)
                {
                    colorTwist = false;
                    effectsMenu.add(twistColor);
                    effectsMenu.remove(stopTwistColor);
                }
                if(shapeTwist)
                {
                    shapeTwist = false;
                    effectsMenu.add(twistShape, 0);
                    effectsMenu.remove(stopTwistShape);
                }
                
            }
            if(e.getSource() == twistColor)
            {
                colorTwist = true;
                effectsMenu.add(stopTwistColor);
                effectsMenu.remove(twistColor);
                
                color1 = JColorChooser.showDialog(null, "Choose a first color", Color.RED);
                color2 = JColorChooser.showDialog(null, "Choose a second color", Color.RED);
            }
            if(e.getSource() == stopTwistColor)
            {
                colorTwist = false;
                effectsMenu.add(twistColor);
                effectsMenu.remove(stopTwistColor);
                
            }
            if(e.getSource() == recolor)
            {
                recolorThis = true; 
                m.col = JColorChooser.showDialog(null, "Choose a new color", Color.RED);
                thePanel.repaint();
                recolorThis = false;
                m.highlight(false);
            }
            if(e.getSource() == resize)
            {
                resizeThis = true;
                try
                {
                    m.setSize(Integer.parseInt(JOptionPane.showInputDialog("Enter New Size")));
                }
                catch(NullPointerException n)
                {
                    
                }
                catch(NumberFormatException i)
                {
                    JOptionPane.showMessageDialog(null, "Size not changed - invalid size");
                }
                
                thePanel.repaint();
                m.highlight(false);
                resizeThis = false;
                
            }
            if(e.getSource() == reshape)
            {
                Color tempCol = m.col;
                double sizey = m.getSize(); //collect all data
                double xey = m.sh.getCenterX();
                double yey = m.sh.getCenterY();
                
                if(m.toString().contains("Circle"))
                {
                    chunks.remove(m);
                    m = new MSquare(sizey, xey, yey, tempCol); //change shape
                }
                else
                {
                    chunks.remove(m);
                    m = new MCircle(sizey, xey, yey, tempCol);
                }
                
                chunks.add(m);
                thePanel.repaint();
                
                
            }
            if(e.getSource() == saveJPG)
            {
                try
                {
                    BufferedImage image = new BufferedImage(600, 600, BufferedImage.TYPE_INT_RGB); //make new image off set size
                    thePanel.print(image.getGraphics()); //turn jpanel to graphic
                    File outputfile = new File(currFile.replace(".txt", ".jpg")); //replace extension
                    ImageIO.write(image, "jpg", outputfile);
                }
                catch(IOException o)
                {
                    JOptionPane.showMessageDialog(null, "Could not save as .jpg");
                }
                catch(NullPointerException n)
                {
                    JOptionPane.showMessageDialog(null, "Save file as .txt first");
                }
                
                theWindow.setTitle(software + " - " + currFile);
            }
            if(e.getSource() == open)
            {
                try
                {
                    openImages();
                } 

                catch (IOException ex) 
                {
                    JOptionPane.showMessageDialog(null, "Unable to open image");
                }
            }
            if(e.getSource() == undo)
            {
                chunks.remove(chunks.size() - 1);
                thePanel.repaint();
            }
            
            
        }
    }

    // Simple mouse event handling to allow a mousePressed to add or remove
    // a Mosaic from the display.  You will need to enhance this
    // MouseAdapter and you will also need to add a MouseMotionListener to
    // your program.  In this simple program all of the Mosaics drawn are
    // MCircles and they all have the same size and color.  You must add in
    // your program the ability to change all of these attributes.
    private class MListen extends MouseAdapter
    {
        public void mousePressed(MouseEvent e)
        {
            X = e.getX();  // Get the location where mouse was pressed
            Y = e.getY();
            
            saved = false;
            
            if (painting)
            {
                // create new MCircle and add it to the ArrayList
                if(!cr && !sq)
                    cr = true;
                
                if(!shapeTwist && cr && !colorTwist) //all of combinations of twisting and shapes 
                    m = new MCircle(newSize, X, Y, newColor);
                else if(!shapeTwist && sq && !colorTwist)
                    m = new MSquare(newSize, X, Y, newColor);
                else if(shapeTwist && !colorTwist)
                {
                    if(chunks.size() % 2 != 0)
                        m = new MCircle(newSize, X, Y, newColor);
                    else
                        m = new MSquare(newSize, X, Y, newColor);
                }
                else if(!shapeTwist && cr && colorTwist)
                {
                    if(chunks.size() % 2 != 0)
                        m = new MCircle(newSize, X, Y, color1);
                    else
                        m = new MCircle(newSize, X, Y, color2);
                }
                else if(!shapeTwist && sq && colorTwist)
                {
                    if(chunks.size() % 2 != 0)
                        m = new MSquare(newSize, X, Y, color1);
                    else
                        m = new MSquare(newSize, X, Y, color2);
                }
                else if(shapeTwist && colorTwist)
                {
                    if(chunks.size() % 2 != 0)
                    {
                        if(chunks.size() % 2 != 1)
                            m = new MCircle(newSize, X, Y, color1);
                        else
                            m = new MSquare(newSize, X, Y, color2);
                    }
                    else
                    {
                        if(chunks.size() % 2 != 1)
                            m = new MCircle(newSize, X, Y, color1);
                        else
                            m = new MSquare(newSize, X, Y, color2);
                    }
                }
                
                thePanel.add(m);
            }
            else if (erasing)
            {
                // see if the point is within a shape -- if so delete
                // that shape
                int loc = thePanel.select(X, Y);
                if (loc > -1)
                {
                    thePanel.remove(loc);
                }
            }	
            
            else if(editing)
            {  
                for(int i = 0; i < chunks.size(); i++)
                {
                    m = chunks.get(i); 
                    if(X <= m.sh.getMaxX() && X >= m.sh.getMinX() && Y <= m.sh.getMaxY() && Y >= m.sh.getMinY() && e.getButton() == MouseEvent.BUTTON3) //right click inside shape
                    {
                        m.highlight(true);
                        popup.show(e.getComponent(), e.getX(), e.getY());
                        
                        i = chunks.size();
                    }
                    if(!popup.isShowing()) //if you click away, shape gets unhighlighted
                        m.highlight(false);
                    
                    if(m.sh.contains(X, Y))
                    {
                        thePanel.select(m.sh.getCenterX(), m.sh.getCenterY());
                        startX = X;
                        startY = Y;
                        selected = 1;
                    }
                }
                thePanel.repaint();
                
            }
        }
        
        public void mouseDragged(MouseEvent e)
        {
            X = e.getX();  // Get the location where mouse was pressed
            Y = e.getY();
            
            saved = false;
            
            if (painting)
            {
                // create new MCircle and add it to the ArrayList
                
                
                if(Math.sqrt(Math.pow((Y - m.sh.getCenterY()), 2) + Math.pow((X - m.sh.getCenterX()), 2)) >= newSize)
                {
                    if(!cr && !sq)
                    cr = true;
                
                if(!shapeTwist && cr && !colorTwist) //same as clicked
                    m = new MCircle(newSize, X, Y, newColor);
                else if(!shapeTwist && sq && !colorTwist)
                    m = new MSquare(newSize, X, Y, newColor);
                else if(shapeTwist && !colorTwist)
                {
                    if(chunks.size() % 2 != 1)
                        m = new MCircle(newSize, X, Y, newColor);
                    else
                        m = new MSquare(newSize, X, Y, newColor);
                }
                else if(!shapeTwist && cr && colorTwist)
                {
                    if(chunks.size() % 2 != 1)
                        m = new MCircle(newSize, X, Y, color1);
                    else
                        m = new MCircle(newSize, X, Y, color2);
                }
                else if(!shapeTwist && sq && colorTwist)
                {
                    if(chunks.size() % 2 != 1)
                        m = new MSquare(newSize, X, Y, color1);
                    else
                        m = new MSquare(newSize, X, Y, color2);
                }
                else if(shapeTwist && colorTwist)
                {
                    if(chunks.size() % 2 != 1)
                    {
                        if(chunks.size() % 2 != 1)
                            m = new MCircle(newSize, X, Y, color1);
                        else
                            m = new MSquare(newSize, X, Y, color2);
                    }
                    else
                    {
                        if(chunks.size() % 2 != 1)
                            m = new MCircle(newSize, X, Y, color1);
                        else
                            m = new MSquare(newSize, X, Y, color2);
                    }
                }
                
                thePanel.add(m);
                }
                    
            }
            else if (erasing)
            {
                // see if the point is within a shape -- if so delete
                // that shape
                int loc = thePanel.select(X, Y);
                if (loc > -1)
                {
                    thePanel.remove(loc);
                }
            }	
            
            else if(editing)
            {
                double newX, newY;
                if(e.getButton() == MouseEvent.MOUSE_RELEASED)
                {
                    newX = e.getXOnScreen();
                    newY = e.getYOnScreen();
                    m.move(newX, newY);
                    
                }
                
            }
            
        }
    }

    public static void main(String [] args)
    {
        new Assig5();
    }
}

    // This class is taken from the Web and is somewhat buggy but it does a basic
    // print of the panel.
    class thePrintPanel implements Printable
    {
        JPanel panelToPrint;

        public int print(Graphics g, PageFormat pf, int page) throws
                                                        PrinterException
        {
            if (page > 0) { /* We have only one page, and 'page' is zero-based */
                return NO_SUCH_PAGE;
            }

            /* User (0,0) is typically outside the imageable area, so we must
             * translate by the X and Y values in the PageFormat to avoid clipping
             */
            Graphics2D g2d = (Graphics2D)g;
            AffineTransform t = new AffineTransform();
            t.scale(0.9, 0.9);
            g2d.transform(t);
            g2d.translate(pf.getImageableX(), pf.getImageableY());
                    //pf.setOrientation(PageFormat.REVERSE_LANDSCAPE);
            /* Now print the window and its visible contents */
            panelToPrint.printAll(g);

            /* tell the caller that this page is part of the printed document */
            return PAGE_EXISTS;
        }

        public thePrintPanel(JPanel p)
        {
            panelToPrint = p;
        }
    }