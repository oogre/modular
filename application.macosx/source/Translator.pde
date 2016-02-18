class Translator {

  int width, height;
  PImage translator;

  Translator(int _width, int _height) {
    this.width = _width;
    this.height = _height;
    translator = new PImage(this.width, this.height);
  }

  void setTranslator(PImage tr) {
    this.translator = tr;
  }

  PImage get(PImage img) {
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
        int t = int(i++ * ratio);
        byte inc = byte(this.translator.pixels[t] & 0xFF); // blue
        byte dec = byte(this.translator.pixels[t] >> 8 & 0xFF); // green
        offset += inc/2560.0;
        offset -= dec/2560.0;
        int c = int(i + offset);
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