package graphics;

import javax.swing.JPanel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.*;
import java.util.ArrayList;
public class PaintGraphics extends JPanel{

	private static final long serialVersionUID = 1L;
	//Attributes for shape scaling
	private int baseX = 85;
	private int baseY = 180;
	private int baseWidth = 95;
	private int baseHeight = 50;
	
	private Shape bananaTip = new Arc2D.Double(100, 100, 100, 120, 8, 180, Arc2D.PIE); //Not using base Y, top section is unique in size and position
	private Shape bananaTipShade = new Ellipse2D.Double(82, 163, 100, 10); //VERY SPECIFIC BRUTE-FORCED HARD-CODED POSITIONAL VALUES, DO NOOT F**KING TOUCH
	
	//10 degree angle tilt to match banana top angle, must be negative because Java idfk
	double angle = Math.toRadians(-10);
	
	//Lists to hold section objects
	private ArrayList<BananaSections> sectionMain = new ArrayList<>();
	private ArrayList <BananaSections> sectionShade = new ArrayList<>();
	
	//All colours
	private Color bananaMain = new Color(0xfbec5d);
	private Color bananaShade = new Color(0xc4b208);
	private Color bananaHighlight = new Color (0xfff591);
	
	private BananaListener listener;
	public void setSelectionListener(BananaListener listener)
    {
        this.listener = listener;
    }
	
