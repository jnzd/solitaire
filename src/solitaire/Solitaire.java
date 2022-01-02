package solitaire;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.event.*;

public class Solitaire extends JFrame implements ActionListener
{
	private JButton[][] field = new JButton[7][7]; //2-dimensionales Spielfeld mit 2D Array
	private int borderDistance = 0; //borderDistance der Buttons vom Rand
	private int buttonSize = 100; //Abmessungen der quadratischen Buttons
	private boolean buttonSelected = false; //Kontrolle, ob ein Button ausgewaehlt ist
	private int sx = 0; //kurz fuer selectedX, x-Koordinate des ausgewaehlten fieldes
	private int sy = 0; //kurz fuer selectedY, y-Koordinate des ausgewaehlten fieldes
	private int jx = 0; //x-Koordinate des fieldes, auf welches gesprungen werden soll (jump)
	private int jy = 0; //y-Koordinate des fieldes, auf welches gesprungen werden soll (jump)
	private int maxTurns = 34;
	private int turns = 0;
	private int fullFields = 0;
	
	private int[] sxHistory = new int [maxTurns];
	private int[] syHistory = new int [maxTurns];
	private int[] jxHistory = new int [maxTurns];
	private int[] jyHistory = new int [maxTurns];
	
	private JButton reset; //Erstellt den Restknopf
	private int resetHeight = 40; //Hoehe des Reset Knopfs
	private int resetWidth = 100; //Breite des Reset Knopfs
	private int resetx = 0; //Variable fuer x-Koordinate des Resetbuttons
	private int resety = 0; //Variable fuer y-Koordinate des Resetbuttons
	private static int windowHeight; //Fensterhoehe
	private static int windowWidth; //Fensterbreite
	
	private JButton back;
	private JButton forward;
	private int controlSize = 40; // Groesse der Vorwaerts- und Zurueckknoepfe
	
	private JLabel win;
	private JLabel gameOver;
	
	private Color marked = Color.white; //Farbe des markierten fieldes
	private Color empty = Color.gray; //Farbe von leeren fieldern
	private Color full = Color.black; //Farbe von vollen fieldern
	private Color windowBackground = Color.darkGray; //Hintergrundfarbe
	private Color buttonBackground = Color.black; //Hintergrundfarbe der Buttons
	private Color buttonFontColor = Color.white; //Schriftfarbe der Buttons
	
	//Fenster Konstruktor
	public Solitaire()
	{
		super("Solitaire"); //Fenstername
		JPanel board = new JPanel();
		board.setLayout(null);
		for (int i = 0; i<7; i++) {
			for (int j = 0; j<7; j++) {
				field[i][j] = new JButton();
				field[i][j].setSize(buttonSize,buttonSize); // Buttongroesse definieren
				field[i][j].setLocation(borderDistance + (buttonSize)*i, borderDistance + (buttonSize)*j); // Position der einzelnen Button einstellen
				field[i][j].addActionListener(this); // Buttons zum Actionlistener hinzufuegen
				field[i][j].setBackground(full); // alle fielder auf voll setzen
				field[i][j].setBorder(new LineBorder (Color.darkGray));
				board.add(field[i][j]); // Buttons zum field hinzufuegen
			}
		}
		//Eckfelder ausblenden
		hideCorners();
		
		//Farbe des mittleren fieldes aendern
		field[3][3].setBackground(empty);
		field[3][3].setBorder(null);
		
		//Fenstergroesse definieren
		windowHeight = field[6][6].getBounds().y + buttonSize + borderDistance + 100;
		windowWidth = field[6][6].getBounds().x + buttonSize + borderDistance;
		
		//reset Knopf definieren
		reset = new JButton ("Reset");
		reset.setSize(resetWidth,resetHeight);
		reset.setBackground(buttonBackground); //Knopfhintergrund aendern
		reset.setForeground(buttonFontColor); //Schriftfarbe aendern
		reset.setBorder(null); //Knopfraender ausblenden
		reset.addActionListener(this);
		resetx = 300;
		resety = 730;
		reset.setLocation(resetx,resety); //Position des Reset Knopfs definieren
		board.add(reset); //Reset Knopf zum board hinzufuegen
		
		String font = "Arial";
		Font buttonFont = new Font(font, Font.PLAIN, 20);
		Font announcementFont = new Font(font, Font.PLAIN, 100);
		
		//zurueck Knopf definieren
		back = button("<", buttonFont, buttonFontColor, buttonBackground, controlSize);
		back.addActionListener(this);
		back.setLocation(0, 0);
		board.add(back);
		
		//vorwaerts Knopf definieren
		forward = button(">", buttonFont, buttonFontColor, buttonBackground, controlSize);
		forward.addActionListener(this);
		forward.setLocation(controlSize,0);
		board.add(forward);
		
		//Fensterhintergrundfarbe aendern
		board.setBackground(windowBackground);
		
		
		//Win-Screen definieren
		win = announcement("You win!", announcementFont, buttonFontColor);
		win.setSize(500,100);
		win.setLocation(windowHeight/2-250,windowWidth/2-50);
		board.add(win);
		win.setVisible(false);
		
		//game over Screen definieren
		gameOver = announcement("Game Over!", announcementFont, buttonFontColor);
		gameOver.setSize(700,200);
		gameOver.setLocation(windowHeight/2-350,windowWidth/2-100);
		board.add(gameOver);
		gameOver.setVisible(false);
		
		setContentPane (board);
	}
	
