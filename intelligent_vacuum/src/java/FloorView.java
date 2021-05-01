import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import jason.environment.grid.GridWorldView;

class FloorView extends GridWorldView {

	private static final long serialVersionUID = 1L;

	public FloorView(final FloorModel model) {
        super(model, "Floor", 600);
        defaultFont = new Font("Comic Sans MS", Font.PLAIN, 24); // change default font
        setVisible(true);
        repaint();
	}
	
	public void addClickListener(final Floor building) {
        
		getCanvas().addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int col = e.getX() / cellSizeW;
				int lin = e.getY() / cellSizeH;
				if (col >= 0 && lin >= 0 && col < getModel().getWidth() && lin < getModel().getHeight()) {
					building.addGarbage(col, lin);
					update(col, lin);
					System.out.println("Added garbage: " + col + lin);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		});
	}
	
    /** draw application objects */
    @Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
            case FloorModel.Dirt: drawGarb(g, x, y);  break;
            case FloorModel.Base: drawBase(g, x, y); break;
        }
        //repaint();
    }

	@Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        String label;
        switch(id) {
        case 0: label = "B"; break;
        case 1: label = "A"; break;
        default: label = "C"; break;
        }
        c = Color.blue;

        super.drawAgent(g, x, y, c, -1);
        g.setColor(Color.white);

        super.drawString(g, x, y, defaultFont, label);
        //repaint();
    }
	
    private void drawBase(Graphics g, int x, int y) {
    	g.setColor(Color.black);
    	g.fillRect(cellSizeW * x, cellSizeH * y, cellSizeW, cellSizeH);
    	g.setColor(Color.white);
        drawString(g, x, y, defaultFont, "Base");
	}

    public void drawGarb(Graphics g, int x, int y) {
    	g.setColor(new Color(102,51,0));
    	g.fillRect(cellSizeW * x, cellSizeH * y, cellSizeW, cellSizeH);
        g.setColor(Color.white);
        drawString(g, x, y, defaultFont, "Dirt");
    }

} 