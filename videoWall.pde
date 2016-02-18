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
  void setScale(float scale) {
    this.scale = scale;
    this.l = this.width * this.scale;
    this.h = this.height * this.scale;
  }
  PImage get(PImage img) {
    i.beginDraw();
    for (int x = 0; x < 1.0/scale; x ++) {
      for (int y = 0; y < 1.0/scale; y ++) {
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
  void display(PImage img) {
    translate(this.ox, this.oy);
    for (int x = 0; x < 1.0/scale; x ++) {
      for (int y = 0; y < 1.0/scale; y ++) {
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