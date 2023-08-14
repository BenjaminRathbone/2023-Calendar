//Programmer: Ben Rathbone
//CS 141
//Date: 6-15-23
//Assignment 3: Calendar Part 3               
//Purpose: A program that can display calendars for any month in 2023
//         Can display user-inputted date, current date, or the next / previous calendar
//         Reads and displays events from calendarEvent.txt
//         User can input their own events as well
//         User can print calendar to a file
//Bugs: Depending on the user input, sometimes an invalid mm/dd format will return the
//      "Invalid Date" error message instead.
//      Also, the user can use any character instead of a "/" in the mm/dd format

import java.util.*;  //imports Scanner
import java.io.*;    //imports File and PrintStream

public class BRCalendar
{
   public static String[][] events = new String[12][];  //creates global array "events"

	public static void main(String[] args) throws FileNotFoundException
	{
      Scanner console = new Scanner(System.in); //creates Scanner object
      
      //scale for the calendar graphics (min. 5)
      //event title character length = scale - 2
      int scale = 18;
      
      //initializes the events array
      initializeEvents(scale);
      
      //the menu will use this int to keep track of the most recently displayed month
      int menuMonth = 0;
      
      //user menu
      while (true)
      {
         //displays menu text
         menuText();
         
         //accepts user input
         String userInput;
         do
         {
            userInput = console.nextLine();   //saves input as String "userInput"
            userInput = userInput.toUpperCase();  //converts String to uppercase
         } while (userInput.isEmpty());   //ensures userInput is not empty
         
         //performs action based on user input
         if (userInput.equals("E"))   //display user-inputted month and date
         {                       //updates menuMonth to user-inputted month
            menuMonth = drawUserDate(console, scale);
         }
         else if (userInput.equals("T")) //display the current month and date
         {                          //updates menuMonth to current month
            menuMonth = drawToday(scale);
         }
         else if (userInput.equals("N")) //display next month
         {
            if (menuMonth == 0)  //if no calendar has been displayed
            {
               System.out.println("You must have a calendar displayed first!");
            }
            else  //if a calendar has been displayed
            {
               if (menuMonth == 12) //if menuMonth is December
               {                    //display January calendar and update menuMonth
                  menuMonth = drawMonth(1, 0, scale, System.out);  
               }
               else  //if any other month
               {     //display next month and update menuMonth
                  menuMonth = drawMonth(menuMonth + 1, 0, scale, System.out);
               }
            }
         }
         else if (userInput.equals("P")) //display previous month
         {
            if (menuMonth == 0)  //if no calendar has been displayed
            {
               System.out.println("You must have a calendar displayed first!");
            }
            else  //if a calendar has been displayed
            {
               if (menuMonth == 1) //if menuMonth is January
               {                   //display December calendar and update menuMonth
                  menuMonth = drawMonth(12, 0, scale, System.out);  
               }
               else  //if any other month
               {     //display next month and update menuMonth
                  menuMonth = drawMonth(menuMonth - 1, 0, scale, System.out);
               }
            }
         }
         else if (userInput.equals("EV")) //add an event
         {
            createEvent(console, scale);
         }
         else if (userInput.equals("FP")) //print calendar to file
         {
            printToFile(console, scale);
         }
         else if (userInput.equals("Q"))   //quit program
         {
            System.out.println("Have a nice day!");
            break;   //breaks user menu while loop
         }
         else  //invalid input
         {
            System.out.println("Invalid input.  Please try again.");
         }
      }//end of menu while loop       
	}//end of main method
   
   //dislays a user-inputted month and date
   //accepts the scanner and scale
   public static int drawUserDate(Scanner console, int scale)
   {
      int userMonth;
      int userDay;     
      do
      {
         //promts the user for mm/dd
         System.out.println("What date would you like to look at? (mm/dd)");
         String date = console.next();
      
         //converts user input into integers
         userMonth = monthFromDate(date);
         userDay = dayFromDate(date);      
      } while (!checkDate(userMonth, userDay)); //calls the checkDate method to check validity 
      
      //calls the drawMonth method to display user-inputted month
      drawMonth(userMonth, userDay, scale, System.out);
      
      //returns user-inputted month
      return userMonth;
   }  
   
