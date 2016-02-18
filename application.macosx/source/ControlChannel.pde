void controllerChange(int channel, int number, int value) {
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

int [] getControlChannel(int channel, int number, int value) {
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
  } else if (channel == 1) { /* AKAÏ */
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
	void sliderEvent(String s, float v);
	void knob1Event(String s, float v);
	void knob2Event(String s, float v);
	void tap1Event(String s, boolean b);
	void tap2Event(String s, boolean b);
	void tap3Event(String s, boolean b);
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

	void update(int button, int value){
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
					vw.setScale(zoom[int(value * ratio_ratio)]); 
				}
			break;
			case 0: /* tap1 */
				tap1 = 0 != value;
				for (ArrayList hl : listeners)
           			((ControlListener)hl.get(0)).tap1Event((String)hl.get(1), tap1);
			break;
			case 1: /* tap2 */
				tap2 = 0 != value;
				for (ArrayList hl : listeners)
           			((ControlListener)hl.get(0)).tap2Event((String)hl.get(1), tap2);
			break;
			case 4: /* tap3 */
				tap3 = 0 != value;
				if(chann > osc.size()) return ;
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

	void changeSliderWithPrecesion(int knobValue){
		if(knob1 == 0)knob1 = knobValue;
		float difference = knob1 -= knobValue;
		slider += difference/1270.0f;
		slider = min(max(slider, 0), 1);
		knob1 = knobValue;
	}
	void changeSlider(int sliderValue){
		slider = min(max(sliderValue/127.0f, 0), 1);
	}
}