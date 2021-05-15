// Bakeoff #3 - Escrita de Texto em Smartwatches
// IPM 2019-20, Semestre 2
// Entrega: exclusivamente no dia 22 de Maio, até às 23h59, via Discord

// Processing reference: https://processing.org/reference/

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

// Screen resolution vars;
float PPI, PPCM;
float SCALE_FACTOR;

// Finger parameters
PImage fingerOcclusion;
int FINGER_SIZE;
int FINGER_OFFSET;

// Arm/watch parameters
PImage arm;
int ARM_LENGTH;
int ARM_HEIGHT;

// Arrow parameters
PImage left;
PImage right;
int ARROW_SIZE;
PImage home;
int HOME_SIZE;

// Study properties
String[] phrases;                         // contains all the phrases that can be tested
int NUM_REPEATS            = 2;           // the total number of phrases to be tested
String[] typed = new String[NUM_REPEATS]; // the current the phrase written by the user
int currTrialNum           = 0;           // the current trial number (indexes into phrases array above)
String currentPhrase       = "";          // the current target phrase
String currentTyped        = "";          // what the user has typed so far
String currentWord         = "";          // the word user is writting
String palavra1            = "the ";      // main menu words
String palavra2            = "of ";
String palavra3            = "to ";
String palavra4            = "and ";
String refEsquerda         = "v - z";     // reference to the menu on the left
String refDireita          = "b - f";     // reference to the menu on the right
char letter1               = 'a';         // initialization of the letter on each screen
char letter2               = 'b';
char letter3               = 'c';
char letter4               = 'd';
char letter5               = 'e';
String[] list;                            // initialization of the list containig all the pre-selected common word of the english dictionary
String[] sugestoes         = new String[867]; // initialization of the list that will contain all the sugestions to a semi-written word
String sugestaoEsquerda    = "";          // sugestion on the left
String sugestaoDireita     = "";          // sugestion on the right

// Performance variables
float startTime            = 0;     // time starts when the user clicks for the first time
float finishTime           = 0;     // records the time of when the final trial ends
float lastTime             = 0;     // the timestamp of when the last trial was completed
float lettersEnteredTotal  = 0;     // a running total of the number of letters the user has entered (need this for final WPM computation)
float lettersExpectedTotal = 0;     // a running total of the number of letters expected (correct phrases)
float errorsTotal          = 0;     // a running total of the number of errors (when hitting next)
int ecra                   = 1;     // flag that keeps track of witch screen to display (except the main screen)
int flag                   = 0;     // flag that is 1 if the user is on the main screen and 0 otherwise

//Setup window and vars - runs once
void setup()
{
  //size(900, 900);
  fullScreen();
  textFont(createFont("Arial", 24));  // set the font to arial 24
  noCursor();                         // hides the cursor to emulate a watch environment
  
  // Load images
  home = loadImage("home.png");       // image of representing the home/main screen
  arm = loadImage("arm_watch.png");
  fingerOcclusion = loadImage("finger.png");
  right = loadImage("right.png");     // right arrow
  left = loadImage("left.png");       // left arrow
  
  // Load sugestion words
  list = loadStrings("CommonWords.txt");
  
  // Load phrases
  phrases = loadStrings("phrases.txt");                       // load the phrase set into memory
  Collections.shuffle(Arrays.asList(phrases), new Random());  // randomize the order of the phrases with no seed
  
  // Scale targets and imagens to match screen resolution
  SCALE_FACTOR = 1.0 / displayDensity();          // scale factor for high-density displays
  String[] ppi_string = loadStrings("ppi.txt");   // the text from the file is loaded into an array.
  PPI = float(ppi_string[1]);                     // set PPI, we assume the ppi value is in the second line of the .txt
  PPCM = PPI / 2.54 * SCALE_FACTOR;               // do not change this!
  
  FINGER_SIZE = (int)(11 * PPCM);
  FINGER_OFFSET = (int)(0.8 * PPCM);
  ARM_LENGTH = (int)(19 * PPCM);
  ARM_HEIGHT = (int)(11.2 * PPCM);
  HOME_SIZE = (int)(1.8 * PPCM);
  ARROW_SIZE = (int)(1.8 * PPCM);                 // the size of the arrows is smaller so that they fit the non Interative area
}

