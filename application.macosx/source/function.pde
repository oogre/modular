
void mateColor(PGraphics p, color c){
	p.blendMode(BLEND);
	p.fill(c);
	p.rect(0, 0, p.width, p.height);
	p.noFill();
}
color ground(color c, int button, int value){
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