	public static JButton button(String text, Font font, Color fontColor, Color backgroundColor, int size) {
		JButton button = new JButton(text);
		button.setFont(font);
		button.setForeground(fontColor);
		button.setSize(size,size);
		button.setBackground(backgroundColor);
		button.setForeground(fontColor);
		button.setBorder(null);
		return button;
	}

	public static JLabel announcement(String text, Font font, Color textColor) {
		JLabel ann = new JLabel (text);
		ann.setForeground(textColor);
		ann.setFont(font);
		ann.setVisible(false);
		return ann;
	}
	
	public void actionPerformed(ActionEvent Klick) {
		Object source = Klick.getSource();
		if(source == reset) { reset(); }
		else if(source == back) { back(); }
		else if(source == forward) { forward(); }
		else if(!buttonSelected) {
			for(int x=0; x<7; x++) {
				for(int y = 0; y<7; y++) {
					if(source == field[x][y]) {
						sx = x; //speichert die x-Koordinate, des ausgewaehlten feldes, in einer Variable
						sy = y; //speichert die y-Koordinate, des ausgewaehlten feldes, in einer Variable
						play1(); //fuehrt die play1 Methode aus
					}
				}
			}
		} else {
			for(int x=0; x<7; x++) {
				for(int y = 0; y<7; y++) {
					if(source == field[x][y]) {
						jx = x; //speichert die x-Koordinate, des feldes auf welches gesprungen werden soll, in einer Variable
						jy = y; //speichert die y-Koordinate, des feldes auf welches gesprungen werden soll, in einer Variable
						play2(); //fuehrt die play2 Methode aus
					}
				}
			}
		}
	}
	
	public void play1() {
		if(field[sx][sy].getBackground() == full) {
		//Nur volle felder koennen zum Springen ausgewaehlt werden. Diese if-Schleife testet, ob ein feld schwarz ist
			field[sx][sy].setBackground(marked); //markiert das ausgewaehlte field mit der Farbe Orange
			buttonSelected = true; // Speichert, dass ein field ausgwaehlt ist
		}		
	}
	
