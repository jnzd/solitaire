package solitaire;
import java.awt.*;

import javax.swing.*;

import javax.swing.border.LineBorder;

import java.awt.Color;
import java.awt.event.*;
import java.util.Arrays;

import javax.swing.JColorChooser;
import javax.swing.border.LineBorder;
public class Solitaire extends JFrame implements ActionListener
{
	private JButton[][] Feld = new JButton[7][7]; //2-dimensionales Spielfeld mit 2D Array
	private int Abstand = 0; //Abstand der Buttons vom Rand
	private int ButtonGrösse = 100; //Abmessungen der quadratischen Buttons
	private boolean ButtonSelected = false; //Kontrolle, ob ein Button ausgewählt ist
	private boolean turnPossible; // Kontrolle, ob noch mindestens ein weiterer Zug möglich ist
	private int sx = 0; //kurz für selectedX, x-Koordinate des ausgewählten Feldes
	private int sy = 0; //kurz für selectedY, y-Koordinate des ausgewählten Feldes
	private int jx = 0; //x-Koordinate des Feldes, auf welches gesprungen werden soll (jump)
	private int jy = 0; //y-Koordinate des Feldes, auf welches gesprungen werden soll (jump)
	private int maxTurns = 34;
	private int turns = 0;
	private int fullFields = 0;
	
	private int[] sxHistory = new int [maxTurns];
	private int[] syHistory = new int [maxTurns];
	private int[] jxHistory = new int [maxTurns];
	private int[] jyHistory = new int [maxTurns];
	
	private JButton reset; //Erstellt den Restknopf
	private int resetHeight = 40; //Höhe des Reset Knopfs
	private int resetWidth = 100; //Breite des Reset Knopfs
	private int resetx = 0; //Variable für x-Koordinate des Resetbuttons
	private int resety = 0; //Variable für y-Koordinate des Resetbuttons
	private static int windowHeight; //Fensterhöhe
	private static int windowWidth; //Fensterbreite
	
	private JButton back;
	private JButton forward;
	private int controlSize = 40; // Grösse der Vorwärts- und Zurückknöpfe
	
	private JLabel win;
	private JLabel gameOver;
	
	private Color marked = Color.white; //Farbe des markierten Feldes
	private Color empty = Color.gray; //Farbe von leeren Feldern
	private Color full = Color.black; //Farbe von vollen Feldern
	private Color windowBackground = Color.darkGray; //Hintergrundfarbe
	private Color buttonBackground = Color.black; //Hintergrundfarbe der Buttons
	private Color buttonFont = Color.white; //Schriftfarbe der Buttons
	