	public PaintGraphics() {
		loadShapesToList();
		addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseClicked(MouseEvent e) {
		        Point p = e.getPoint();
		        
		        if (bananaTip.contains(p)) {
            	    listener.sectionSelected("Tip");
            	    return;
            	}
		        
		        for (BananaSections m : sectionMain) {
		            if (m.hasMouse(p)) {
		            	
		            	if(listener != null)
		            	{
		            	    listener.sectionSelected(m.name);
		            	}
		            	
		            }
		        }
		    }
		});
		
		addMouseMotionListener(new MouseAdapter() {
		    @Override
		    public void mouseMoved(MouseEvent e) {
		        Point p = e.getPoint();

		        boolean hovering = bananaTip.contains(p);
		        
		        if(!hovering)
		        {
			        for (BananaSections m : sectionMain) {
			            if (m.hasMouse(p)) {
			                hovering = true;
			                break;
			            }
			        }
		        }
		        
		        if (hovering) {
		            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		        } else {
		            setCursor(Cursor.getDefaultCursor());
		        }
		    }
		});
		repaint();	
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D paintBrush = (Graphics2D) g;
		
		DrawBananaSections(paintBrush);
		
	}
	
	public void DrawBananaSections(Graphics2D paintBrush) {
		//Loop to draw section pieces
			//This was like 2% AI and 98% tears
		int currentY = baseY;
		paintBrush.setStroke(new BasicStroke(3, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
		
		
		paintBrush.drawArc(100, 100, 100, 120, 8, 180); //Outline
		paintBrush.setColor(bananaMain);
		
		paintBrush.fill(bananaTip);
		drawTextInShape(bananaTip, paintBrush, "Headers");

		paintBrush.rotate(angle, 100, 60);//Rotate around center of ellipse (x + w/2, y + h/2), stinky math
		AffineTransform old = paintBrush.getTransform();
		
		paintBrush.drawOval(82, 163, 100, 10); //Draw outline first
		paintBrush.setColor(bananaShade);
		paintBrush.fill(bananaTipShade);
			for(int i = 1; i < sectionMain.size(); i++) //Must start at index 1 to account for tip
			{
				String sectionName = switch(i) {
				case 1 -> "Imports / Exports";
				case 2 -> "Strings";
				case 3 -> "Section Headers";
				default -> "TBD";
				};
				
				BananaSections m = sectionMain.get(i);
				BananaSections s = sectionShade.get(i);
				
				QuadCurve2D curve = new QuadCurve2D.Double();
				int yBottom = currentY + baseHeight;
				int curveX = baseX + 20; //Places initial fiber curve inside banana section

				
				m.main.setFrame(baseX, currentY, baseWidth, baseHeight);
				s.shade.setFrame(baseX, currentY-2, baseWidth, 10);
				
				
				paintBrush.setColor(Color.BLACK);
				paintBrush.drawRoundRect(baseX, currentY, baseWidth, baseHeight, 5, 5);
				paintBrush.setColor(bananaMain);
				paintBrush.fill(m.main);
				
				//Absolutely cursed demon spaghetti code to draw stupid ass fiber lines
				for(int rcx = 0; rcx<3; rcx++)
				{
					
					
					double bendOffset = (rcx == 0) ? curveX-5 : curveX+5; //black magic ternary BS, the 2% AI
					
					//I cant believe this works
					curve.setCurve(curveX, currentY-3, bendOffset,(currentY+yBottom) /2, curveX, yBottom-3); //wtf is this 
					//(baseY and yBottom are -3 to account for black outline)
						
					paintBrush.setColor(bananaHighlight);
					paintBrush.draw(curve);
					curveX = curveX + 30; //move the next fiber line to the right
					drawTextInShape(m.getMain(), paintBrush, sectionName);
				}
					
				paintBrush.setColor(Color.BLACK);
				paintBrush.drawOval(baseX, currentY-2, baseWidth, 10);
				paintBrush.setColor(bananaShade);
				paintBrush.fill(s.shade);
					
				int seedXOffset = baseWidth / 2;
				int seedYOffset = currentY + 3;
				for(int d = 0;d<=3;d++)
				{
						
					paintBrush.setColor(Color.BLACK);
					paintBrush.fillOval(baseX+seedXOffset, seedYOffset, 3, 3);
					switch(d)
					{
						case 0:
							seedYOffset -= 4;
							break;
						case 1:
							seedXOffset -= 6;
							seedYOffset += 2;
							break;
						case 2:
							seedXOffset += 12;
							
					}
				}
					
				currentY = currentY + 60;

				angle = Math.toRadians(angle + 3); //Idk how this math is working, but don't fuck with it
				
				paintBrush.rotate(angle, (baseX + baseWidth) / 2, (currentY + baseHeight) / 2);
				
					
				}
			angle = Math.toRadians(-10); //reset angle
			paintBrush.setTransform(old);
	}
	/**
	 * 
	 * @param Shape to draw text in the centre of
	 * @param paintBrush (Graphics2D Object)
	 * @param Text to draw
	 */
	private void drawTextInShape(Shape shape, Graphics2D paintBrush, String text) {
		AffineTransform savedTransform = paintBrush.getTransform();
		boolean isArc2d = (shape instanceof Arc2D);
		int shapeX = (int)shape.getBounds().getCenterX(); //Graphics2D .drawString method only accepts integers :(
		int shapeY = (int)shape.getBounds().getCenterY();
		
		paintBrush.setFont(new Font(Font.DIALOG, Font.BOLD, 12));
		FontMetrics fontSize = paintBrush.getFontMetrics();
		
		int textWidth = fontSize.stringWidth(text);
		int textHeight = fontSize.getAscent();
		int x = shapeX - textWidth / 2;
		int y = isArc2d ? ((shapeY - textHeight / 2) - 20) : (shapeY - textHeight / 2 + 10) ;
		
		paintBrush.setColor(Color.BLACK);
		paintBrush.drawString(text, x, y);
		paintBrush.setTransform(savedTransform);
	}
	
	public void loadShapesToList() {
		sectionMain.add(new BananaSections("Tip"));
		sectionMain.add(new BananaSections("Top"));
		sectionMain.add(new BananaSections("MidTop"));
		sectionMain.add(new BananaSections("Mid"));
		sectionMain.add(new BananaSections("MidBot"));
		sectionMain.add(new BananaSections("Bot"));
		
		sectionShade.add(new BananaSections("ShadeTip"));
		sectionShade.add(new BananaSections("ShadeTop"));
		sectionShade.add(new BananaSections("ShadeMidTop"));
		sectionShade.add(new BananaSections("ShadeMid"));
		sectionShade.add(new BananaSections("ShadeMidBot"));
		sectionShade.add(new BananaSections("ShadeBot"));
	}
}