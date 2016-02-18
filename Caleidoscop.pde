class Caleidoscop{

	float alpha = TWO_PI/2.0f;
	PGraphics i;
	int width, height, ox, oy;
	float d;
	float THREE_HALF_PI = 3f * HALF_PI;
	Caleidoscop (PApplet self, int w, int h){
		this.width = w;
		this.height = h;
		this.ox = (int) (this.width / 2.0f);
		this.oy = (int) (this.height / 2.0f);
		d = sqrt(sq(ox)+sq(oy));
		i = createGraphics(this.width, this.height, P3D);
	}

	void setCenterX(float v){
		this.ox = (int) (this.width / 2.0f) + (int) (this.width * v);
	}
	void setCenterY(float v){
		this.oy = (int) (this.height / 2.0f) + (int) (this.height * v);
	}
	void setAlpha(int n){
		alpha = TWO_PI/(float)n;
	}
	void setDiameter(float n){
		d = sqrt(sq(n)+sq(n));
	}

	PImage get(PImage img) {
		if(alpha > PI)return img;
		i.beginDraw();
		mateColor(i, bg);
		int c = 0;
		if(alpha == PI){
			i.pushMatrix();
			i.translate(ox, oy);
			i.rotateZ(alpha/4);
			i.noStroke();
			i.textureMode(NORMAL);
			i.beginShape();
			i.texture(img);
			i.vertex(d * sin(0)            , d * cos(0)            , 1, 1);
			i.vertex(d * sin(HALF_PI)      , d * cos(HALF_PI)      , 0, 1);
			i.vertex(d * sin(PI)           , d * cos(PI)           , 0, 0);
			i.vertex(d * sin(THREE_HALF_PI), d * cos(THREE_HALF_PI), 1, 0);
			i.endShape(CLOSE);
			i.popMatrix();
		}else{
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
		}
		i.endDraw();
		return i ;
	}
}