	//Fenster Konstruktor
	public Solitaire()
	{
		super("Solitaire"); //Fenstername
		JPanel Spielbrett = new JPanel();
		Spielbrett.setLayout(null);
		for (int i = 0; i<7; i++) //For Schleife für x- Koordinaten
		{
			for (int j = 0; j<7; j++) //For Schleife für y- Koordinaten
			{
				Feld[i][j] = new JButton();
				Feld[i][j].setSize(ButtonGrösse,ButtonGrösse); // Buttongrösse definieren
				Feld[i][j].setLocation(Abstand + (ButtonGrösse)*i, Abstand + (ButtonGrösse)*j); // Position der einzelnen Button einstellen
				Feld[i][j].addActionListener(this); // Buttons zum Actionlistener hinzufügen
				Feld[i][j].setBackground(full); // alle Felder auf voll setzen
				Feld[i][j].setBorder(new LineBorder (Color.darkGray));
				Spielbrett.add(Feld[i][j]); // Buttons zum Feld hinzufügen
			}
		}
	//Eckfelder ausblenden
	hideCorners();
	
	//Farbe des mittleren Feldes ändern
	Feld[3][3].setBackground(empty);
	Feld[3][3].setBorder(null);
	
	//Fenstergrösse definieren
	windowHeight = Feld[6][6].getBounds().y + ButtonGrösse + Abstand + 100;
	windowWidth = Feld[6][6].getBounds().x + ButtonGrösse + Abstand;
	
	//reset Knopf definieren
	reset = new JButton ("Reset");
	reset.setSize(resetWidth,resetHeight);
	reset.setBackground(buttonBackground); //Knopfhintergrund ändern
	reset.setForeground(buttonFont); //Schriftfarbe ändern
	reset.setBorder(null); //Knopfränder ausblenden
	reset.addActionListener(this);
	resetx = 300;
	resety = 730;
	reset.setLocation(resetx,resety); //Position des Reset Knopfs definieren
	Spielbrett.add(reset); //Reset Knopf zum Spielbrett hinzufügen
	
	//zurück Knopf definieren
	back = new JButton ("<");
	back.setSize(controlSize,controlSize);
	back.setBackground(buttonBackground);
	back.setFont(new Font("Arial", Font.PLAIN, 20));
	back.setForeground(buttonFont);
	back.setBorder(null);
	back.addActionListener(this);
	back.setLocation(0, 0);
	Spielbrett.add(back);
	
	//vorwärts Knopf definieren
	forward = new JButton (">");
	forward.setSize(controlSize,controlSize);
	forward.setBackground(buttonBackground);
	forward.setFont(new Font("Arial", Font.PLAIN, 20));
	forward.setForeground(buttonFont);
	forward.setBorder(null);
	forward.addActionListener(this);
	forward.setLocation(controlSize,0);
	Spielbrett.add(forward);
	
	//Fensterhintergrundfarbe ändern
	Spielbrett.setBackground(windowBackground);
	
	//Win-Screen definieren
	win = new JLabel ("You win!");
	win.setSize(500,100);
	win.setLocation(windowHeight/2-250,windowWidth/2-50);
	win.setForeground(buttonFont);
	win.setFont(new Font("Arial", Font.BOLD, 100));
	Spielbrett.add(win);
	win.setVisible(false);
	
	//game over Screen definieren
	gameOver = new JLabel ("Game Over!");
	gameOver.setSize(700,200);
	gameOver.setLocation(windowHeight/2-350,windowWidth/2-100);
	gameOver.setForeground(buttonFont);
	gameOver.setFont(new Font("Arial", Font.BOLD, 100));
	Spielbrett.add(gameOver);
	gameOver.setVisible(false);
	
	setContentPane (Spielbrett);
	}
	
	public void actionPerformed(ActionEvent Klick) 
	{
		Object Quelle = Klick.getSource();
		if(Quelle == reset) //Testet, ob der Resetknopf gedrückt wurde
		{
			reset(); //führt die reset Methode aus
		}
		else if(Quelle == back)
		{
			back();
		}
		else if(Quelle == forward)
		{
			forward();
		}
		else if(ButtonSelected == false)
		{
			for(int x=0; x<7; x++) //For-Schleife für x-Koordinaten
			{
				for(int y = 0; y<7; y++) //For-Schleife für y-Koordinaten
				{
					if(Quelle == Feld[x][y]) //testet, von welchem Feld eine Aktion ausgeht
					{
						sx = x; //speichert die x-Koordinate, des ausgewählten Feldes, in einer Variable
						sy = y; //speichert die y-Koordinate, des ausgewählten Feldes, in einer Variable
						play1(); //führt die play1 Methode aus
					}
				}
			}
		}
		else if(ButtonSelected == true)
		{
			for(int x=0; x<7; x++) //For-Schleife für x-Koordinaten
			{
				for(int y = 0; y<7; y++) //For-Schleife für y-Koordinaten
				{
					if(Quelle == Feld[x][y]) //testet, von welchem Feld eine Aktion ausgeht
					{
						jx = x; //speichert die x-Koordinate, des Feldes auf welches gesprungen werden soll, in einer Variable
						jy = y; //speichert die y-Koordinate, des Feldes auf welches gesprungen werden soll, in einer Variable
						play2(); //führt die play2 Methode aus
					}
				}
			}
		}
	}
	