void draw()
{ 
  // Check if we have reached the end of the study
  if (finishTime != 0)  return;
 
  background(255);                                                         // clear background
  
  // Draw arm and watch background
  imageMode(CENTER);
  image(arm, width/2, height/2, ARM_LENGTH, ARM_HEIGHT);
  
  // Check if we just started the application
  if (startTime == 0 && !mousePressed)
  {
    fill(0);
    textAlign(CENTER);
    text("Tap to start time!", width/2, height/2);
  }
  else if (startTime == 0 && mousePressed) nextTrial();                    // show next sentence
  
  // Check if we are in the middle of a trial
  else if (startTime != 0)
  {
    textAlign(LEFT);
    fill(100);
    text("Phrase " + (currTrialNum + 1) + " of " + NUM_REPEATS, width/2 - 4.0*PPCM, height/2 - 8.1*PPCM);   // write the trial count
    text("Target:    " + currentPhrase, width/2 - 4.0*PPCM, height/2 - 7.1*PPCM);                           // draw the target string
    fill(0);
    text("Entered:  " + currentTyped + "|", width/2 - 4.0*PPCM, height/2 - 6.1*PPCM);                      // draw what the user has entered thus far 
    
    // Draw very basic ACCEPT button - do not change this!
    textAlign(CENTER);
    noStroke();
    fill(0, 250, 0);
    rect(width/2 - 2*PPCM, height/2 - 5.1*PPCM, 4.0*PPCM, 2.0*PPCM);
    fill(0);
    text("ACCEPT >", width/2, height/2 - 4.1*PPCM);
    noStroke();
    
    // Draw screen areas
    // simulates text box - not interactive
    drawNonInteractiveArea();    // calls funtion that draws the non interactive area
    
    // THIS IS THE ONLY INTERACTIVE AREA (4cm x 4cm); do not change size
    stroke(0, 255, 0);
    noFill();
    rect(width/2 - 2.0*PPCM, height/2 - 1.0*PPCM, 4.0*PPCM, 3.0*PPCM);
    
    // Write current letter
    textAlign(CENTER);
    fill(0);
    stroke(153);
    // Draws the outline of the keyboard
    noFill();
    drawDisplay();  // calls a funtion that draws the lines on the display
    // Draws the main features of the keyboard
    textSize(0.35*PPCM);
    text('_',  width/2, height/2 + 1.7*PPCM);
    text('`',  width/2 + 1.35*PPCM, height/2 + 1*PPCM);
    text("" + refDireita, width/2 + 1.35*PPCM, height/2 + 1.8*PPCM);
    text("" + refEsquerda, width/2 - 1.35*PPCM, height/2 + 1.8*PPCM);
    text("" + sugestaoDireita, width/2 + 1.00*PPCM, height/2 - 0.55*PPCM);    //all the words in commonWords.txt fit the space
    text("" + sugestaoEsquerda, width/2 - 1.00*PPCM, height/2 - 0.55*PPCM);   //all the words in commonWords.txt fit the space
    drawWord(ecra);  // calls a funtion that draws the words/letters acording to the screen
       
    // Draws the home image on the keyboard acording to witch screen the user is in
    noFill();
    imageMode(CORNER);
    if (flag == 1) { //screen after the home menu
      image(home, width/2 - 1.8*PPCM, height/2 + 1.2*PPCM, HOME_SIZE/2, HOME_SIZE/2);
    }
    else if (flag == 2) { //screen before the home menu
      image(home, width/2 + 0.9*PPCM, height/2 + 1.2*PPCM, HOME_SIZE/2, HOME_SIZE/2);
    }
  }
  
  // Draw the user finger to illustrate the issues with occlusion (the fat finger problem)
  imageMode(CORNER);
  image(fingerOcclusion, mouseX - FINGER_OFFSET, mouseY - FINGER_OFFSET, FINGER_SIZE, FINGER_SIZE);
}

