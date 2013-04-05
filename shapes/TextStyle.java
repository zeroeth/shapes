package shapes;

import java.awt.*;
import java.awt.geom.*;

/**
 * Used to change the visual style of text displayed in the game. Can be used
 * to customize text's font, size, color, etc.
 * <p>
 * There are a few places you can display text, including {@link Shape#say},
 * {@link Game#setTitle}, {@link Game#setSubtitle}, and {@link Counter}.
 * <p>
 * <strong>Example usage:</strong>
 * <p>
 * <code>
 *  TextStyle speechStyle = new TextStyle("Helvetica", 12, Color.BLACK);<br />
 *  circle.setSpeechStyle(speechStyle);<br />
 *  circle.say("Hello, world!");
 * </code>
 */
public class TextStyle {
  private String fontName;
  private int fontSize;
  private Color color;
  private Color backgroundColor;
  private boolean bold;
  private boolean italic;

  enum ReferencePointLocation { CENTER, BOTTOM_LEFT, TOP_LEFT, BOTTOM_CENTER };

  public TextStyle(String fontName, int fontSize, Color color) {
    setFontName(fontName);
    setFontSize(fontSize);
    setColor(color);
    setBold(bold);
    setItalic(italic);
  }

  void applyTo(Graphics2D g) {
    g.setColor(color);
    g.setFont(getFont());
  }

  Font getFont() {
    int style;
    if (!bold && !italic) {
      style = Font.PLAIN;
    } else {
      style = (bold ? Font.BOLD : 0) | (italic ? Font.ITALIC : 0);
    }
    
    return new Font(fontName, style, fontSize);
  }

  void renderString(
    String string,
    Point referencePoint,
    ReferencePointLocation referenceLocation,
    Graphics2D g,
    Point speechOrigin
  ) {
    int boxMargin = 3;

    // find height and width of rendered speech
    FontMetrics metrics = g.getFontMetrics(getFont());
    String[] lines = string.split("\n");
    double width = 0.0;
    for (String line : lines) {
      width = Math.max(width, metrics.stringWidth(string));
    }
    // getAscent() includes room for accents, etc., so we shrink it (by
    // an arbitrary amount)
    double wordHeight = metrics.getAscent() * 0.8;
    double spaceHeight = metrics.getHeight() - wordHeight;
    double height =
      (lines.length - 1) * spaceHeight + lines.length * wordHeight;
    Vector offset = null; // from reference point to bottom left
    if (backgroundColor != null) {
      width += 2 * boxMargin;
      height += 2 * boxMargin;
    }
    switch (referenceLocation) {
      case CENTER:
        offset = new Vector(width / -2.0, height / -2.0);
        break;
      case BOTTOM_LEFT:
        offset = new Vector(0, 0);
        break;
      case TOP_LEFT:
        offset = new Vector(0, -1.0 * height);
        break;
      case BOTTOM_CENTER:
        offset = new Vector(width / -2.0, 0);
        break;
    }
    Point bottomLeft = referencePoint.translation(offset);
    Point textBottomLeft = bottomLeft;
    Vector lineOffset = new Vector(0, wordHeight + spaceHeight);

    // render background
    if (backgroundColor != null) {
      height += metrics.getDescent();
      Point topLeft = bottomLeft.translation(new Vector(0, height));

      g.setColor(backgroundColor);
      g.fillRect(
        topLeft.getCanvasX(),
        topLeft.getCanvasY(),
        (int) width,
        (int) height
      );

      textBottomLeft = bottomLeft.translation(
        new Vector(boxMargin, boxMargin + metrics.getDescent())
      );

      // render speech bubble "foot"
      if (speechOrigin != null) {
        Point third = bottomLeft.translation(new Vector(7, 0));  // TODO: height?
        int[] x = new int[] {
          bottomLeft.getCanvasX(),
          speechOrigin.getCanvasX(),
          third.getCanvasX()
        };
        int[] y = new int[] {
          bottomLeft.translation(new Vector(0, 1)).getCanvasY(),
          speechOrigin.getCanvasY(),
          // translate upward to eliminate gap between foot and main background
          // caused by rounding
          third.translation(new Vector(0, 1)).getCanvasY()
        };

        g.fillPolygon(x, y, 3);
      }
    }

    // render text
    applyTo(g);
    for (int i = lines.length - 1; i >= 0; i--) {
      g.drawString(
        lines[i],
        textBottomLeft.getCanvasX(),
        textBottomLeft.getCanvasY()
      );
      bottomLeft = bottomLeft.translation(lineOffset);
    }
  }

  public static TextStyle monospaced() {
    Font font = new Font(Font.MONOSPACED, Font.PLAIN, 16);
    return new TextStyle(font.getFontName(), 16, Color.BLACK);
  }

  public static TextStyle serif() {
    Font font = new Font(Font.SERIF, Font.PLAIN, 16);
    return new TextStyle(font.getFontName(), 16, Color.BLACK);
  }
  
  public static TextStyle sansSerif() {
    Font font = new Font(Font.SANS_SERIF, Font.PLAIN, 16);
    return new TextStyle(font.getFontName(), 16, Color.BLACK);
  }
  
  /* Getters and setters */

  public void setFontName(String fontName) {
    if (fontName == null) {
      throw new IllegalArgumentException("fontName must not be null.");
    }
    this.fontName = fontName;
  }

  public String getFontName() {
    return fontName;
  }

  public void setFontSize(int fontSize) {
    if (fontSize <= 0) {
      throw new IllegalArgumentException("fontSize must be positive");
    }
    this.fontSize = fontSize;
  }

  public int getFontSize() {
    return fontSize;
  }

  public void setColor(Color color) {
    if (color == null) {
      throw new IllegalArgumentException("color must not be null.");
    }
    this.color = color;
  }

  public Color getColor() {
    return color;
  }

  public void setBold(boolean bold) {
    this.bold = bold;
  }

  public boolean isBold() {
    return bold;
  }

  public void setItalic(boolean italic) {
    this.italic = italic;
  }

  public boolean isItalic() {
    return italic;
  }

  // set null for no background
  public void setBackgroundColor(Color background) {
    this.backgroundColor = background;
  }

  public Color getBackgroundColor() {
    return backgroundColor;
  }
}
