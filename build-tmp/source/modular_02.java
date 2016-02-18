import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.Map; 
import themidibus.*; 
import processing.awt.PSurfaceAWT; 
import net.fladdict.oscillator.*; 
import g4p_controls.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class modular_02 extends PApplet {




HashMap<String, ControlChannel> cc;

PImage img ;

MidiBus korg, akai;
int bg = color(0, 255);
int fg = color(0, 1);

float [] zoom = { 1, 0.5f, 0.25f, 0.125f, 0.0625f };

ArrayList <Osc> osc;

PGraphics p;
VideoWall vw, vw2;
Translator tr;
Caleidoscop ca;
public void settings() {
  size(1400, 800, P3D);
}

public void setup() {
  SecondApplet sa = new SecondApplet( this, 0, 0, 800, 600);
  String[] args = {"YourSketchNameHere"};
  PApplet.runSketch(args, sa);
  
  frameRate(15);
  colorMode(RGB, 1, 1, 1, 1);

  vw = new VideoWall(this, 1, height, height);
  vw.setScale(zoom[0]); 
  vw2 = new VideoWall(this, 1, height, height);
  vw2.setScale(zoom[0]); 

  ca = new Caleidoscop(this, 800, 800);

  tr = new Translator(height, height);

  cc = new HashMap<String, ControlChannel>();
  cc.put("1 1", new ControlChannel(1));// cc.put([SCENE CHANNEL])
  cc.put("1 2", new ControlChannel(2));
  cc.put("1 3", new ControlChannel(3));
  cc.put("1 4", new ControlChannel(4));
  cc.put("1 5", new ControlChannel(5));
  cc.put("1 6", new ControlChannel(6));
  cc.put("1 7", new ControlChannel(7));
  cc.put("1 8", new ControlChannel(8));

  osc = new ArrayList<Osc>();
  osc.add(new Osc(this, 20, 20 + (0 * 150), "vertical"));
  osc.add(new Osc(this, 20, 20 + (1 * 150), "horizontal"));
  osc.add(new Osc(this, 20, height - 20 - (2*150), "circle"));
  osc.add(new Osc(this, 20, height - 20 - (1*150), "diagonal"));
  osc.add(new Osc(this, width - 20 - 260, 20 + (0 * 150), "vertical"));
  osc.add(new Osc(this, width - 20 - 260, 20 + (1 * 150), "horizontal"));
  osc.add(new Osc(this, width - 20 - 260, height - 20 - (2*150), "circle2"));
  osc.add(new Osc(this, width - 20 - 260, height - 20 - (1*150), "diagonal"));

  MidiBus.list();
  korg = new MidiBus(this, "SLIDER/KNOB", -1);
  akai = new MidiBus(this, "LPD8", -1);



  p = createGraphics(160, 160, P3D);
}

public void draw() {

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

  mateColor(p, fg);
  p.endDraw();

  img = vw2.get(ca.get(vw.get(p))).copy();
  image(img, vw.ox, vw.oy);
}
class Caleidoscop{

	float alpha = TWO_PI/4.0f;
	PGraphics i;
	int width, height, ox, oy;
	float d;
	Caleidoscop (PApplet self, int w, int h){
		this.width = w;
		this.height = h;
		this.ox = (int) (this.width / 2.0f);
		this.oy = (int) (this.height / 2.0f);
		d = sqrt(sq(ox)+sq(oy));
		i = createGraphics(this.width, this.height, P3D);
	}

	public void setAlpha(int n){
		alpha = TWO_PI/(float)n;
	}
	public void setDiameter(float n){
		d = sqrt(sq(n)+sq(n));
	}

	public PImage get(PImage img) {
		if(alpha >= PI)return img;
		i.beginDraw();
		mateColor(i, bg);
		int c = 0;
		for (float a = 0; a < TWO_PI ; a+=alpha) {
			c++;
			i.pushMatrix();
			i.translate(ox, oy);
			i.rotateZ(alpha/2);
			if (c%2==0) {
			  i.rotateY(PI);
			}
			i.noStroke();
			i.textureMode(NORMAL);
			i.beginShape();
			i.texture(img);
			i.vertex(0, 0, 0, 0);
			i.vertex(d * sin(a-(alpha/2)), d * cos(a-(alpha/2)), 1, 0);
			i.vertex(d * sin(a+(alpha/2)), d * cos(a+(alpha/2)), 0, 1);
			i.endShape(CLOSE);

			i.popMatrix();
			
		}
		i.endDraw();
		return i ;
	}
}
public void controllerChange(int channel, int number, int value) {
  int [] ccValue =  getControlChannel(channel, number, value);

  if (cc.containsKey(ccValue[0]+" "+ccValue[1])) {
    cc.get(ccValue[0]+" "+ccValue[1]).update(ccValue[2], value);
  } else if (ccValue[0] == 5) {
    if (ccValue[1] == 1) {
      bg = ground(bg, ccValue[2], value);
    } else {
      fg = ground(fg, ccValue[2], value);
    }
  }
}

public int [] getControlChannel(int channel, int number, int value) {
  int [] result = new int [3];
  if (channel == 0) { /* KORG */
    if (number>= 120) {
      number -=8;
    } else if (number>= 113) {
      number +=28;
    } else {
      number --;
    }
    result[0] = 1+(number / 4)/7; 	// scene
    result[1] = 1+(number / 4)%7; 	// chann
    result[2] = number % 4;			// buton
  } else if (channel == 1) { /* AKA\u00cf */
    if (number>= 9) {
      number -=8;
      result[0] = 1;
      result[1] = number;
      result[2] = 5;
    } else {
      result[0] = 1;
      result[1] = number;
      result[2] = 4;
    }
  }
  return result;
}

interface ControlListener {
	public void sliderEvent(String s, float v);
	public void knob1Event(String s, float v);
	public void knob2Event(String s, float v);
	public void tap1Event(String s, boolean b);
	public void tap2Event(String s, boolean b);
}

class ControlChannel{

	private ArrayList<ArrayList>listeners = new ArrayList<ArrayList>();

	public void addListener(ControlListener toAdd, String s) {
		ArrayList element = new ArrayList();
		element.add(toAdd);
		element.add(s);
		listeners.add(element);
	}
    public void removeListener(ControlListener toRemove) {
		
		int i = 0 ;
		for (ArrayList al : listeners) {
			if(((ControlListener)(al.get(0))).equals(toRemove)){
				listeners.remove(i);
				return;
			}
			i++;
		}
	}

	float slider;
	float knob1;
	float knob2;
	boolean tap1;
	boolean tap2;
	boolean tap3;
	int chann = 0;
	float ratio_ratio;
	ControlChannel(int chan){
		this.chann = chan;
		ratio_ratio = (float)(zoom.length-1) / 127.0f;
	} 

	public void update(int button, int value){
		switch(button){
			case 2: /* slider */
				changeSlider(value);
				for (ArrayList hl : listeners)
           			((ControlListener)hl.get(0)).sliderEvent((String)hl.get(1), slider);
			break;
			case 3: /* knob1 */
				changeSliderWithPrecesion(value);
				for (ArrayList hl : listeners)
           			((ControlListener)hl.get(0)).knob1Event((String)hl.get(1), slider);
			break;
			case 5: /* knob2 */
				if(chann == 1){
					vw.setScale(zoom[PApplet.parseInt(value * ratio_ratio)]); 
				}else if(chann == 2){
					for (ArrayList hl : listeners)
							((ControlListener)hl.get(0)).knob2Event("inflateX", value / 12.7f);
				}else if(chann == 3){
					for (ArrayList hl : listeners)
							((ControlListener)hl.get(0)).knob2Event("inflateY", value / 12.7f);
				}else if(chann == 4){
					ca.setAlpha(((int)(value / 6.35f))*2); 
				}else if(chann == 5){
					for (ArrayList hl : listeners)
							((ControlListener)hl.get(0)).knob2Event("deltaX", value * 2 );
				}else if(chann == 6){
					for (ArrayList hl : listeners)
							((ControlListener)hl.get(0)).knob2Event("deltaY", value * 2);
				}else if(chann == 7){
					ca.setDiameter(value*20); 
				}else if(chann == 8){
					vw2.setScale(zoom[PApplet.parseInt(value * ratio_ratio)]); 
				}
			break;
			case 0: /* tap1 */
				tap1 = 0 != value;
				if(chann == 1){
					for (ArrayList hl : listeners)
							((ControlListener)hl.get(0)).tap1Event("SINSQUARE", tap1);
				}else if(chann == 2){
				}
			break;
			case 1: /* tap2 */
				tap2 = 0 != value;
				if(chann == 1){
					for (ArrayList hl : listeners)
							((ControlListener)hl.get(0)).tap2Event("TRISAWTOOTH", tap2);
				}
			break;
			case 4: /* tap3 */
				tap3 = 0 != value;
				if(chann > osc.size()) return ;
				println(chann);
				Osc o = osc.get(chann-1);
				if (tap3) {
					o.controlled = true;
					cc.get("1 1").addListener(o, "alpha");
					cc.get("1 2").addListener(o, "red");
					cc.get("1 3").addListener(o, "green");
					cc.get("1 4").addListener(o, "blue");
					cc.get("1 5").addListener(o, "sldr1");
					cc.get("1 6").addListener(o, "sldr2");
				} else {
					o.controlled = false;
					cc.get("1 1").removeListener(o);
					cc.get("1 2").removeListener(o);
					cc.get("1 3").removeListener(o);
					cc.get("1 4").removeListener(o);
					cc.get("1 5").removeListener(o);
					cc.get("1 6").removeListener(o);
				}
			break;
			
		}
	}

	public void changeSliderWithPrecesion(int knobValue){
		if(knob1 == 0)knob1 = knobValue;
		float difference = knob1 -= knobValue;
		slider += difference/1270.0f;
		slider = min(max(slider, 0), 1);
		knob1 = knobValue;
	}
	public void changeSlider(int sliderValue){
		slider = min(max(sliderValue/127.0f, 0), 1);
	}
}


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
  public void settings() {
    size(_w, _h);
  }
  public void setup() {
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



class Osc implements ControlListener{
	GCustomSlider sldr1, sldr2;
	float sldr2Value = 0;
	GCheckbox cbxActive1, cbxActive2;
	GCustomSlider sldrR, sldrG, sldrB, sldrA;
	GCheckbox cbxADD;
	Oscillator osc;
	int X, Y ;
	String type ;
	PApplet self;
	boolean controlled = false;

	float inflateX = 0;
	float inflateY = 0;
	
	float deltaX = 0;
	float deltaY = 0;

	Osc(PApplet self, int x, int y, String type) {
		this.self = self;
		X = x;
		Y = y;
		this.type = type;

		cbxActive1 = new GCheckbox(self, x, y, 100, 20, "");
		cbxActive1.setSelected(true);

		cbxActive2 = new GCheckbox(self, x, y+20, 100, 20, "" );

		cbxADD = new GCheckbox(self, x, y+50, 100, 20, "" );

		sldr1 = new GCustomSlider(self, x + 20, y, 240, 10);
		sldr1.setEasing(12.5f);

		sldr2 = new GCustomSlider(self, x+20, y+20, 240, 10);
		sldr2.setEasing(12.5f);

		sldrR = new GCustomSlider(self, x+20, y+40, 240, 10);
		sldrR.setEasing(12.5f);
		sldrR.setValue(0);

		sldrG = new GCustomSlider(self, x+20, y+60, 240, 10);
		sldrG.setEasing(12.5f);
		sldrG.setValue(0);

		sldrB = new GCustomSlider(self, x+20, y+80, 240, 10);
		sldrB.setEasing(12.5f);
		sldrB.setValue(0);

		sldrA = new GCustomSlider(self, x+20, y+100, 240, 10);
		sldrA.setEasing(12.5f);
		sldrA.setValue(0);

		osc = new OscSin(0.5f, 1000.0f, 0.5f);
	}

	public void setWave(String type){
		float period = osc.getPeriod();
		if(type == "SIN"){
			osc = new OscSin(0.5f, period, 0.5f);
		}
		else if(type == "SAW_TOOTH"){
			osc = new OscSawTooth(0.5f, period, 0.5f);
		}
		else if(type == "TRIANGLE"){
			osc = new OscTriangle(0.5f, period, 0.5f);
		}
		else if(type == "SQUARE"){
			osc = new OscSquare(0.5f, period, 0.5f);
		}
	}

	public int getBlend() {
		return cbxADD.isSelected() ? SUBTRACT : ADD;
	}

	public float getPeriod() {
		return 100001.0f*pow(sldr1.getValueF(), 10);
	}

	public float getValue() {
		return pow(osc.getValue(), sldr2Value);
	}

	public void update() {
		osc.update();
	}

	public void drawPreview(PImage i) {
		image(i, X+20, Y-10, 240, 130);
		if(controlled){
			stroke(1);
			noFill();
			rect(X+20, Y-10, 240, 130);
			noStroke();
		}
	}
	public void getControlValues() {
		sldr2Value = pow(sldr2.getValueF(), 1.0f/sldr2.getValueF()) * 50;
	}

	public PImage render() {
		if (type == "horizontal") {
			return this.horizontalRender();
		} else if (type == "vertical") {
			return this.verticalRender();
		} else if (type == "circle") {
			return this.circleRender();
		} else if (type == "circle2") {
			return this.circle2Render();
		} else {
			return this.diagonalRender();
		}
	}
	public int getColor(float V){
		return 	((int)(sldrA.getValueF()*255)) << 24 | 
				((int)(sldrR.getValueF()*V*255)) << 16 | 
				((int)(sldrG.getValueF()*V*255)) << 8 | 
				((int)(sldrB.getValueF()*V*255));
	}

	public PImage verticalRender () {
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();
			float x_norm_inc = inflateX / (float)i.width;
			float y_norm_inc = inflateY / (float)i.height;
			PVector inflate_vector = new PVector(0, 0);
			for (int x = 0; x < i.width; x ++) {
				this.update();
				for (int y = 0; y < i.height; y ++) {
					float inflate = inflateY == 0 && inflateX == 0 ? 1 : inflate_vector.magSq()/2f;
					float V  = invert ? 1 - this.getValue() : this.getValue();
					int X  = x + (int)deltaX; 
					int Y = y + (int)deltaY; 
					if (X >= i.width || X < 0) X %= i.width;
					if (Y >= i.height || Y < 0) Y %= i.height;
					int N = min(max(X + (Y * i.width), 0), i.pixels.length-1);
					i.pixels[N] = getColor(inflate * V);
					inflate_vector.y+= y_norm_inc;
				}
				inflate_vector.x+= x_norm_inc;
				inflate_vector.y = 0;
			}
			i.updatePixels();
		}
		drawPreview(i.copy());
		return i ;
	}

	public PImage horizontalRender () {
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();
			float x_norm_inc = inflateX / (float)i.width;
			float y_norm_inc = inflateY / (float)i.height;
			PVector inflate_vector = new PVector(0, 0);
			for (int y = 0; y < i.height; y ++) {
				this.update();
				for (int x = 0; x < i.width; x ++) {
					float inflate = inflateY == 0 && inflateX == 0 ? 1 : inflate_vector.magSq()/2f;
					float V  = invert ? 1 - this.getValue(): this.getValue();
					int X  = x + (int)deltaX; 
					int Y = y + (int)deltaY; 
					if (X >= i.width || X < 0) X %= i.width;
					if (Y >= i.height || Y < 0) Y %= i.height;
					int N = min(max(X + (Y * i.width), 0), i.pixels.length-1);
					i.pixels[N] = getColor(inflate * V);
					inflate_vector.x+= x_norm_inc;
				}
				inflate_vector.y+= y_norm_inc;
				inflate_vector.x= 0;
			}
			i.updatePixels();
		}
		drawPreview(i.copy());
		return i ;
	}

	public PImage circleRender () {
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();
			float x_norm_inc = inflateX / (float)i.width;
			float y_norm_inc = inflateY / (float)i.height;
			PVector inflate_vector = new PVector(0, 0);
			for (int y = 0; y < i.height; y ++) {
				this.update();
				float G = this.getValue();
				PVector o = new PVector(G, G);
				for (int x = 0; x < i.width; x ++) {
					float inflate = inflateY == 0 && inflateX == 0 ? 1 : inflate_vector.magSq()/2f;
					PVector v = new PVector((float)x/(float)i.width, (float)y/(float)i.height);
					v.sub(o);
					float V = G * sin((G + v.magSq())* TWO_PI);
					V = min(max(V, 0), 1);
					V = invert ? 1- V : V ;
					int X  = x + (int)deltaX; 
					int Y = y + (int)deltaY; 
					if (X >= i.width || X < 0) X %= i.width;
					if (Y >= i.height || Y < 0) Y %= i.height;
					int N = min(max(X + (Y * i.width), 0), i.pixels.length-1);
					i.pixels[N] = getColor(inflate * V);
					inflate_vector.x+= x_norm_inc;
				}
				inflate_vector.y+= y_norm_inc;
				inflate_vector.x= 0;
			}
		}
		i.updatePixels();
		drawPreview(i.copy());
		return i ;
	}

	public PImage circle2Render () {
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();
			float x_norm_inc = inflateX / (float)i.width;
			float y_norm_inc = inflateY / (float)i.height;
			PVector inflate_vector = new PVector(0, 0);
			for (int x = 0; x < i.width; x ++) {
				this.update();
				float G = this.getValue();
				PVector o = new PVector(G, G);
				for (int y = 0; y < i.height; y ++) {
					float inflate = inflateY == 0 && inflateX == 0 ? 1 : inflate_vector.magSq()/2f;
					PVector v = new PVector((float)x/(float)i.width, (float)y/(float)i.height);
					v.sub(o);
					float V = G * sin((G + v.magSq())* TWO_PI);
					V = min(max(V, 0), 1);
					V = invert ? 1- V : V ;
					int X  = x + (int)deltaX; 
					int Y = y + (int)deltaY; 
					if (X >= i.width || X < 0) X %= i.width;
					if (Y >= i.height || Y < 0) Y %= i.height;
					int N = min(max(X + (Y * i.width), 0), i.pixels.length-1);
					i.pixels[N] = getColor(inflate * V);
					inflate_vector.y+= y_norm_inc;
				}
				inflate_vector.x+= x_norm_inc;
				inflate_vector.y= 0;
			}
		}
		i.updatePixels();
		drawPreview(i.copy());
		return i ;
	}

	float offset = 0;
	public PImage diagonalRender () {
		offset = 2 * (0.5f-((float)mouseX /(float)width));
		//println(offset);
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();
			for (int x = 0; x < i.width; x ++) {
				this.update();
				float o = offset;  
				for (int y = 0; y < i.height; y ++) {
					o += offset;
					float V  = invert ? 1 - this.getValue(): this.getValue();
					int n = x + y * i.width;
					n += o;
					if (n >= i.pixels.length-1)n -= i.pixels.length;
					if (n < 0)n += i.pixels.length;
					n = max(0, min(i.pixels.length-1, n));
					i.pixels[n] = getColor(V);
				}
			}
			i.updatePixels();
		}
		drawPreview(i.copy());
		return i ;
	}


	public PImage exponantRender () {
		offset = 2 * (0.5f-((float)mouseX /(float)width));
		println(offset);
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();

			for (int x = 0; x < i.width; x ++) {
				this.update();
				float o = offset;  
				for (int y = 0; y < i.height; y ++) {
					o += offset;
					float V  = invert ? 1 - this.getValue(): this.getValue();
					int n = x + y * i.width;
					n += sq(o);
					if (n >= i.pixels.length-1)n -= i.pixels.length;
					if (n < 0)n += i.pixels.length;
					n = max(0, min(i.pixels.length-1, n));
					i.pixels[n] = getColor(V);
				}
			}
			i.updatePixels();
		}
		drawPreview(i.copy());
		return i ;
	}

	public void sliderEvent(String name, float value){
		if(name == "alpha")
			sldrA.setValue(value);
		else if(name == "red")
			sldrR.setValue(value);
		else if(name == "green")
			sldrG.setValue(value);
		else if(name == "blue")
			sldrB.setValue(value);
		else if(name == "sldr1")
			sldr1.setValue(value);
		else if(name == "sldr2")
			sldr2.setValue(value);
	}
	public void knob1Event(String name, float value){
		if(name == "sldr1")
			sldr1.setValue(value);
		else if(name == "sldr2")
			sldr2.setValue(value);
	}
	public void knob2Event(String name,float value){
		if(name == "inflateX"){
			this.inflateX = value;
		}else if(name == "inflateY"){
			this.inflateY = value;
		}else if(name == "deltaX"){
			this.deltaX = value;
		}else if(name == "deltaY"){
			this.deltaY = value;
		}
	}
	public void tap1Event(String name, boolean state){
		if(name == "SINSQUARE"){
			this.setWave(state ? "SQUARE" : "SIN");
		}
		
	}
	public void tap2Event(String name, boolean state){
		if(name == "TRISAWTOOTH"){
			this.setWave(state ? "TRIANGLE" : "SAW_TOOTH");
		}
	}
}
class Translator {

