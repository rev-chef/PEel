package graphics;

import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.*;


public class BananaSections {
	
	RoundRectangle2D.Double main;
	Ellipse2D.Double shade;
	Shape hitShape;
	String name;
	
	public BananaSections(String name) {
		this.name = name;
		this.main = new RoundRectangle2D.Double();
		this.shade = new Ellipse2D.Double();
	}
	
	public boolean hasMouse(Point p) {
		
		return main.contains(p); 
		
	}
	
	 public void updateHitbox(AffineTransform transform) {
	        this.hitShape = transform.createTransformedShape(main);
	    }

	public RoundRectangle2D.Double getMain() {
		return main;
	}

	public Ellipse2D.Double getShade() {
		return shade;
	}

	public Shape getHitShape() {
		return hitShape;
	}

	public String getName() {
		return name;
	}

	public void setMain(RoundRectangle2D.Double main) {
		this.main = main;
	}

	public void setShade(Ellipse2D.Double shade) {
		this.shade = shade;
	}

	public void setHitShape(Shape hitShape) {
		this.hitShape = hitShape;
	}

	public void setName(String name) {
		this.name = name;
	}
	 
}