   //displays the current month and date
   //accepts scale
   public static int drawToday(int scale)
   {
      Calendar currentDate = Calendar.getInstance(); //creates a Calendar object
      
      //stores current month and day as integers
      int currentMonth = currentDate.get(Calendar.MONTH) + 1;
      int currentDay = currentDate.get(Calendar.DATE);
      
      //calls the drawMonth method to display current month
      drawMonth(currentMonth, currentDay, scale, System.out);
      
      //returns the current month
      return currentMonth;
   }
   
   //prints a user-specified month to an output file
   //accepts console and scale
   public static void printToFile(Scanner console, int scale) throws FileNotFoundException
   {
      int fileMonth;
      while (true)
      {
         System.out.print("What month would you like to print?: ");
         try
         {
            fileMonth = console.nextInt();
         }
         catch (InputMismatchException e)  //if format is invalid
         {
            fileMonth = 0;
            console.next();
         }
         
         if (fileMonth >= 1 && fileMonth <= 12) //if month is valid
         {
            break;
         }
         else  //if month is invalid
         {
            System.out.println("Invalid input.  Please try again.");
         } 
      }
      
      System.out.print("Output file name: ");   //prompts user for output file name
      while (true)
      {
         String filename = console.nextLine();
         if (!filename.isEmpty())
         {
            File outputFile = new File(filename);  //creates new file object with that name
            PrintStream fileOutput = new PrintStream(outputFile); //creates PrintStream for output file
            drawMonth(fileMonth, 0, scale, fileOutput);  //calls drawMonth method, uses PrintStream
            System.out.println("Calendar successfully printed to " + outputFile + "!\n");
            break;
         }
      }
   }//end of printToFileMethod
   
   //draws a calendar month
   //accepts month, day, scale, and output
   public static int drawMonth(int month, int day, int scale, PrintStream output)
   { 
      //calls the drawArt method to display ASCII art
      drawArt(scale, output);
      
      //prints spaces before month number
      for (int i = 1; i <= (scale * 3) + (scale / 2); i++)
      {
         output.print(" ");
      }
      output.println(month); //prints month number
      
      int boxDay = getBoxDay(month); //sets the "boxDay" int (see method description)
      int daysInMonth = getDaysInMonth(month); //sets the # of days in the month
      
      while (boxDay <= daysInMonth) //prints calendar rows
      { 
         boxDay = drawRow(month, day, boxDay, daysInMonth, scale, output);
      }
      
      for (int k = 1; k <= scale * 7 + 1; k++)  //prints === (bottom line of calendar)
      {
         output.print("=");
      } 
      output.println();
      
      //calls the displayDate method for user-inputted date
      displayDate(month, day, output);
      
      return month;
   }//end of drawMonth method
   
   //draws one row of the calendar
   //accepts month, user-inputted day (int day), starting day for the row (boxDay)
      //# of days in month, scale, and output
   public static int drawRow(int month, int day, int boxDay,
                             int daysInMonth, int scale, PrintStream output)
   {
      int eventDay;
      int eventLength = 0;
      
      for (int i = 1; i <= scale * 7 + 1; i++)  //prints ===
      {
         output.print("=");
      }
      output.println();
         
      for (int j = 1; j <=7; j++)   //prints the first line of boxes
      {
         if (boxDay >= 1 && boxDay <= daysInMonth) //prints box with day number
         {  
            output.print("| " + boxDay);
            int boxDayLength = String.valueOf(boxDay).length();
            
            if (boxDay == day)   //marks the user-specified date
            {
               output.print("*");
            }
            else
            {
               output.print(" ");
            }
         
            if (boxDayLength == 1)
            {
               for (int k = 1; k <= scale - 4; k++)
               {
                  output.print(" ");
               }
            }
            else if (boxDayLength == 2)
            {
               for (int l = 1; l <= scale - 5; l++)
               {
                  output.print(" ");
               }
            }
            boxDay++;
         }
         else  //prints empty box
         {
            output.print("|");
            for (int m = 1; m <= scale - 1; m++)
               {
                  output.print(" ");
               }
            boxDay++;
         }
      }
      output.print("|");  //prints final | of the first line
      output.println();
      
      for (int o = 1; o <= 7; o++)  //prints the second line of boxes
      {     
         output.print("| ");
         eventDay = boxDay - 8 + o; //sets this box's eventDay = it's boxDay
         
         if (eventDay >= 1 && eventDay <= daysInMonth)   //if this is a box with a day
         {     
            try
            {
               if (!events[month - 1][eventDay - 1].isEmpty()) //if there is an event
               {
                  output.print(events[month - 1][eventDay - 1].replace("_"," ")); //print it
                  eventLength = events[month - 1][eventDay - 1].length();
               }
            }
            catch (NullPointerException e)   //if there is not an event
            {
               eventLength = 0;
            }
         }
         else  //if this is not a box with a day
         {
            eventLength = 0;
         }
         
         for (int p = 1; p <= scale - 2 - eventLength; p++) //prints empty spaces
         {
            output.print(" ");
         }
      }
      output.print("|"); // prints final | of the second line
      output.println();
      
      for (int n = 1; n <= scale / 2 - 2; n++)  //prints subsequent lines of boxes
      {
         for (int o = 1; o <= 7; o++)
         {
            output.print("|");
            for (int p = 1; p <= scale - 1; p++)
            {
               output.print(" ");
            }
         }
         output.print("|"); // prints final | of the subsequent lines
         output.println();
      }
      return boxDay;
   }//end of the drawRow method
   