	public void play1() //Methode für den ersten Teil eines Spielzugs. (auswählen eines Feldes)
	{
		if(Feld[sx][sy].getBackground() == full) //Nur volle Felder können zum Springen ausgewählt werden. Diese if-Schleife testet, ob ein Feld schwarz ist
		{
				Feld[sx][sy].setBackground(marked); //markiert das ausgewählte Feld mit der Farbe Orange
				ButtonSelected = true; // Speichert, dass ein Feld ausgwählt ist
		}		
	}
	
	public void play2() //Methode für den zweiten Teil eines Spielzugs. Das Überspringen
	{
		if(Feld[jx][jy].getBackground() == full) //if-Schleife zum direkten Wechseln des ausgewählten Feldes
		{
			Feld[jx][jy].setBackground(marked);
			Feld[sx][sy].setBackground(full);
			sx = jx;
			sy = jy;
			jx = 0;
			jy = 0;
			ButtonSelected = true;
		}
		else if(jx == sx && jy == sy) //testet, ob das Feld mit dem ausgewählten Feld übereinstimmt
		{
			Feld[jx][jy].setBackground(full); //setzt das markierte Feld zurück auf schwarz
			ButtonSelected = false; //speichert, dass kein Feld mehr markiert ist
			sx=0;
			sy=0;
			jx=0;
			jy=0;
		}
		else if(Feld[jx][jy].getBackground() == empty)
		{
			if(jx == sx && jy == (sy - 2) && Feld[sx][sy-1].getBackground() == full) //nach oben springen
			{
				Feld[jx][jy].setBackground(full);
				Feld[sx][sy].setBackground(empty);
				Feld[jx][jy + 1].setBackground(empty);
				Feld[jx][jy].setBorder(new LineBorder (Color.darkGray));
				Feld[sx][sy].setBorder(null);
				Feld[jx][jy + 1].setBorder(null);
				logHistory();
				ButtonSelected = false; //speichert, dass kein Feld mehr markiert ist
				gameOver();
			}
			else if(jx == sx && jy == sy + 2 && Feld[sx][sy+1].getBackground() == full) //nach unten springen
			{
				Feld[jx][jy].setBackground(full);
				Feld[sx][sy].setBackground(empty);
				Feld[jx][jy -1].setBackground(empty);
				Feld[jx][jy].setBorder(new LineBorder (Color.darkGray));
				Feld[sx][sy].setBorder(null);
				Feld[jx][jy -1].setBorder(null);
				logHistory();
				ButtonSelected = false; //speichert, dass kein Feld mehr markiert ist
				gameOver();
			}
			else if(jx == sx + 2 && jy == sy && Feld[sx+1][sy].getBackground() == full) //nach links springen
			{
				Feld[jx][jy].setBackground(full);
				Feld[sx][sy].setBackground(empty);
				Feld[jx -1][jy].setBackground(empty);
				Feld[jx][jy].setBorder(new LineBorder (Color.darkGray));
				Feld[sx][sy].setBorder(null);
				Feld[jx -1][jy].setBorder(null);
				logHistory();
				ButtonSelected = false; //speichert, dass kein Feld mehr markiert ist
				gameOver();
			}
			else if(jx == sx - 2 && jy == sy && Feld[sx-1][sy].getBackground() == full) //nach rechts springen
			{
				Feld[jx][jy].setBackground(full);
				Feld[sx][sy].setBackground(empty);
				Feld[jx + 1][jy].setBackground(empty);
				Feld[jx][jy].setBorder(new LineBorder (Color.darkGray));
				Feld[sx][sy].setBorder(null);
				Feld[jx + 1][jy].setBorder(null);
				logHistory();
				ButtonSelected = false; //speichert, dass kein Feld mehr markiert ist
				gameOver();
			}
		}
	}
	