	public void play2() {
		if(field[jx][jy].getBackground() == full) {
			field[jx][jy].setBackground(marked);
			field[sx][sy].setBackground(full);
			sx = jx;
			sy = jy;
			jx = 0;
			jy = 0;
			buttonSelected = true;
		} else if(jx == sx && jy == sy) {
			//testet, ob das field mit dem ausgewaehlten field uebereinstimmt
			field[jx][jy].setBackground(full); //setzt das markierte field zurueck auf schwarz
			buttonSelected = false; //speichert, dass kein field mehr markiert ist
			sx=0;
			sy=0;
			jx=0;
			jy=0;
		}
		else if(field[jx][jy].getBackground() == empty) {
			if(jx == sx && jy == (sy - 2) && field[sx][sy-1].getBackground() == full) {
				// jump up
				field[jx][jy].setBackground(full);
				field[sx][sy].setBackground(empty);
				field[jx][jy + 1].setBackground(empty);
				field[jx][jy].setBorder(new LineBorder (Color.darkGray));
				field[sx][sy].setBorder(null);
				field[jx][jy + 1].setBorder(null);
				logHistory();
				buttonSelected = false;
				gameOver();
			} else if(jx == sx && jy == sy + 2 && field[sx][sy+1].getBackground() == full) {
				// jump down
				field[jx][jy].setBackground(full);
				field[sx][sy].setBackground(empty);
				field[jx][jy -1].setBackground(empty);
				field[jx][jy].setBorder(new LineBorder (Color.darkGray));
				field[sx][sy].setBorder(null);
				field[jx][jy -1].setBorder(null);
				logHistory();
				buttonSelected = false;
				gameOver();
			} else if(jx == sx + 2 && jy == sy && field[sx+1][sy].getBackground() == full) {
				// jump left
				field[jx][jy].setBackground(full);
				field[sx][sy].setBackground(empty);
				field[jx -1][jy].setBackground(empty);
				field[jx][jy].setBorder(new LineBorder (Color.darkGray));
				field[sx][sy].setBorder(null);
				field[jx -1][jy].setBorder(null);
				logHistory();
				buttonSelected = false; 
				gameOver();
			} else if(jx == sx - 2 && jy == sy && field[sx-1][sy].getBackground() == full) {
				// jump right
				field[jx][jy].setBackground(full);
				field[sx][sy].setBackground(empty);
				field[jx + 1][jy].setBackground(empty);
				field[jx][jy].setBorder(new LineBorder (Color.darkGray));
				field[sx][sy].setBorder(null);
				field[jx + 1][jy].setBorder(null);
				logHistory();
				buttonSelected = false;
				gameOver();
			}
		}
	}
	
	public void reset () {
		for (int i = 0; i<7; i++) {
			for (int j = 0; j<7; j++) {
				field[i][j].setBackground(full); // setzt alle fielder auf voll
				field[i][j].setVisible(true);
				field[i][j].setBorder(new LineBorder (Color.darkGray));
				win.setVisible(false);
			}
		}
		field[3][3].setBackground(empty); //Farbe des mittleren fieldes auf leer setzen
		field[3][3].setBorder(null);
		buttonSelected = false; //speichert, dass kein field markiert ist
		for(int i=0; i<maxTurns; i++) {
			sxHistory[i] = 0;
			syHistory[i] = 0;
			jxHistory[i] = 0;
			jyHistory[i] = 0;
		}
		
		hideCorners(); // faellt die Eckfelder mit der Farbe "empty", da es sonst zu Bugs in Zusammenhang mit der gameOver Methode fuehren kann
		
		turns = 0; //setzt die Anzahl Spielzuege zurueck
		sx=0;
		sy=0;
		jx=0;
		jy=0;
	}
	
	public void back () {
		if(turns > 0) {
			//Holt die Koordinaten der fielder, welche am letzten Spielzug beteiligt waren aus dem Verlauf
			sx = sxHistory[turns];
			sy = syHistory[turns];
			jx = jxHistory[turns];
			jy = jyHistory[turns];
			turns--; //setzt den Spielzugzaehler um 1 zurueck
			//macht den Spielzug rueckgaengig
			field[jx][jy].setBackground(empty);
			field[sx][sy].setBackground(full);
			field[(sx+jx)/2][(sy+jy)/2].setBackground(full);
			field[jx][jy].setBorder(null);
			field[sx][sy].setBorder(new LineBorder (Color.darkGray));
			field[(sx+jx)/2][(sy+jy)/2].setBorder(new LineBorder (Color.darkGray));
			
			hideCorners();
		} 
	}
	
	public void forward () {
		if(sxHistory[turns+1] + syHistory[turns+1] > 0) {
			turns++; //erhoeht den Spielzugzaehler um 1
			//holt die Koordinaten fuer den naechsten Zug aus dem Verlauf
			sx = sxHistory[turns];
			sy = syHistory[turns];
			jx = jxHistory[turns];
			jy = jyHistory[turns];
			//macht einen Spielzug vorwaerts
			field[jx][jy].setBackground(full);
			field[sx][sy].setBackground(empty);
			field[(sx+jx)/2][(sy+jy)/2].setBackground(empty);
			field[jx][jy].setBorder(new LineBorder (Color.darkGray));
			field[sx][sy].setBorder(null);
			field[(sx+jx)/2][(sy+jy)/2].setBorder(null);
		}
	}
	