   //draws ASCII art
   //accepts scale and output
   public static void drawArt(int scale, PrintStream output)
   {
      int artScale = (scale * 3) + (scale / 2) - 19; //this value aligns the art to the center
      if (artScale < 0) //prevents the artScale from being lower than 0
      {                 //things get a little screwy otherwise
         artScale = 0;  
      }                 
      
      for (int i = 1; i <= 6; i++)
      {
         if (i == 1 || i == 6)
         {
            for (int j = 1; j <= artScale + 4; j++)
            {
               output.print(" ");
            }
            for (int k = 1; k <= 3; k++)
            {
               output.print("+");
            }
         }
         else if (i == 2 || i == 5)
         {
            for (int l = 1; l <= artScale + 4; l++)
            {
               output.print(" ");
            }
            for (int m = 1; m <= 3; m++)
            {
               output.print("+");
            }
            for (int n = 1; n <= 21; n++)
            {
               output.print(" ");
            }
            for (int o = 1; o <= 11; o++)
            {
               output.print("=");
            }
         }
         else if (i == 3 || i == 4)
         {
            for (int p = 1; p <= artScale; p++)
            {
               output.print(" ");
            }
            for (int q = 1; q <= 11; q++)
            {
               output.print("+");
            }
            for (int r = 1; r <= 3; r++)
            {
               output.print(" ");
            }
            for (int s = 1; s <= 11; s++)
            {
               output.print("-");
            }//end of if statement
         }
         output.println();
      }//end of for loop
   }//end of the drawArt method
   
   //displays month and day
   //accepts month and day
   public static void displayDate(int month, int day, PrintStream output)
   {
      output.println("Month: " + month);
      if (day != 0)  // if day is 0, don't print day
      {
         output.println("Day: " + day);
      }
      output.printf("%n---%n%n");
   }//end of displayDate method
   
   //prompts the user for an event to be added to the events array
   //accepts console and scale
   public static void createEvent(Scanner console, int scale)
   {
      int eventMonth;
      int eventDay;
      String eventTitle;
      
      do
      {
         while (true)
         {
            System.out.println("Please enter an event. (mm/dd event_title)"); //prompts user
            String userEvent = console.nextLine();
            String[] userEventSplit = userEvent.split(" "); //splits input into 2 strings
            
            if (userEventSplit.length == 2)  //makes sure format is valid
            {
               if (userEventSplit[1].length() <= scale - 2) //if char limit is ok
               {
                  eventMonth = monthFromDate(userEventSplit[0]);  //sets month, day, and title
                  eventDay = dayFromDate(userEventSplit[0]);
                  eventTitle = userEventSplit[1];
                  break;
               }
               else  //if string is too long
               {
                  System.out.println("Please keep event title to " + (scale - 2) +
                                     " characters or less.");
               }
            }
            else  //if format is not valid
            {
               System.out.println("Invalid Input.  Please try again.");
            }
         }
      }while (!checkDate(eventMonth, eventDay));   //calls the checkDate method
      
      events[eventMonth - 1][eventDay - 1] = eventTitle; //adds event to the array
      
      System.out.println("Event successfully created!\n");
   }//end of createEvent method
   
   //checks if date is valid
   //accpets month and day
   public static boolean checkDate(int month, int day)
   {
      //sets checks to false
      boolean monthCheck = false;
      boolean dayCheck = false;
      
      //checks if userMonth and userDay are valid
      if (month >= 1 && month <= 12) //checks month
      {
         monthCheck = true;
      }
      if (day >= 1 && day <= getDaysInMonth(month))  //checks day
      {
         dayCheck = true;
      }
      
      if (monthCheck && dayCheck) //if date is valid
      {
         return true;   //break user input while loop
      }
      else if (month == 999 || day == 999) //if format is invalid
      {
         System.out.println("Invalid Format.  Date must be in mm/dd format");
         return false;
      }
      else  //if date is invalid
      {
         System.out.println("Invalid Date.  Please try again.");
         return false;
      }
   }//end of checkDate method
   