  int width, height;
  PImage translator;

  Translator(int _width, int _height) {
    this.width = _width;
    this.height = _height;
    translator = new PImage(this.width, this.height);
  }

  public void setTranslator(PImage tr) {
    this.translator = tr;
  }

  public PImage get(PImage img) {
    PImage result = new PImage(img.width, img.height);

    result.loadPixels();
    img.loadPixels();
    this.translator.loadPixels();

    int trLen = this.translator.pixels.length;
    int imgLen = img.pixels.length;
    float ratio = (float)trLen / (float)imgLen;
    float offset = 0 ;
    int i = 0;
    for (int x = 0; x < img.width; x ++) {
      for (int y = 0; y < img.height; y ++) {
        int t = PApplet.parseInt(i++ * ratio);
        byte inc = PApplet.parseByte(this.translator.pixels[t] & 0xFF); // blue
        byte dec = PApplet.parseByte(this.translator.pixels[t] >> 8 & 0xFF); // green
        offset += inc/2560.0f;
        offset -= dec/2560.0f;
        int c = PApplet.parseInt(i + offset);
        if (c >= imgLen-1)c -= imgLen;
        if (c < 0)c += imgLen;
        c = max(0, min(imgLen-1, c));
        i = max(0, min(imgLen-1, i));
        result.pixels[i] = img.pixels[c];
      }
    }
    result.updatePixels();
    img.updatePixels();
    this.translator.updatePixels();
    return result;
  }
}

