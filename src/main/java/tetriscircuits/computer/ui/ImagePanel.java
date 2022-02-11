package tetriscircuits.computer.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {
    
    private final BufferedImage image = new BufferedImage(256, 256, BufferedImage.TYPE_INT_RGB);
    
    private final Color BACKGROUND_COLOR = new Color(0x6A6D6A);
    private final Color PLAYFIELD_COLOR = new Color(0x000000);
    private final Color BLOCK_COLOR = new Color(0x4B30E3); 
    
    private int[] readBuffer = new int[32 * 32];
    private int[] writeBuffer = new int[32 * 32];
    private int[] thirdBuffer = new int[32 * 32];
    
    public ImagePanel() {
        setPreferredSize(new Dimension(512, 512));
    }
    
    private synchronized int[] getNextWriteBuffer() {
        final int[] tempBuffer = writeBuffer;
        writeBuffer = thirdBuffer;
        thirdBuffer = tempBuffer;
        return writeBuffer;
    }
    
    private synchronized int[] getNextReadBuffer() {
        final int[] tempBuffer = readBuffer;
        readBuffer = thirdBuffer;
        thirdBuffer = tempBuffer;
        return readBuffer;
    }    
    
    public void updateImage() {
        final int[] buffer = getNextReadBuffer();
        final Graphics2D g = image.createGraphics();
        for (int y = 31; y >= 0; --y) {
            for (int x = 31; x >= 0; --x) {
                final Color color;
                switch (buffer[(y << 5) | x]) {
                    case 0:
                        color = PLAYFIELD_COLOR;
                        break;
                    case 1:
                        color = BLOCK_COLOR;
                        break;
                    default:
                        color = BACKGROUND_COLOR;
                        break;
                }
                g.setColor(color);
                g.fillRect(x << 3, y << 3, 8, 8);
            }
        }
        repaint();
    }
    
    @Override
    public void update(final Graphics g) {
        paint(g);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final Dimension size = getSize();
        int imageX = 0;
        int imageY = 0;
        int imageSize = 512;
        if (size.width > size.height) {
            imageSize = size.height;
            imageX = (size.width - imageSize) >> 1;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, imageX, size.height);
            g.fillRect(imageX + imageSize, 0, size.width - imageX - imageSize, size.height);
        } else if (size.height > size.width) {
            imageSize = size.width;
            imageY = (size.height - imageSize) >> 1;
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, size.width, imageY);
            g.fillRect(0, imageY + imageSize, size.width, size.height - imageY - imageSize);
        }
        g.drawImage(image, imageX, imageY, imageSize, imageSize, null);
    }    
}