   //displays menu text explaining the functions to the user
   public static void menuText()
   {
      System.out.println("Please type a command:");
         System.out.println("   \"e\" to enter a date and display the corresponding calendar");
         System.out.println("   \"t\" to display today's calendar");
         System.out.println("   \"n\" to display the next month");
         System.out.println("   \"p\" to display the previous month");
         System.out.println("   \"ev\" to add a new event");
         System.out.println("   \"fp\" to enter a month and print the calendar to a file");
         System.out.println("   \"q\" to quit the program");
   }
   
   //initializes the global events array
   //sets the correct length for subarrays and imports events from file
   //accepts scale
   public static void initializeEvents(int scale) throws FileNotFoundException
   {
      //sets the proper length for each subarray
      for (int i = 0; i < 12; i++)
      {
         events[i] = new String[getDaysInMonth(i + 1)];  
      }
      
      //reads calendarEvents.txt and adds events to array
      int eventMonth;
      int eventDay;
      String eventTitle;
      
      File eventFile = new File("calendarEvents.txt"); //creates file object
      Scanner input = new Scanner(eventFile);         //creates Scanner for file
      while (input.hasNextLine())
      {
         String fileEvent = input.nextLine();
         String[] fileEventSplit = fileEvent.split(" "); //splits input into 2 strings
         if (fileEventSplit.length == 2)  //makes sure format is valid
            {
               eventMonth = monthFromDate(fileEventSplit[0]);  //sets month
               eventDay = dayFromDate(fileEventSplit[0]);      //sets day
               if (eventMonth >= 1 && eventMonth <= 12 &&      //if date is valid
                   eventDay >= 1 && eventDay <= getDaysInMonth(eventMonth))
               {
                  if (fileEventSplit[1].length() <= scale - 2) //if char limit is ok
                  {   
                     eventTitle = fileEventSplit[1]; //sets title
                  }
                  else  //if string is too long
                  {
                     eventTitle = fileEventSplit[1].substring(0, scale - 2);  //shortens title
                  }
                  
                  events[eventMonth - 1][eventDay - 1] = eventTitle; //adds event to array
               }
            }
      }
   }//end of intializeEvents method
   
   //returns int "boxDay", which has 2 uses
   //1. Determines which weekday the 1st of the month is on (2023)
   //2. Determines which date is printed in each box of the calendar
   public static int getBoxDay(int month)
   {
      int boxDay;
      switch (month)
      {
         case 1:  boxDay = 1;
                  break;
         case 2:  boxDay = -2;
                  break;
         case 3:  boxDay = -2;
                  break;
         case 4:  boxDay = -5;
                  break;
         case 5:  boxDay = 0;
                  break;
         case 6:  boxDay = -3;
                  break;
         case 7:  boxDay = -5;
                  break;
         case 8:  boxDay = -1;
                  break;
         case 9:  boxDay = -4;
                  break;
         case 10:  boxDay = 1;
                  break;
         case 11:  boxDay = -2;
                  break;
         case 12:  boxDay = -4;
                  break;
         default: boxDay = 1;
                  break;                                                              
      }
      return boxDay;
   }
   
   //determines the number of days in the month
   public static int getDaysInMonth(int month)
   {
      int daysInMonth;
      if (month == 2)
      {
         daysInMonth = 28;
      }
      else if (month == 4 || month == 6 || month == 9 || month == 11)
      {
         daysInMonth = 30;
      }
      else
      {
         daysInMonth = 31;
      }
      return daysInMonth;
   }
   
   //converts user-inputted date into month integer
   public static int monthFromDate(String date)
   {
      try
      {
         return Integer.parseInt(date.substring(0,2));
      }
      catch (NumberFormatException e)  //if format is invalid
      {
         return 999;
      }
   }//end of monthFromDate method
   
   //converts user-inputted date into day integer
   public static int dayFromDate(String date) 
   {
      try
      {
         return Integer.parseInt(date.substring(3));
      }
      catch (NumberFormatException e)  //if format is invalid
      {
         return 999;
      }
   }//end of monthFromDate method  
}//end of program