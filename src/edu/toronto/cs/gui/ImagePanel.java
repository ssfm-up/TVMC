package edu.toronto.cs.gui;

import javax.swing.*;
import java.awt.*;


/**
 * ImagePanel.java
 *
 *
 * Created: Wed Jun 26 13:58:59 2002
 *
 * @author <a href="mailto:anya@tallinn.cs">Tafliovich Anya</a>
 * @version
 */

public class ImagePanel extends JPanel {

  // the image  
  Image image;

  public ImagePanel (Image _image) 
  {
    image = _image;
  
    if (image != null)
      setPreferredSize (new Dimension (image.getWidth(this),
				       image.getHeight(this)));
    setVisible(true);
  }

  public void paintComponent(Graphics g) {
    super.paintComponent(g); //paint background
    
    g.setFont(new Font (getFont().getFontName(), Font.BOLD, 12));
    

    // -- Draw image at its natural size,
    // -- if it exists; otherwise, display a text message
    if (image != null) 
      g.drawImage(image, 10, 10, this);
    else
      g.drawString ("No image available", 10, 100);
    
  }
  
}