// Draw the words/letter on the keyboard
void drawWord(int flag){
  if(flag == 1) { //main menu keyboard
    text("" + palavra1,  width/2 - 1.35*PPCM, height/2 + 0.25*PPCM);
    text("" + palavra2, width/2, height/2 + 0.25*PPCM);
    text("" + palavra3,  width/2 + 1.35*PPCM, height/2 + 0.25*PPCM);
    text("" + palavra4, width/2 - 1.35*PPCM, height/2 + 1*PPCM);
    text("" + letter1,  width/2, height/2 + 1*PPCM);
  }
  else { //other keyboards
    //letter apear in alphabet oder
    text("" + letter2,  width/2 - 1.35*PPCM, height/2 + 0.25*PPCM);
    text("" + letter3, width/2, height/2 + 0.25*PPCM);
    text("" + letter4,  width/2 + 1.35*PPCM, height/2 + 0.25*PPCM);
    text("" + letter5, width/2 - 1.35*PPCM, height/2 + 1*PPCM);
    text("" + letter1,  width/2, height/2 + 1*PPCM);
  }
}

//Draw display lines on the screen
void drawDisplay() {
  line(width/2 - 2*PPCM, height/2 - 0.3*PPCM, width/2 + 2*PPCM, height/2 - 0.3*PPCM);    //upper horizontal line
  line(width/2 - 2*PPCM, height/2 + 0.5*PPCM, width/2 + 2*PPCM, height/2 + 0.5*PPCM);    //middle horizontal line
  line(width/2 - 2*PPCM, height/2 + 1.25*PPCM, width/2 + 2*PPCM, height/2 + 1.25*PPCM);  //lower horizontal line
  line(width/2 - 0.7*PPCM, height/2 - 0.3*PPCM, width/2 - 0.7*PPCM, height/2 + 2*PPCM);  //left vertical line
  line(width/2 + 0.7*PPCM, height/2 - 0.3*PPCM, width/2 + 0.7*PPCM, height/2 + 2*PPCM);  //right vertical line
  line(width/2, height/2 - 1*PPCM, width/2, height/2 - 0.3*PPCM);                        //middle (shorter) vertical line
}

// Draw non interactive area - area that helps guide the user through the screens
void drawNonInteractiveArea()
{
  // simulates text box - not interactive
    noStroke();
    fill(125);
    rect(width/2 - 2.0*PPCM, height/2 - 2.0*PPCM, 4.0*PPCM, 1.0*PPCM);
    textAlign(CENTER);
    fill(0);
    //draws the arrows
    image(left, width/2 - 0.7*PPCM, height/2 - 1.5*PPCM, ARROW_SIZE/2, ARROW_SIZE/2);
    image(right, width/2 + 0.7*PPCM, height/2 - 1.5*PPCM, ARROW_SIZE/2, ARROW_SIZE/2);
    //draws the home image acoording to the screen
    if(flag == 2) //screen before the main/home screen
      image(home, width/2 + 1.5*PPCM, height/2 - 1.5*PPCM, HOME_SIZE/2, HOME_SIZE/2);
    else if(flag == 1) //screen after the main/home screen
      image(home, width/2 - 1.5*PPCM, height/2 - 1.5*PPCM, HOME_SIZE/2, HOME_SIZE/2);
    //draws the references to the next display (the same as on the keyboard)
    text("" + refEsquerda, width/2 - 1.5*PPCM, height/2 - 1.4*PPCM);
    text("" + refDireita, width/2 + 1.5*PPCM, height/2 - 1.4*PPCM);
}

// Check if mouse click was within certain bounds
boolean didMouseClick(float x, float y, float w, float h)
{
  return (mouseX > x && mouseX < x + w && mouseY > y && mouseY < y + h);
}

// Finds the suggested words acording to the alredy written part of the word
void apresentaSugestao(String sugEsq, String sugDir) {
  int n=0;
  for(int j = 0; j < 867; j++) {  // amount of suggested words = 987
    if(list[j].matches(currentWord + "(.*)") && list[j]!= sugEsq && list[j]!= sugDir){ //finds the matching words in the file
      sugestoes[n] = list[j];                  //saves the sugestions in a array
      n++;
    }
  }
  if(n > 2) { //if there are more than 2 suggested words
    sugestaoEsquerda = sugestoes[0];
    sugestaoDireita = sugestoes[1];
  }
  else if(n == 1) //if there is only one suggested word
    sugestaoEsquerda = sugestoes[0];
}

