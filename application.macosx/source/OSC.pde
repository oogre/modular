import net.fladdict.oscillator.*;
import g4p_controls.*;

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
		sldr1.setEasing(12.5);

		sldr2 = new GCustomSlider(self, x+20, y+20, 240, 10);
		sldr2.setEasing(12.5);

		sldrR = new GCustomSlider(self, x+20, y+40, 240, 10);
		sldrR.setEasing(12.5);
		sldrR.setValue(0);

		sldrG = new GCustomSlider(self, x+20, y+60, 240, 10);
		sldrG.setEasing(12.5);
		sldrG.setValue(0);

		sldrB = new GCustomSlider(self, x+20, y+80, 240, 10);
		sldrB.setEasing(12.5);
		sldrB.setValue(0);

		sldrA = new GCustomSlider(self, x+20, y+100, 240, 10);
		sldrA.setEasing(12.5);
		sldrA.setValue(0);

		osc = new OscSin(0.5, 1000.0, 0.5);
	}

	void setWave(String type){
		float period = osc.getPeriod();
		if(type == "SIN"){
			osc = new OscSin(0.5, period, 0.5);
		}
		else if(type == "SAW_TOOTH"){
			osc = new OscSawTooth(0.5, period, 0.5);
		}
		else if(type == "TRIANGLE"){
			osc = new OscTriangle(0.5, period, 0.5);
		}
		else if(type == "SQUARE"){
			osc = new OscSquare(0.5, period, 0.5);
		}
	}

	int getBlend() {
		return cbxADD.isSelected() ? SUBTRACT : ADD;
	}

	float getPeriod() {
		return 100001.0*pow(sldr1.getValueF(), 10);
	}

	float getValue() {
		return pow(osc.getValue(), sldr2Value);
	}

	void update() {
		osc.update();
	}

	void drawPreview(PImage i) {
		image(i, X+20, Y-10, 240, 130);
		if(controlled){
			stroke(1);
			noFill();
			rect(X+20, Y-10, 240, 130);
			noStroke();
		}
	}
	void getControlValues() {
		sldr2Value = pow(sldr2.getValueF(), 1.0/sldr2.getValueF()) * 50;
	}

	PImage render() {
		if (type == "horizontal") {
			return this.horizontalRender();
		} else if (type == "vertical") {
			return this.verticalRender();
		} else if (type == "circle") {
			return this.circleRender();
		} else if (type == "cyclic") {
			return this.cyclicRender();
		} else {
			return this.diagonalRender();
		}
	}
	int getColor(float V){
		return 	((int)(sldrA.getValueF()*255)) << 24 | 
				((int)(sldrR.getValueF()*V*255)) << 16 | 
				((int)(sldrG.getValueF()*V*255)) << 8 | 
				((int)(sldrB.getValueF()*V*255));
	}

	PImage verticalRender () {
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();
			for (int x = 0; x < i.width; x ++) {
				this.update();
				for (int y = 0; y < i.height; y ++) {
					float V  = invert ? 1 - this.getValue() : this.getValue();

					i.pixels[x + y * i.width] = getColor(V);
				}
			}
			i.updatePixels();
		}
		drawPreview(i.copy());
		return i ;
	}

	PImage vertical2Render () {
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();
			for (int x = 0; x < i.width; x ++) {
				this.update();
				float o =0;
				for (int y = 0; y < i.height; y ++) {
					float x_norm = (float)x / (float)i.width;
					float y_norm = (float)y / (float)i.height;
					PVector p = new PVector(this.getValue() - x_norm, this.getValue() - y_norm);
					
					float m = p.magSq()/2f;
					o+= m;
					float V  = invert ? 1 - this.getValue() : this.getValue();
					int n = x  + (y+ int(o)) * i.width;
					
					//n += o;
					if (n >= i.pixels.length-1)n -= i.pixels.length;
					if (n < 0)n += i.pixels.length;
					n = max(0, min(i.pixels.length-1, n));
					i.pixels[n] = getColor(  m * V);
				}
			}
			i.updatePixels();
		}
		drawPreview(i.copy());
		return i ;
	}

	PImage horizontalRender () {
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();
			for (int y = 0; y < i.height; y ++) {
				this.update();
				for (int x = 0; x < i.width; x ++) {
					float V  = invert ? 1 - this.getValue(): this.getValue();
					i.pixels[x + y * i.width] = getColor(V);
				}
			}
			i.updatePixels();
		}
		drawPreview(i.copy());
		return i ;
	}


	PImage circleRender () {
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();
			for (int y = 0; y < i.height; y ++) {
				this.update();
				float G = this.getValue();
				PVector o = new PVector(G, G);
				for (int x = 0; x < i.width; x ++) {
					PVector v = new PVector((float)x/(float)i.width, (float)y/(float)i.height);
					v.sub(o);
					float V = G * sin((G + v.magSq())* TWO_PI);
					V = invert ? 1- V : V ;
					V = min(max(V, 0), 1);
					i.pixels[x + y * p.width] = getColor(V);
				}
			}
		}
		i.updatePixels();
		drawPreview(i.copy());
		return i ;
	}

	float offset = 0;
	PImage diagonalRender () {
		offset = 2 * (0.5-((float)mouseX /(float)width));
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


	PImage exponantRender () {
		offset = 2 * (0.5-((float)mouseX /(float)width));
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

	float g, j;
	PImage cyclicRender () {
		j+= 0.005;
		this.getControlValues();
		osc.setPeriod(this.getPeriod());
		PImage i = new PImage(p.width, p.height);
		boolean invert = cbxActive2.isSelected();
		if (cbxActive1.isSelected()) {
			i.loadPixels();
			for (int y = 0; y < i.height; y ++) {
				float G = this.getValue() * 50 * sin(this.getValue() * TWO_PI);
				this.update();
				for (int x = 0; x < i.width; x ++) {
					PVector v = new PVector(x, y);
					PVector o = new PVector(i.width, i.height);

					v.sub(o);
					float t = 1 + sin(( G + (G * v.magSq())));   

					float V  = invert ? 1 - t: t;
					i.pixels[x + y * p.width] = getColor(V);
				}
			}
			i.updatePixels();
		}
		drawPreview(i.copy());
		return i ;
	}

	void sliderEvent(String name, float value){
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
	void knob1Event(String name, float value){
		if(name == "sldr1")
			sldr1.setValue(value);
		else if(name == "sldr2")
			sldr2.setValue(value);
	}
	void knob2Event(String name,float value){}
	void tap1Event(String name,boolean state){}
	void tap2Event(String name,boolean state){}
	void tap3Event(String name,boolean state){}
}