public void mateColor(PGraphics p, int c){
	p.blendMode(BLEND);
	p.fill(c);
	p.rect(0, 0, p.width, p.height);
	p.noFill();
}
public int ground(int c, int button, int value){
	int white = 0, alpha = 0;
	if (button == 3) {
		white = max(min(255, value*2), 0);
		alpha = max(min(255, c >> 24 & 0xFF), 0);
	} else if (button == 2) {
		white = max(min(255, c >> 16 & 0xFF), 0);
		alpha = max(min(255, 255 - (value*2)), 0);
	}
	return  alpha << 24 | white << 16 | white << 8 | white;
}
class VideoWall {
  float scale ;
  float l ;
  float h ;
  float j, k;
  PGraphics i;
  int width, height, ox, oy;
  VideoWall(PApplet self, float scale) {
    setScale(scale);
    this.width = self.width;
    this.height = self.height;
    this.ox = 0 ;
    this.oy = 0 ;
    i = createGraphics(this.width, this.height, P3D);
  }
  VideoWall(PApplet self, float scale, int _width, int _height) {
    
    this.width = _width;
    this.height = _height;
    this.ox = self.width /2 - _width /2 ;
    this.oy = self.height /2 - _height /2 ;
    
    setScale(scale);
    i = createGraphics(this.width, this.height, P3D);
  }
  public void setScale(float scale) {
    this.scale = scale;
    this.l = this.width * this.scale;
    this.h = this.height * this.scale;
  }
  public PImage get(PImage img) {
    i.beginDraw();
    for (int x = 0; x < 1.0f/scale; x ++) {
      for (int y = 0; y < 1.0f/scale; y ++) {
        i.pushMatrix();
        i.translate(l*(x+0)+l/2, h*(y+0)+h/2);
        if (x%2==0) {
          i.rotateY(PI);
        }
        if (y%2==0) {
          i.rotateX(PI);
        }
        i.translate(-(l*(x+0)+l/2), -(h*(y+0)+h/2));
        //translate(img.width * noise(j+= 0.001), img.height * noise(k+= 0.00111));
        i.noStroke();
        i.beginShape();
        i.texture(img);
        i.vertex(l*(x+0), h*(y+0), img.width, img.height);
        i.vertex(l*(x+1), h*(y+0), 0, img.height);
        i.vertex(l*(x+1), h*(y+1), 0, 0);
        i.vertex(l*(x+0), h*(y+1), img.width, 0);
        i.endShape();

        i.popMatrix();
      }
    }
    i.endDraw();
    return i ;
  }
  public void display(PImage img) {
    translate(this.ox, this.oy);
    for (int x = 0; x < 1.0f/scale; x ++) {
      for (int y = 0; y < 1.0f/scale; y ++) {
        pushMatrix();
        translate(l*(x+0)+l/2, h*(y+0)+h/2);
        if (x%2==0) {
          rotateY(PI);
        }
        if (y%2==0) {
          rotateX(PI);
        }
        translate(-(l*(x+0)+l/2), -(h*(y+0)+h/2));
        //translate(img.width * noise(i+= 0.001), img.height * noise(j+= 0.001));
        noStroke();
        beginShape();
        texture(img);
        vertex(l*(x+0), h*(y+0), img.width, img.height);
        vertex(l*(x+1), h*(y+0), 0, img.height);
        vertex(l*(x+1), h*(y+1), 0, 0);
        vertex(l*(x+0), h*(y+1), img.width, 0);
        endShape();

        popMatrix();
      }
    }
  }
}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "modular_02" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