void mousePressed()
{
  if (didMouseClick(width/2 - 2*PPCM, height/2 - 5.1*PPCM, 4.0*PPCM, 2.0*PPCM))                 // Test click on 'accept' button - do not change this!
  {
    nextTrial(); 
    ecra = 1;
    flag = 0;
    letter1 = 'a';                                     //initializes all the letters of the keyWord to the start of the alphabet
    letter2 = 'b';
    letter3 = 'c'; 
    letter4 = 'd';
    letter5 = 'e';
    sugestaoEsquerda = "";
    sugestaoDireita = "";
  }                      
  else if(didMouseClick(width/2 - 2.0*PPCM, height/2 - 1.0*PPCM, 4.0*PPCM, 3.0*PPCM))  // Test click on 'keyboard' area - do not change this condition! 
  {
    // YOUR KEYBOARD IMPLEMENTATION NEEDS TO BE IN HERE! (inside the condition)
    char letter2Next, letter1Next, letter2Before, letter1Before;
    // Test click to go to the right screen reference -> next keyWord according the alphabet
    if (didMouseClick(width/2 + 0.7*PPCM, height/2 + 1.25*PPCM, 1.3*PPCM, 0.8*PPCM))
    {
      letter1+= 5;                                          //display as 5 letter, if you change display letter increments 5 times
      if (letter1 != 'a') ecra = 0;                         // if letter1 not "a" the ecra is not the main
      if (letter1 == 'f') {                                 
        refDireita = "g - k";
        refEsquerda = "";
        flag = 1;                                            // puts the refEsquerda with the button to go the main display
      }
      if (letter1 == 'k' || letter1 == 'p' || letter1 == 'u' || letter1 == 'z') {
        flag = 0;                                            //puts the buttons to go to others display with the normal a appearance
        //letters of the keyWord
        letter2+= 5;                                         
        letter3+= 5;
        letter4+= 5;
        letter5+= 5;
        //changes button to go to the next display
        letter2Next = letter2;                                //letter that appear on the button to the next display ->first letter on display 
        letter1Next = letter1;                                 //letter that appear on the button to the next display ->last letter on display
        letter2Next += 5;                                      //increments to the first letter on the next display
        letter1Next += 5;                                       //increments to the next last letter on the next display
        //changes button to go to the before display
        letter2Before = letter2;                              //letter that appear on the button to the before display ->first letter on display 
        letter1Before = letter1;                               //letter that appear on the button to the before display ->first letter on display 
        letter2Before -= 5;                                  //decrements increments to the first letter on the before display
        letter1Before -= 5;                                   //decrements increments to the first letter on the before display
        if(letter1 == 'z') {                                  //puts the refDireita with the button to go the main display
          flag = 2;
          refDireita = "";
        }
        //puts the buttons to go to the others display in the normal format -> "b-f"/"v-z"
        else {
          flag = 0;
          refDireita = "" + letter2Next + " - " + letter1Next;
        }
        refEsquerda = "" + letter2Before + " - " + letter1Before;
      }
      if (letter1 > 'z') {
        flag = 0;
        letter1 = 'a';                                     //initializes all the letters of the keyWord to the start of the alphabet
        letter2 = 'b';
        letter3 = 'c'; 
        letter4 = 'd';
        letter5 = 'e';
        refEsquerda = "v - z";
        refDireita = "b - f";
        ecra = 1;                                          //goes to the main display again
      }
    }
    // Test click to go to the left screen reference -> before keyWord according the alphabet
    if (didMouseClick(width/2 - 2*PPCM, height/2 + 1.25*PPCM, 1.3*PPCM, 0.8*PPCM))
    {
      letter1 -= 5;
      if (letter1 != 'a') ecra = 0;                      // if letter1 not "a" the display is not the main
      if (letter1 == 'a') {                              // if letter1 is "a" draw the main display
        flag = 0;
        ecra = 1;
        refEsquerda = "v - z";
        refDireita = "b - f";
      }
      if (letter1 == 'k' || letter1 == 'p' || letter1 == 'u' || letter1 == 'z' || letter1 == 'f') {
        flag = 0;
        //letters of the keyWord 
        letter2 -= 5;                                      
        letter3 -= 5;
        letter4 -= 5;
        letter5 -= 5;
        //changes button to go to the before display
        letter2Before = letter2;                              //letter that appear on the button to the before display ->first letter on display 
        letter1Before = letter1;                               //letter that appear on the button to the before display ->first letter on display 
        letter2Before -= 5;                                  //decrements increments to the first letter on the before display
        letter1Before -= 5;                                   //decrements increments to the first letter on the before display
        //changes button to go to the next display
        letter2Next = letter2;                              //letter that appear on the button to the next display ->first letter on display 
        letter1Next = letter1;                                 //letter that appear on the button to the next display ->last letter on display
        letter2Next += 5;                                      //increments to the first letter on the next display
        letter1Next += 5;                                       //increments to the next last letter on the next display
        if(letter1 == 'f') {                                    //if the before button goes to the main
          flag = 1;
          refEsquerda = "";
        }
        //puts the buttons to go to the others display in the normal format -> "b-f"/"v-z"
        else
          refEsquerda = "" + letter2Before + " - " + letter1Before;
        refDireita = "" + letter2Next + " - " + letter1Next;
      }
      if (letter1 < 'a') {
        ecra = 0;                                          //draws the screen that is not the main
        letter1 = 'z';                                    //initializes all the letters of the keyWord to the ending of the alphabet
        letter2 = 'v';
        letter3 = 'w'; 
        letter4 = 'x';
        letter5 = 'y';
        refEsquerda = "q - u";
        refDireita = "";
        flag = 2;
      }
    }
    // Test click on space
    else if (didMouseClick(width/2 - 0.65*PPCM, height/2 + 1.25*PPCM, 1.3*PPCM, 0.8*PPCM))
    {
       currentTyped+=" ";                   // if underscore, consider that a space bar
       currentWord = "";                    // cleans the reference used for the suggestions
       sugestaoEsquerda = "the ";
       sugestaoDireita = "of ";
       
    }
    // Test click on letter1
    else if (didMouseClick(width/2 - 0.65*PPCM, height/2 + 0.5*PPCM, 1.3*PPCM, 0.7*PPCM))
    {
       currentTyped += letter1;  // if not any of the above cases, add the current letter to the typed string*/
       currentWord += letter1;   
       apresentaSugestao(sugestaoEsquerda, sugestaoDireita);      //updates the suggestions
    }
    // Test click on letter2
    else if (didMouseClick(width/2 - 2*PPCM, height/2 - 0.3*PPCM, 1.3*PPCM, 0.8*PPCM))
    {
      if(ecra == 0){  //if not on main/home screen
        currentTyped += letter2;  // if not any of the above cases, add the current letter to the typed string*/
        currentWord += letter2;
        apresentaSugestao(sugestaoEsquerda, sugestaoDireita);       //updates the suggestions
      }
      else{  //if on main/home screen
        if(currentTyped.length() == 0) {
          currentTyped += palavra1;
        }
        else{
          currentTyped = currentTyped.substring(0, currentTyped.length() - currentWord.length());
          currentTyped += palavra1;
          currentWord = "";
        }
      }
    }
    // Test click on letter3
    else if (didMouseClick(width/2 - 0.65*PPCM, height/2 - 0.3*PPCM, 1.3*PPCM, 0.8*PPCM))
    {
      if(ecra == 0){  //if not on main/home screen
        currentTyped += letter3;  // if not any of the above cases, add the current letter to the typed string*/
        currentWord += letter3;
        apresentaSugestao(sugestaoEsquerda, sugestaoDireita);      //updates the suggestions
      }
      else{  //if on main/home screen
        if(currentTyped.length() == 0) {
          currentTyped += palavra2;
        }
        else {
          currentTyped = currentTyped.substring(0, currentTyped.length() - currentWord.length());
          currentTyped += palavra2;
          currentWord = "";
        }
      }
    }
    // Test click on letter4
    else if (didMouseClick(width/2 + 0.7*PPCM, height/2 - 0.3*PPCM, 1.3*PPCM, 0.8*PPCM))
    {
      if(ecra == 0){  //if not on main/home screen
        currentTyped += letter4;  // if not any of the above cases, add the current letter to the typed string*/
        currentWord += letter4;
        apresentaSugestao(sugestaoEsquerda, sugestaoDireita);      //updates the suggestions
      }
       else{  //if on main/home screen
          if(currentTyped.length() == 0) {
            currentTyped += palavra3;
          }
          else {
            currentTyped = currentTyped.substring(0, currentTyped.length() - currentWord.length());
            currentTyped += palavra3;
            currentWord = "";
          }
      }
    }
    // Test click on letter5
    else if (didMouseClick(width/2 - 2*PPCM, height/2 + 0.5*PPCM, 1.3*PPCM, 0.7*PPCM))
    {
      if(ecra == 0){  //if not on main/home screen
        currentTyped += letter5;  // if not any of the above cases, add the current letter to the typed string*/
        currentWord += letter5;
        apresentaSugestao(sugestaoEsquerda, sugestaoDireita);       //updates the suggestions
      }
      else{  //if on main/home screen
        if(currentTyped.length() == 0) {
              currentTyped += palavra4;
        }
        else{
          currentTyped = currentTyped.substring(0, currentTyped.length() - currentWord.length());
          currentTyped += palavra4;
          currentWord = "";
        }
      }
    }
    // Test click on delete
    else if (didMouseClick(width/2 + 0.7*PPCM, height/2 + 0.5*PPCM, 1.3*PPCM, 0.7*PPCM))
    {
      if (currentTyped.length() > 0)                                      // if `, treat that as a delete command
        currentTyped = currentTyped.substring(0, currentTyped.length() - 1);
      if(currentWord.length() > 0) {                                        //deletes the last letter of the word used as refference to the suggestions
        currentWord = currentWord.substring(0, currentWord.length() - 1);
        apresentaSugestao(sugestaoEsquerda, sugestaoDireita);              //updates the suggestions
      }
    }
    // Test click on left suggestion given
    else if (didMouseClick(width/2 - 2*PPCM, height/2 - 0.95*PPCM, 2*PPCM, 0.65*PPCM)){
      currentTyped = currentTyped.substring(0, currentTyped.length() - currentWord.length());
      currentTyped += sugestaoEsquerda + " ";                          //add the suggestion to the phrase typed
      currentWord = "";                                                //cleans the reference used for the suggestions
    }
    // Test click on right suggestion given
    else if (didMouseClick(width/2, height/2 - 0.95*PPCM, 2*PPCM, 0.65*PPCM)){
      currentTyped = currentTyped.substring(0, currentTyped.length() - currentWord.length());
      currentTyped += sugestaoDireita + " ";                           //add the suggestion to the phrase typed
      currentWord = "";                                                // cleans the reference used for the suggestions
    }
  }
  else System.out.println("debug: CLICK NOT ACCEPTED");
}