	public void logHistory () //speichert die Spielzuege in einem Verlauf
	{
		turns++;//erhoeht den Spielzugzaehler um 1
		sxHistory[turns] = sx;
		syHistory[turns] = sy;
		jxHistory[turns] = jx;
		jyHistory[turns] = jy;
		//setzt die Variabeln zurueck
		sx = 0;
		sy = 0;
		jx = 0;
		jy = 0;
	}
		
	public void gameOver() {
		boolean turnPossible = false;
		
		for (int i = 0; i<7; i++) {
		//For-Schleife testet fuer alle fielder, ob noch ein Zug moeglich ist. Falls nicht bleibt der bollean "turnPossible" die ganze Schleife ueber false und der win- oder gameOver-Screen wird angezeigt
			for (int j = 0; j<7; j++) {
				if(field[i][j].getBackground() == full) {
					fullFields++;
					turnPossible = turnPossible
								   || i<5 && field[i+1][j].getBackground() == full && field[i+2][j].getBackground() == empty
								   || i>1 && field[i-1][j].getBackground() == full && field[i-2][j].getBackground() == empty
								   || j<5 && field[i][j+1].getBackground() == full && field[i][j+2].getBackground() == empty
								   || j>1 &&field[i][j-1].getBackground() == full && field[i][j-2].getBackground() == empty;
				}
			}
		}
		
		if(fullFields == 1) {
			if(field[3][3].getBackground() == full) {
				clearButtons();
				win.setVisible(true); // win-Screen wird angezeigt
			} else {
				clearButtons(); 
				gameOver.setVisible(true);
			}
		} else if(!turnPossible) {
			clearButtons(); 
			gameOver.setVisible(true); // gameOver-Screen wird angezeigt
		}
		fullFields = 0; //Die Anzahl der vollen fielder wird fuer den naechsten Test zurueckgesetzt
	}
	
	public void clearButtons() {
		for (int x = 0; x<7; x++) {
			for (int y = 0; y<7; y++) {
				field[x][y].setVisible(false); 
			}
		}
	}
	
	public void hideCorners() {
		//Eckfelder ausblenden
		field[0][0].setVisible(false);
		field[0][1].setVisible(false);
		field[0][5].setVisible(false);
		field[0][6].setVisible(false);
		field[1][0].setVisible(false);
		field[1][1].setVisible(false);
		field[1][5].setVisible(false);
		field[1][6].setVisible(false);
		field[5][0].setVisible(false);
		field[5][1].setVisible(false);
		field[5][5].setVisible(false);
		field[5][6].setVisible(false);
		field[6][0].setVisible(false);
		field[6][1].setVisible(false);
		field[6][5].setVisible(false);
		field[6][6].setVisible(false);
		
		//Setzt die Farbe der Eckfelder auf leer
		field[0][0].setBackground(windowBackground);
		field[0][1].setBackground(windowBackground);
		field[0][5].setBackground(windowBackground);
		field[0][6].setBackground(windowBackground);
		field[1][0].setBackground(windowBackground);
		field[1][1].setBackground(windowBackground);
		field[1][5].setBackground(windowBackground);
		field[1][6].setBackground(windowBackground);
		field[5][0].setBackground(windowBackground);
		field[5][1].setBackground(windowBackground);
		field[5][5].setBackground(windowBackground);
		field[5][6].setBackground(windowBackground);
		field[6][0].setBackground(windowBackground);
		field[6][1].setBackground(windowBackground);
		field[6][5].setBackground(windowBackground);
		field[6][6].setBackground(windowBackground);
	}
	
	public static void main (String[] args) {
	//Fenster generieren
		Solitaire Fenster = new Solitaire(); //fuehrt den Konstruktor aus
		Fenster.setSize(windowWidth,windowHeight); //setzt die Groesse des Fensters
		Fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		Fenster.setVisible(true); // macht das Fenster sichtbar
		Fenster.setLocationRelativeTo(null); //zentriert das Fenster auf dem Bildschirm
		Fenster.setResizable(false); //deaktiviert, dass die Fenstergroesse mit der Maus geaendert werden kann
	}
}