	public void reset ()
	{
		for (int i = 0; i<7; i++)
		{
			for (int j = 0; j<7; j++)
			{
				Feld[i][j].setBackground(full); // setzt alle Felder auf voll
				Feld[i][j].setVisible(true);
				Feld[i][j].setBorder(new LineBorder (Color.darkGray));
				win.setVisible(false);
			}
		}
		Feld[3][3].setBackground(empty); //Farbe des mittleren Feldes auf leer setzen
		Feld[3][3].setBorder(null);
		ButtonSelected = false; //speichert, dass kein Feld markiert ist
		for(int i=0; i<maxTurns; i++) //löscht den Verlauf
		{
			sxHistory[i] = 0;
			syHistory[i] = 0;
			jxHistory[i] = 0;
			jyHistory[i] = 0;
		}
		
		hideCorners(); // füllt die Eckfelder mit der Farbe "empty", da es sonst zu Bugs in Zusammenhang mit der gameOver Methode führen kann
		
		turns = 0; //setzt die Anzahl Spielzüge zurück
		sx=0;
		sy=0;
		jx=0;
		jy=0;
	}
	
	public void back ()
	{
		if(turns > 0) // testet, ob bereits ein Zug gemacht wurde
		{
			//Holt die Koordinaten der Felder, welche am letzten Spielzug beteiligt waren aus dem Verlauf
			sx = sxHistory[turns];
			sy = syHistory[turns];
			jx = jxHistory[turns];
			jy = jyHistory[turns];
			turns--; //setzt den Spielzugzähler um 1 zurück
			//macht den Spielzug rückgängig
			Feld[jx][jy].setBackground(empty);
			Feld[sx][sy].setBackground(full);
			Feld[(sx+jx)/2][(sy+jy)/2].setBackground(full);
			Feld[jx][jy].setBorder(null);
			Feld[sx][sy].setBorder(new LineBorder (Color.darkGray));
			Feld[(sx+jx)/2][(sy+jy)/2].setBorder(new LineBorder (Color.darkGray));
			
			hideCorners();
		} 
	}
	
	public void forward ()
	{
		if(sxHistory[turns+1] + syHistory[turns+1] > 0) //testet, ob ein Zug nach vorne gemacht werden kann
		{
			turns++; //erhöht den Spielzugzähler um 1
			//holt die Koordinaten für den nächsten Zug aus dem Verlauf
			sx = sxHistory[turns];
			sy = syHistory[turns];
			jx = jxHistory[turns];
			jy = jyHistory[turns];
			//macht einen Spielzug vorwärts
			Feld[jx][jy].setBackground(full);
			Feld[sx][sy].setBackground(empty);
			Feld[(sx+jx)/2][(sy+jy)/2].setBackground(empty);
			Feld[jx][jy].setBorder(new LineBorder (Color.darkGray));
			Feld[sx][sy].setBorder(null);
			Feld[(sx+jx)/2][(sy+jy)/2].setBorder(null);
		}
	}
	
	public void logHistory () //speichert die Spielzüge in einem Verlauf
	{
		turns++;//erhöht den Spielzugzähler um 1
		sxHistory[turns] = sx;
		syHistory[turns] = sy;
		jxHistory[turns] = jx;
		jyHistory[turns] = jy;
		//setzt die Variabeln zurück
		sx = 0;
		sy = 0;
		jx = 0;
		jy = 0;
	}
		