//saves the information entered on the last trial and checks the number of trials done
void nextTrial()
{
  if (currTrialNum >= NUM_REPEATS) return;                                            // check to see if experiment is done
  
  // Check if we're in the middle of the tests
  else if (startTime != 0 && finishTime == 0)                                         
  {
    System.out.println("==================");
    System.out.println("Phrase " + (currTrialNum+1) + " of " + NUM_REPEATS);
    System.out.println("Target phrase: " + currentPhrase);
    System.out.println("Phrase length: " + currentPhrase.length());
    System.out.println("User typed: " + currentTyped);
    System.out.println("User typed length: " + currentTyped.length());
    System.out.println("Number of errors: " + computeLevenshteinDistance(currentTyped.trim(), currentPhrase.trim()));
    System.out.println("Time taken on this trial: " + (millis() - lastTime));
    System.out.println("Time taken since beginning: " + (millis() - startTime));
    System.out.println("==================");
    lettersExpectedTotal += currentPhrase.trim().length();
    lettersEnteredTotal += currentTyped.trim().length();
    errorsTotal += computeLevenshteinDistance(currentTyped.trim(), currentPhrase.trim());
    typed[currTrialNum] = currentTyped;
  }
  
  // Check to see if experiment just finished
  if (currTrialNum == NUM_REPEATS - 1)                                           
  {
    finishTime = millis();
    System.out.println("==================");
    System.out.println("Trials complete!"); //output
    System.out.println("Total time taken: " + (finishTime - startTime));
    System.out.println("Total letters entered: " + lettersEnteredTotal);
    System.out.println("Total letters expected: " + lettersExpectedTotal);
    System.out.println("Total errors entered: " + errorsTotal);

    float wpm = (lettersEnteredTotal / 5.0f) / ((finishTime - startTime) / 60000f);   // FYI - 60K is number of milliseconds in minute
    float freebieErrors = lettersExpectedTotal * .05;                                 // no penalty if errors are under 5% of chars
    float penalty = max(0, (errorsTotal - freebieErrors) / ((finishTime - startTime) / 60000f));
    float cps = (lettersEnteredTotal) / ((finishTime - startTime) / 1000f);           // calculates the cps
    
    System.out.println("Raw WPM: " + wpm);
    System.out.println("Freebie errors: " + freebieErrors);
    System.out.println("Penalty: " + penalty);
    System.out.println("WPM w/ penalty: " + (wpm - penalty));                         // yes, minus, because higher WPM is better
    System.out.println("CPS: " + cps);
    System.out.println("==================");
    
    printResults(wpm, freebieErrors, penalty, cps);
    
    currTrialNum++;                                                                   // increment by one so this mesage only appears once when all trials are done
    return;
  }

  else if (startTime == 0)                                                            // first trial starting now
  {
    System.out.println("Trials beginning! Starting timer...");
    startTime = millis();                                                             // start the timer!
  } 
  else currTrialNum++;                                                                // increment trial number

  lastTime = millis();                                                                // record the time of when this trial ended
  currentTyped = "";                                                                  // clear what is currently typed preparing for next trial
  currentPhrase = phrases[currTrialNum];                                              // load the next phrase!
}

