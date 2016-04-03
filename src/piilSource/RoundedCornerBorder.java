/*  
    PiiL: Pathway Interactive vIsualization tooL
    Copyright (C) 2015  Behrooz Torabi Moghadam

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package piilSource;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;


public class RoundedCornerBorder extends AbstractBorder {
	  @Override public void paintBorder(
	      Component c, Graphics g, int x, int y, int width, int height) {
	    Graphics2D g2 = (Graphics2D)g.create();
	    g2.setColor(Color.RED);
	    g2.setRenderingHint(
	        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    int r = 12;
	    int w = width  - 1;
	    int h = height - 1;
	    Area round = new Area(new RoundRectangle2D.Float(x, y, w, h, r, r));
	    Container parent = c.getParent();
	    if(parent!=null) {
	      g2.setColor(parent.getBackground());
	      Area corner = new Area(new Rectangle2D.Float(x, y, width, height));
	      corner.subtract(round);
	      g2.fill(corner);
	    }
	    g2.setPaint(Color.GRAY);
//	    g2.setPaint(c.getForeground());
	    g2.draw(round);
	    g2.dispose();
	  }
	  @Override public Insets getBorderInsets(Component c) {
//	    return new Insets(4, 8, 4, 8);
	    return new Insets(0, 0, 0, 0);
	  }
	  @Override public Insets getBorderInsets(Component c, Insets insets) {
	    insets.left = insets.right = 0;
	    insets.top = insets.bottom = 0;
	    return insets;
	  }
	}

	class KamabokoBorder extends RoundedCornerBorder {
	  @Override public void paintBorder(
	      Component c, Graphics g, int x, int y, int width, int height) {
	    Graphics2D g2 = (Graphics2D)g.create();
	    g2.setColor(Color.GRAY);
	    g2.setRenderingHint(
	        RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	    int r = 12;
	    int w = width  - 1;
	    int h = height - 1;
	//*/
	    Path2D.Float p = new Path2D.Float();
	    p.moveTo(x, y + h);
	    p.lineTo(x, y + r);
	    p.quadTo(x, y, x + r, y);
	    p.lineTo(x + w - r, y);
	    p.quadTo(x + w, y, x + w, y + r);
	    p.lineTo(x + w, y + h);
	    p.closePath();
	    Area round = new Area(p);
	/*/
	    Area round = new Area(new RoundRectangle2D.Float(x, y, w, h, r, r));
	    Rectangle b = round.getBounds();
	    b.setBounds(b.x, b.y + r, b.width, b.height - r);
	    round.add(new Area(b));
	//*/
	    Container parent = c.getParent();
	    if(parent!=null) {
	      g2.setColor(parent.getBackground());
	      Area corner = new Area(new Rectangle2D.Float(x, y, width, height));
	      corner.subtract(round);
	      g2.fill(corner);
	    }
	    g2.setPaint(c.getForeground());
	    g2.draw(round);
	    g2.dispose();
	  }
	}
