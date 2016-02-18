
import java.util.Map;
import themidibus.*;

HashMap<String, ControlChannel> cc;

PImage img ;

MidiBus korg, akai;
color bg = color(0, 255);
color fg = color(0, 1);

float [] zoom = new float [5];

ArrayList <Osc> osc;

PGraphics p;
VideoWall vw;
Translator tr;


void settings() {
  size(1400, 800, P3D);
}

void setup() {
  SecondApplet sa = new SecondApplet( this, 0, 0, 800, 600);
  String[] args = {"YourSketchNameHere"};
  PApplet.runSketch(args, sa);
  
  frameRate(15);
  colorMode(RGB, 1, 1, 1, 1);

  zoom[0] = 1;
  for (int i = 1; i < zoom.length; i ++) {
    zoom[i] = zoom[i-1] /2.0f;
  }

  vw = new VideoWall(this, 1, height, height);
  vw.setScale(zoom[1]); 

  tr = new Translator(height, height);

  cc = new HashMap<String, ControlChannel>();
  cc.put("1 1", new ControlChannel(1));// cc.put([SCENE CHANNEL])
  cc.put("1 2", new ControlChannel(2));
  cc.put("1 3", new ControlChannel(3));
  cc.put("1 4", new ControlChannel(4));
  cc.put("1 5", new ControlChannel(5));
  cc.put("1 6", new ControlChannel(6));
  cc.put("1 7", new ControlChannel(7));

  osc = new ArrayList<Osc>();
  osc.add(new Osc(this, 20, 20 + (0 * 150), "vertical"));
  osc.add(new Osc(this, 20, 20 + (1 * 150), "horizontal"));
  osc.add(new Osc(this, 20, height - 20 - (2*150), "circle"));
  osc.add(new Osc(this, 20, height - 20 - (1*150), "diagonal"));
  osc.add(new Osc(this, width - 20 - 260, 20 + (0 * 150), "vertical"));
  osc.add(new Osc(this, width - 20 - 260, 20 + (1 * 150), "horizontal"));
  osc.add(new Osc(this, width - 20 - 260, height - 20 - (2*150), "circle"));
  osc.add(new Osc(this, width - 20 - 260, height - 20 - (1*150), "diagonal"));

  MidiBus.list();
  korg = new MidiBus(this, "SLIDER/KNOB", -1);
  akai = new MidiBus(this, "LPD8", -1);



  p = createGraphics(160, 160, P3D);
}

void draw() {

  if (frameCount %100==0)println("frameRate : " + frameRate);
  background(0);
  p.beginDraw();
  p.noStroke();
  p.colorMode(RGB, 1, 1, 1, 1);

  mateColor(p, bg);

  for (int i = 0; i < osc.size(); i ++) {
    Osc o = osc.get(i);
    p.blendMode(o.getBlend());
    p.tint(1, 1, 1, o.sldrA.getValueF());
    p.image(o.render(), 0, 0);
    p.noTint();
  }

  //tr.setTranslator(osc.get( osc.size()-1).render());
  //p.blendMode(osc.get( osc.size()-2).getBlend());
  //p.image(tr.get(osc.get( osc.size()-2).render()), 0, 0);

  mateColor(p, fg);
  p.endDraw();
  img = vw.get(p).copy();
  image(img, vw.ox, vw.oy);
}