	public void gameOver() //Funktion, welche testet, ob noch mindestens ein Spielzug möglich ist und ob das Spiel geschafft wurde
	{
		turnPossible = false;
		
		for (int i = 0; i<7; i++)//For-Schleife testet für alle Felder, ob noch ein Zug möglich ist. Falls nicht bleibt der bollean "turnPossible" die ganze Schleife über false und der win- oder gameOver-Screen wird angezeigt
		{
			for (int j = 0; j<7; j++)
			{
				if(Feld[i][j].getBackground() == full) //Feld voll?
				{
					fullFields++;//zählt die Anzahl der gefüllten Felder
					if(i<5 && Feld[i+1][j].getBackground() == full && Feld[i+2][j].getBackground() == empty) //Zug möglich nach unten?
					{
						turnPossible = true;
					}
					else if(i>1 && Feld[i-1][j].getBackground() == full && Feld[i-2][j].getBackground() == empty) //Zug möglich nach oben?
					{
						turnPossible = true;
					}
					else if(j<5 && Feld[i][j+1].getBackground() == full && Feld[i][j+2].getBackground() == empty) //Zug möglich nach rechts?
					{
						turnPossible = true;
					}
					else if(j>1 &&Feld[i][j-1].getBackground() == full &&Feld[i][j-2].getBackground() == empty) //Zug möglich nach links?
					{						
						turnPossible = true;
					}
				}
			}
		}
		
		if(fullFields == 1) //wenn nur noch ein Feld voll ist, wird getestet, ob es sich um das mittlere handelt und somit das Spiel gewonnen wurde
		{
			if(Feld[3][3].getBackground() == full)
			{
				clearButtons();
				win.setVisible(true); // win-Screen wird angezeigt
			}
			else
			{
				clearButtons(); 
				gameOver.setVisible(true);
			}
		}
		else if(turnPossible == false)
		{
			clearButtons(); 
			gameOver.setVisible(true); // gameOver-Screen wird angezeigt
		}
		fullFields = 0; //Die Anzahl der vollen Felder wird für den nächsten Test zurückgesetzt
	}
	
	public void clearButtons() //Funktion zum Ausblenden aller Felder, für Win oder Game Over Screen
	{
		for (int x = 0; x<7; x++)
		{
			for (int y = 0; y<7; y++)
			{
				Feld[x][y].setVisible(false); 
			}
		}
	}
	
	public void hideCorners()
	{
		//Eckfelder ausblenden
		Feld[0][0].setVisible(false);
		Feld[0][1].setVisible(false);
		Feld[0][5].setVisible(false);
		Feld[0][6].setVisible(false);
		Feld[1][0].setVisible(false);
		Feld[1][1].setVisible(false);
		Feld[1][5].setVisible(false);
		Feld[1][6].setVisible(false);
		Feld[5][0].setVisible(false);
		Feld[5][1].setVisible(false);
		Feld[5][5].setVisible(false);
		Feld[5][6].setVisible(false);
		Feld[6][0].setVisible(false);
		Feld[6][1].setVisible(false);
		Feld[6][5].setVisible(false);
		Feld[6][6].setVisible(false);
		
		//Setzt die Farbe der Eckfelder auf leer
		Feld[0][0].setBackground(windowBackground);
		Feld[0][1].setBackground(windowBackground);
		Feld[0][5].setBackground(windowBackground);
		Feld[0][6].setBackground(windowBackground);
		Feld[1][0].setBackground(windowBackground);
		Feld[1][1].setBackground(windowBackground);
		Feld[1][5].setBackground(windowBackground);
		Feld[1][6].setBackground(windowBackground);
		Feld[5][0].setBackground(windowBackground);
		Feld[5][1].setBackground(windowBackground);
		Feld[5][5].setBackground(windowBackground);
		Feld[5][6].setBackground(windowBackground);
		Feld[6][0].setBackground(windowBackground);
		Feld[6][1].setBackground(windowBackground);
		Feld[6][5].setBackground(windowBackground);
		Feld[6][6].setBackground(windowBackground);
	}
	
	public static void main (String[] args)
	//Fenster generieren
	{
		Solitaire Fenster = new Solitaire(); //führt den Konstruktor aus
		Fenster.setSize(windowWidth,windowHeight); //setzt die Grösse des Fensters
		Fenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
		Fenster.setVisible(true); // macht das Fenster sichtbar
		Fenster.setLocationRelativeTo(null); //zentriert das Fenster auf dem Bildschirm
		Fenster.setResizable(false); //deaktiviert, dass die Fenstergrösse mit der Maus geändert werden kann
	}
}

