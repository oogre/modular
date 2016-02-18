import processing.awt.PSurfaceAWT;

public class SecondApplet extends PApplet {
  int _w, _h, _x, _y;
  int px = 0;
  PApplet _self;
  SecondApplet (PApplet self, int locX, int locY, int w, int h){
  _w = w;
  _h = h;
  _x = locX;
  _y = locY;
  px = _w/2 - _h / 2;
  _self = self;
  } 
  void settings() {
    size(_w, _h);
  }
  void setup() {
    PSurfaceAWT awtSurface = (PSurfaceAWT)surface;
    PSurfaceAWT.SmoothCanvas smoothCanvas = (PSurfaceAWT.SmoothCanvas)awtSurface.getNative();
    smoothCanvas.getFrame().setAlwaysOnTop(false);
    smoothCanvas.getFrame().removeNotify();
    smoothCanvas.getFrame().setUndecorated(true);
    smoothCanvas.getFrame().setLocation(_x, _y);
    smoothCanvas.getFrame().addNotify();
    smoothCanvas.getFrame().setSize(width, height);
  }
  public void draw() {
    background(0);
    if(null != img){
      image(img, px, 0, height, height);
    }
  }
}