// Print results at the end of the study
void printResults(float wpm, float freebieErrors, float penalty, float cps)
{
  background(0);       // clears screen
  
  textFont(createFont("Arial", 16));    // sets the font to Arial size 16
  fill(255);    //set text fill color to white
  text(day() + "/" + month() + "/" + year() + "  " + hour() + ":" + minute() + ":" + second(), 100, 20);   // display time on screen
  
  text("Finished!", width / 2, height / 2);
  
  int h = 20;
  for(int i = 0; i < NUM_REPEATS; i++, h += 40 ) {  //saves and helps compare the phases written and given
    text("Target phrase " + (i+1) + ": " + phrases[i], width / 2, height / 2 + h);
    text("User typed " + (i+1) + ": " + typed[i], width / 2, height / 2 + h+20);
  }
  
  text("Raw WPM: " + wpm, width / 2, height / 2 + h+20);
  text("Freebie errors: " + freebieErrors, width / 2, height / 2 + h+40);
  text("Penalty: " + penalty, width / 2, height / 2 + h+60);
  text("WPM with penalty: " + max((wpm - penalty), 0), width / 2, height / 2 + h+80);
  text("CPS: " + cps, width / 2, height / 2 + h+100);
  
  saveFrame("results-######.png");    // saves screenshot in current folder    
}

// This computes the error between two strings (i.e., original phrase and user input)
int computeLevenshteinDistance(String phrase1, String phrase2)
{
  int[][] distance = new int[phrase1.length() + 1][phrase2.length() + 1];

  for (int i = 0; i <= phrase1.length(); i++) distance[i][0] = i;
  for (int j = 1; j <= phrase2.length(); j++) distance[0][j] = j;

  for (int i = 1; i <= phrase1.length(); i++)
    for (int j = 1; j <= phrase2.length(); j++)
      distance[i][j] = min(min(distance[i - 1][j] + 1, distance[i][j - 1] + 1), distance[i - 1][j - 1] + ((phrase1.charAt(i - 1) == phrase2.charAt(j - 1)) ? 0 : 1));

  return distance[phrase1.length()][phrase2.length()];
}
