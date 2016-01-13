package piilSource;

import java.awt.geom.Path2D;

public class ArrowEdge {

public static Path2D.Double createArrow(double fromX, double fromY, double toX, double toY) {
        
        Path2D.Double path = new Path2D.Double();
        double startX, startY, endX, endY;
        startX = fromX;
        startY = fromY;
        endX = toX;
        endY= toY;
        
        double x = endX - startX ;
        double y = endY - startY ;
        
        double phi = Math.acos(x/Math.sqrt(x*x+y*y));
        double r = Math.sqrt(x*x+y*y) ;
        
        double arrowWings = 3;
        double size_back = 5;	

        double x1 = r - size_back;
        double y1 = arrowWings;
        double x2 = r - size_back;
        double y2 = - arrowWings;
        
        if (y < 0) { phi = -phi;}
        
        double rx1 = startX + x1 * Math.cos(phi) - y1 * Math.sin(phi);
        double ry1 = startY + x1 * Math.sin(phi) + y1 * Math.cos(phi);
        double rx2 = startX + x2 * Math.cos(phi) - y2 * Math.sin(phi);
        double ry2 = startY + x2 * Math.sin(phi) + y2 * Math.cos(phi);
        
        path.moveTo(fromX, fromY);
        path.lineTo(toX, toY);

        path.lineTo(rx1, ry1);
        path.moveTo(toX, toY);
        path.lineTo(rx2, ry2);

        return path;
    }
	
}