public class BeatBox {
	JPanel panel;
	ArrayList<JCheckBox> checkboxList;
	Sequencer sequencer;
	Sequence sequence;
 	Track track;
 	JFrame frame;
 	
	String[] instrumentNames = {"Bass Drum", "Closed Hi-Hat", "Open Hi-Hat",
			"Acoustic Snare", "Crash Cymbal", "Hand Clap", "High Tom", 
			"Hi Bongo", "Maracas", "Whistle", "Low Conga", "Cowbell", "Vibraslap",
			"Low-mid Tom", "High Agogo", "Open Hi Conga"};
	int[] instruments = {35,42,46,38,49,39,50,60,70,72,64,56,58,47,67,63};
	
	public static void main(String[] args) {
		BeatBox bb = new BeatBox();
		bb.makeGui();

	}

		public void makeGui(){
			frame =new JFrame("BeatBox");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			BorderLayout layout = new BorderLayout();
			JPanel background = new JPanel(layout);
			background.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			checkboxList = new ArrayList<JCheckBox>();
			Box buttonBox = new Box(BoxLayout.Y_AXIS);
			
			JButton start = new JButton("Start");
			start.addActionListener(new MyStartListener());
			buttonBox.add(start);
			
			JButton stop = new JButton("Stop");
			stop.addActionListener(new MyStopListener());
			buttonBox.add(stop);
		
			
			JButton upTempo = new JButton("Tempo Up");
			upTempo.addActionListener(new MyUpTempoListener());
			buttonBox.add(upTempo);
			
			JButton downTempo = new JButton("Tempo Down");
			downTempo.addActionListener(new MyDownTempoListener());
			buttonBox.add(downTempo);
			
			Box nameBox = new Box(BoxLayout.Y_AXIS);
			for (int i =0 ; i<16; i++ ){
				nameBox.add(new Label(instrumentNames[i]));
			}
			background.add(BorderLayout.EAST, buttonBox);
			background.add(BorderLayout.WEST, nameBox);
			
			frame.getContentPane().add(background);
		
			GridLayout grid = new GridLayout(16,16);
			grid.setVgap(1);
			grid.setHgap(2);
			panel = new JPanel(grid);
			background.add(BorderLayout.CENTER, panel);
			
			for(int i=0; i<256; i++){
				JCheckBox c = new JCheckBox();
				c.setSelected(false);
				checkboxList.add(c);
				panel.add(c);
			}
			setUpMidi();
			frame.setSize(300, 300);
			frame.setLocationRelativeTo(null);
			frame.pack();
			frame.setVisible(true);
		}
		public void setUpMidi(){
			try{
				sequencer = MidiSystem.getSequencer();
				sequencer.open();
				sequence = new Sequence(Sequence.PPQ, 4);
				track = sequence.createTrack();
				sequencer.setTempoInBPM(120);
			} catch(Exception e){e.printStackTrace();}
		}
		
		public void buildTrackAndStart(){
			int[] trackList = null;
			
			sequence.deleteTrack(track);
			track = sequence.createTrack();
			for(int i=0; i<16; i++){
				
				trackList = new int[16];
				int key = instruments[i];
				for(int j=0; j<16; j++){
					JCheckBox jc = (JCheckBox) checkboxList.get(j+(16*i));
					if(jc.isSelected()){
						trackList[j] = key;
					} else {
						trackList[j] = 0;
					}
				}
				makeTrack(trackList);
				track.add(makeEvent(176, 1,127,0,16));
				
			}
			track.add(makeEvent(192,9,1,0,15));
			try{
				sequencer.setSequence(sequence);
				sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
				sequencer.start();
				sequencer.setTempoInBPM(120);
			}catch(Exception e){e.printStackTrace();}
			
			
			
		}
			public  class MyStartListener implements ActionListener{
				public void actionPerformed(ActionEvent arg0) {
					buildTrackAndStart();
					
				}
				
			}
			
			
		public class MyStopListener implements ActionListener{
			public void actionPerformed(ActionEvent arg0) {
				sequencer.stop();
				
			}
			
		}
		public class MyUpTempoListener implements ActionListener{
			public void actionPerformed(ActionEvent arg0) {
				float tempoFactor = sequencer.getTempoFactor();
				sequencer.setTempoFactor((float)(tempoFactor*1.03));	
			}
			
		}
		
		
		public class MyDownTempoListener implements ActionListener{
			public void actionPerformed(ActionEvent arg0) {
				float tempoFactor = sequencer.getTempoFactor();
				sequencer.setTempoFactor((float)(tempoFactor* .97));	
				
			}
			
		}
		public void makeTrack(int[] list){
			for(int i=0; i<16; i++){
				int key = list[i];
				if(key !=0){
					track.add(makeEvent(144,9,key,100,i));
					track.add(makeEvent(128,9,key,100,i+1));
				}
			}
			
		}
		
		public MidiEvent makeEvent(int comd, int chan, int one, int two, int tick){
			MidiEvent event = null;
			try{
				ShortMessage a = new ShortMessage();
				a.setMessage(comd, chan, one, two);
				event = new MidiEvent(a, tick);
			}catch(Exception e){e.printStackTrace();}
			
			return event;
			
		}
}
