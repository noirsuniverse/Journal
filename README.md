# 📖 LunaLog - Digital Journal & Sketchbook  
*A hybrid Java Swing application for expressive journaling with rich text and drawing tools*

![LunaLog Screenshot](Screenshots/ss1.png)
![LunaLog Screenshot](Screenshots/ss2.png)

## ✨ Features  
**🎨 Dual-Mode Interface**  
- **Rich Text Editor** with font customization  
- **Drawing Canvas** with multiple tools (Pencil, Paintbrush, Eraser, Paint Bucket)  

**🌈 Themed Experiences**  
- 6 aesthetic themes: Cyberpunk, Pastel, Cottagecore, Monochrome, Oceanic, Retro  
- Full UI color customization (backgrounds, text, menus)  

**💾 Smart Journal Management**  
- Save entries as compressed `.journal` files (text + drawings combined)  
- Load previous entries with one click  

**✏️ Creative Tools**  
- Color picker for custom palette selection  
- Multiple brush sizes and styles  
- Font family and size customization  

## 🛠 Tech Stack  
![Java](https://img.shields.io/badge/Java-17+-lightgrey?logo=java)  ![Swing](https://img.shields.io/badge/Java_Swing-GUI-black)  ![AWT](https://img.shields.io/badge/AWT-Drawing_Utils-white)  

```
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;




public class JournalApp extends JFrame {

    private JTextPane textPane;
    private JPanel centerToolPanel;
    private DrawingPanel drawingPanel;
    private Color currentColor = Color.BLACK;
    private String currentTool = "Text";
    private JMenuBar menuBar;

    public JournalApp() {
        setTitle("LunaLog");
        setSize(1200, 800);
        setResizable(false); // Prevent resizing
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Text pane for typing
        textPane = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(400, 600));
        add(scrollPane, BorderLayout.EAST);

        // Drawing panel setup
        drawingPanel = new DrawingPanel();
        drawingPanel.setPreferredSize(new Dimension(800, 800));
        add(drawingPanel, BorderLayout.CENTER);

        // Center tool panel for icons and font selection
        centerToolPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        add(centerToolPanel, BorderLayout.SOUTH);
        addToolIcons(centerToolPanel);
        addFontSelectors(centerToolPanel);

        // Menu bar setup
        menuBar = createMenuBar();
        setJMenuBar(menuBar);

        // Center the window on the screen
        setLocationRelativeTo(null);
    }

    private void addToolIcons(JPanel panel) {
        panel.add(createToolIcon("icons/textbox.png", e -> setCurrentTool("Text")));
        panel.add(createToolIcon("icons/pencil.png", e -> setCurrentTool("Pencil")));
        panel.add(createToolIcon("icons/paintbrush.png", e -> setCurrentTool("Paintbrush")));
        panel.add(createToolIcon("icons/paintbucket.png", e -> setCurrentTool("PaintBucket")));
        panel.add(createToolIcon("icons/eraser.png", e -> setCurrentTool("Eraser")));
        panel.add(createToolIcon("icons/palette.png", e -> chooseColor()));
    }

    private JButton createToolIcon(String iconFile, ActionListener action) {
        ImageIcon icon = new ImageIcon(iconFile);
        Image scaledImage = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.addActionListener(action);
        return button;
    }

    private void addFontSelectors(JPanel panel) {
        // Font selection dropdown
        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        JComboBox<String> fontDropdown = new JComboBox<>(fonts);
        fontDropdown.setPreferredSize(new Dimension(150, 30));
        fontDropdown.addActionListener(e -> updateTextFont(fontDropdown.getSelectedItem().toString(), null));
        panel.add(fontDropdown);

        // Font size dropdown
        Integer[] fontSizes = {10, 12, 14, 16, 18, 20, 24, 28, 32, 36};
        JComboBox<Integer> fontSizeDropdown = new JComboBox<>(fontSizes);
        fontSizeDropdown.setPreferredSize(new Dimension(70, 30));
        fontSizeDropdown.addActionListener(e -> updateTextFont(null, (Integer) fontSizeDropdown.getSelectedItem()));
        panel.add(fontSizeDropdown);
    }

    private void updateTextFont(String fontName, Integer fontSize) {
        StyledDocument doc = textPane.getStyledDocument();
        javax.swing.text.Style style = textPane.addStyle("FontStyle", null);

        if (fontName != null) {
            StyleConstants.setFontFamily(style, fontName);
        }
        if (fontSize != null) {
            StyleConstants.setFontSize(style, fontSize);
        }

        textPane.setCharacterAttributes(style, true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem newItem = new JMenuItem("New");
        JMenuItem saveItem = new JMenuItem("Save");
        JMenuItem openItem = new JMenuItem("Open");
        saveItem.addActionListener(e -> saveEntry());
        fileMenu.add(newItem);
        fileMenu.add(saveItem);
        
    openItem.addActionListener(e -> loadEntries());
    fileMenu.add(openItem);
    
        menuBar.add(fileMenu);

        JMenu themesMenu = new JMenu("Themes");
        JMenuItem pastelTheme = new JMenuItem("Pastel");
        JMenuItem cyberpunkTheme = new JMenuItem("Cyberpunk");
        JMenuItem cottagecoreTheme = new JMenuItem("Cottagecore");
        JMenuItem monochromeTheme = new JMenuItem("Monochrome");
        JMenuItem oceanicTheme = new JMenuItem("Oceanic");
        JMenuItem retroTheme = new JMenuItem("Retro");
        pastelTheme.addActionListener(e -> setTheme("Pastel"));
        cyberpunkTheme.addActionListener(e -> setTheme("Cyberpunk"));
        cottagecoreTheme.addActionListener(e -> setTheme("Cottagecore"));
        monochromeTheme.addActionListener(e -> setTheme("Monochrome"));
        oceanicTheme.addActionListener(e -> setTheme("Oceanic"));
        retroTheme.addActionListener(e -> setTheme("Retro"));
        themesMenu.add(pastelTheme);
        themesMenu.add(cyberpunkTheme);
        themesMenu.add(cottagecoreTheme);
        themesMenu.add(monochromeTheme);
        themesMenu.add(oceanicTheme);
        themesMenu.add(retroTheme);
        menuBar.add(themesMenu);

        return menuBar;
    }

    private void loadEntries() {
        List<String> entries = DatabaseHelper.getJournalEntries(1);
        if (entries == null || entries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No entries found!");
            return;
        }
    
        // Display entries
        JTextArea entriesArea = new JTextArea();
        StringBuilder entryDisplay = new StringBuilder();
        for (String entry : entries) {
            entryDisplay.append(entry).append("\n\n");
        }
        entriesArea.setText(entryDisplay.toString());
        entriesArea.setEditable(false);
    
        JScrollPane scrollPane = new JScrollPane(entriesArea);
        scrollPane.setPreferredSize(new Dimension(600, 400));
    
        JOptionPane.showMessageDialog(this, scrollPane, "Journal Entries", JOptionPane.INFORMATION_MESSAGE);
    }
    


    private void setTheme(String theme) {
    Color backgroundColor;
    Color textPaneColor;
    Color textColor;
    Color menuBarColor;
    
    switch (theme) {
        case "Pastel":
            backgroundColor = new Color(255, 228, 225);
            textPaneColor = new Color(255, 250, 240);
            textColor = Color.BLACK;
            menuBarColor = new Color(255, 228, 225);  // Light pink for menu
            break;
        case "Cyberpunk":
            backgroundColor = new Color(12, 12, 50);
            textPaneColor = new Color(34, 34, 74);
            textColor = Color.GREEN;
            menuBarColor = new Color(34, 34, 74);  // Dark blue for menu
            break;
        case "Cottagecore":
            backgroundColor = new Color(250, 240, 215);
            textPaneColor = new Color(240, 230, 220);
            textColor = Color.DARK_GRAY;
            menuBarColor = new Color(250, 240, 215);  // Soft beige for menu
            break;
        case "Monochrome":
            backgroundColor = Color.BLACK;
            textPaneColor = Color.DARK_GRAY;
            textColor = Color.WHITE;
            menuBarColor = Color.DARK_GRAY;  // Dark gray for menu
            break;
        case "Oceanic":
            backgroundColor = new Color(0, 105, 148);
            textPaneColor = new Color(0, 168, 232);
            textColor = Color.WHITE;
            menuBarColor = new Color(0, 168, 232);  // Light blue for menu
            break;
        case "Retro":
            backgroundColor = new Color(255, 204, 0);
            textPaneColor = new Color(255, 239, 173);
            textColor = Color.BLACK;
            menuBarColor = new Color(255, 239, 173);  // Light yellow for menu
            break;
        default:
            return;
    }

    // Update background and foreground for the main content
    getContentPane().setBackground(backgroundColor);
    centerToolPanel.setBackground(backgroundColor);
    textPane.setBackground(textPaneColor);
    textPane.setForeground(textColor);
    
    // Update the menu bar's colors
    menuBar.setBackground(menuBarColor);
    menuBar.setForeground(textColor);

    // Update colors for buttons and combo boxes in the center tool panel
    for (Component c : centerToolPanel.getComponents()) {
        if (c instanceof JButton || c instanceof JComboBox) {
            c.setBackground(backgroundColor);
            c.setForeground(textColor);
        }
    }

    // Repaint to apply the changes
    repaint();
}


    private void setCurrentTool(String tool) {
        this.currentTool = tool;
        drawingPanel.setCurrentTool(tool);
        if ("Text".equals(tool)) {
            drawingPanel.setVisible(false);
            textPane.setVisible(true);
        } else {
            drawingPanel.setVisible(true);
            textPane.setVisible(false);
        }
    }

    private void chooseColor() {
        Color selectedColor = JColorChooser.showDialog(this, "Choose a color", currentColor);
        if (selectedColor != null) {
            currentColor = selectedColor;
            drawingPanel.setCurrentColor(selectedColor);
            textPane.setForeground(selectedColor); // Set font color
        }
    }

    private void saveEntry() {
    String content = textPane.getText();
    String title = JOptionPane.showInputDialog("Enter title for the entry:");

    if (title != null && !title.isEmpty() && content != null && !content.isEmpty()) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Journal Entry");
        fileChooser.setSelectedFile(new File(title + ".journal"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            saveJournalToFile(fileToSave, content);
            JOptionPane.showMessageDialog(this, "Journal entry saved successfully!");
        }
    } else {
        JOptionPane.showMessageDialog(this, "Title and content cannot be empty!");
    }
}

// Helper method to save both text and drawing
private void saveJournalToFile(File file, String textContent) {
    try {
        // Create a temporary folder to store text and image
        File tempFolder = new File(file.getParent(), "temp_" + System.currentTimeMillis());
        tempFolder.mkdir();

        // Save text
        File textFile = new File(tempFolder, "content.txt");
        try (FileWriter writer = new FileWriter(textFile)) {
            writer.write(textContent);
        }

        // Save drawing as an image
        File imageFile = new File(tempFolder, "drawing.png");
        ImageIO.write(drawingPanel.getCanvas(), "png", imageFile);

        // Create a ZIP archive (or a `.journal` file)
        try (FileOutputStream fos = new FileOutputStream(file);
             ZipOutputStream zos = new ZipOutputStream(fos)) {

            // Add text file to ZIP
            addToZipFile(textFile, zos);

            // Add image file to ZIP
            addToZipFile(imageFile, zos);
        }

        // Clean up temporary files
        textFile.delete();
        imageFile.delete();
        tempFolder.delete();
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error saving journal: " + e.getMessage());
    }
}

// Add a file to ZIP
private void addToZipFile(File file, ZipOutputStream zos) throws IOException {
    try (FileInputStream fis = new FileInputStream(file)) {
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);

        byte[] bytes = new byte[1024];
        int length;
        while ((length = fis.read(bytes)) >= 0) {
            zos.write(bytes, 0, length);
        }
    }
}

private void openJournalEntry() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Open Journal Entry");

    int userSelection = fileChooser.showOpenDialog(this);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToOpen = fileChooser.getSelectedFile();
        loadJournalFromFile(fileToOpen);
    }
}

// Helper method to load a journal entry
private void loadJournalFromFile(File file) {
    try {
        // Extract files from the `.journal` ZIP archive
        File tempFolder = new File(file.getParent(), "temp_" + System.currentTimeMillis());
        tempFolder.mkdir();

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
            ZipEntry zipEntry;
            while ((zipEntry = zis.getNextEntry()) != null) {
                File newFile = new File(tempFolder, zipEntry.getName());
                try (FileOutputStream fos = new FileOutputStream(newFile)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }
                }
            }
        }

        // Load text content
        File textFile = new File(tempFolder, "content.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(textFile))) {
            textPane.setText(reader.lines().reduce("", (acc, line) -> acc + line + "\n"));
        }

        // Load drawing
        File imageFile = new File(tempFolder, "drawing.png");
        BufferedImage image = ImageIO.read(imageFile);
        drawingPanel.setCanvas(image);

        // Clean up temporary files
        textFile.delete();
        imageFile.delete();
        tempFolder.delete();

        JOptionPane.showMessageDialog(this, "Journal entry loaded successfully!");
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Error opening journal: " + e.getMessage());
    }
}






    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JournalApp app = new JournalApp();
            app.setVisible(true);
        });
    }
}



class DrawingPanel extends JPanel {
    private String currentTool = "Pencil";
    private Color currentColor = Color.BLACK;
    private BufferedImage canvas;
    private Graphics2D g2d;
    private int prevX, prevY;

    public DrawingPanel() {
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(800, 800));

        canvas = new BufferedImage(800, 800, BufferedImage.TYPE_INT_ARGB);
        g2d = canvas.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        g2d.setColor(currentColor);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                prevX = e.getX();
                prevY = e.getY();
                if ("PaintBucket".equals(currentTool)) {
                    fillArea(e.getX(), e.getY(), currentColor);
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if ("Pencil".equals(currentTool)) {
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawLine(prevX, prevY, x, y);
                } else if ("Paintbrush".equals(currentTool)) {
                    g2d.setStroke(new BasicStroke(15));
                    g2d.drawLine(prevX, prevY, x, y);
                } else if ("Eraser".equals(currentTool)) {
                    g2d.setStroke(new BasicStroke(50));
                    g2d.setColor(getBackground());
                    g2d.drawLine(prevX, prevY, x, y);
                    g2d.setColor(currentColor);
                }
                prevX = x;
                prevY = y;
                repaint();
            }
        });
    }

    public void setCurrentTool(String tool) {
        this.currentTool = tool;
    }

    public void setCurrentColor(Color color) {
        this.currentColor = color;
        g2d.setColor(color);
    }

    public BufferedImage getCanvas() {  // Add this method
        return canvas;
    }

    public void setCanvas(BufferedImage newCanvas) {
        this.canvas = newCanvas;
        this.g2d = newCanvas.createGraphics();
        repaint();
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }

    private void fillArea(int x, int y, Color fillColor) {
        g2d.setColor(fillColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        repaint();
    }
}
```
## **🖥️ UI Components**
**DrawingPanel:** Handles all drawing operations with mouse listeners

**JTextPane:**	  Text editor with style support

**JToolBar:**	    Custom panel for brushes and font controls

**JMenuBar:**	    File operations and theme selection 

## **💫 How to Use**
1. **Write:** Type in the right panel with customizable fonts
2. **Draw:** Switch to canvas mode using toolbar
3. **Style:** Change themes via Themes Menu
4. **Save:** Export as ```.journal``` file (text + drawing combined)

## **⌨️Run from source**
javac JournalApp.java
java JournalApp

## **🌟Getting Started**
1. **Requirements:** Java 17+
2. Clone the repo
   git clone https://github.com/yourusername/LunaLog.git
3. Compile & run:
   cd src && javac *.java && java JournalApp
   
## **🤝 Contrinuting**
PRs welcome! To add:
- More brush types (spray, shapes)
- Cloud sync functionality
- PDF export
