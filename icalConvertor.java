


	/*
	 * Converts the output from the fet timetabling program to ical format
	* for importation into Google Calendar
	* Version 0.7 - Auto generation of UID field working.
	* Richard Bolger
	* 
	 Sample input file:
	56,20171118,111500,MS00B,MS00B,Dara Cummins,F2F,CG86,
	57,20171125,111500,MSA,MSA,Grace Kenny,F2F,QG02,
	  
	 * Sample output file
	sample ical file:

		BEGIN:VCCALENDAR
		BEGIN:VEVENT
		SUMMARY:HS1G2 F2F in X201
		UID:20170506T0339Z-55@dcu.ie
		DTSTART:20171014T080000Z
		DTEND:20171014T93000Z
		LOCATION:Online Classroom
		END:VEVENT
		END:VCALENDAR
		
	 */

	import java.io.File;
	import java.util.Calendar;
	import java.util.Scanner;

	public class icalConvertor
	{
		public static void main(String[] args) throws Exception
		{
			if(args.length!=1)
				{
				System.out.println("Usage java -cp. icalconvertor fetoutput.csv");
				System.exit (0);
				}
			File myFile = new File(args[0]);
			//System.out.println(args[0]);
			try
			{
				Scanner in = new Scanner(myFile);
				int x = 0; // counter variable to update UID
				String line = "";
				String location = "";
				//final int TZOFFSET=10000;  // gcal defaults to UTC  
				final int TZOFFSET=00000;  // gcal defaults to UTC  
				
				final int ID = 0;
				final int DATE = 1; //
				final int TIME = 2; // format of the csv output from Fet
				final int SUBJECT = 3; //
				final int SUBGROUP = 4;
				final int TUTOR = 5;
				final int CLTYPE = 6;
				final int ROOM = 7;
				int time = 0;
				int classLength = 90; // most classes are 90 minutes
				Calendar now = Calendar.getInstance();
				String classType = "";
				System.out.println("BEGIN:VCALENDAR");
				String uid = String.format("%d%02d%02dT%02d%02dZ", now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1,
						now.get(Calendar.DAY_OF_MONTH) + 1, now.get(Calendar.HOUR), now.get(Calendar.MINUTE));
				System.out.println(uid + "= UID");

				while (in.hasNextLine())
				{
					System.out.println("BEGIN:VEVENT");
					line = in.nextLine();
					x++; // need this to create unique UIDs
					String[] words = line.split(",");
				
					if (words[CLTYPE].equals("F2F"))
					{
						//System.out.println("SUMMARY:" + words[SUBJECT] + " F2F in " + words[ROOM]);
						  System.out.println("SUMMARY:" + words[SUBJECT] + " F2F in TBC" + " with " + words[TUTOR]);
							classType="F2F";
							location = "Campus Classroom";
						//System.out.println("SUMMARY:" + words[SUBJECT] + " F2F  " );
					}
					if (words[CLTYPE].equals("OLC"))
					{
						location = "Online Classroom";
						classType="OLC";
						System.out.println("SUMMARY:" + words[SUBJECT] + " Online Class"+ " with " + words[TUTOR]);
					}
					time = Integer.parseInt(words[TIME]);
					System.out.println("UID:" + uid + "-" + x + "@dcu.ie");
					System.out.printf("DTSTART:%s%s%06d%s\n", words[DATE], "T", time - TZOFFSET, "Z"); // -10000 is a hack to fix IST +1
					classLength = getClassLength(time, classType); // need to give the start time to cater for hour overflow
					classLength = getClassLength(time, classType); // need to give the start time to cater for hour overflow
					System.out.println("DTEND:" + words[DATE] + "T" + (time + classLength - TZOFFSET) + "Z"); // gcal 
					System.out.println("LOCATION:" + location);
					System.out.println("ATTENDEE:" + words[TUTOR]);
					System.out.println("END:VEVENT");
				}
				System.out.println("END:VCALENDAR");

				// out.close();
				in.close();
			} catch (Exception e)
			{
				System.out.println("Can't locate that file");
			}
		}

		public static int getClassLength(int startTime, String classType)
		{
			final int OLCLENGTH = 13000; // hour and a half
			final int F2FLENGTH = 20000; // two hours
			int classLength = OLCLENGTH;
			//System.out.println(classType+"_______________________");
			if (classType.equals("F2F"))
			{
				classLength = F2FLENGTH; // 2 hrs 0 mins used to calc DTEND
			}
			int hours = startTime / 10000;
			int mins = (startTime - (hours * 10000)) / 100;
			if (mins >= 30 && classLength == 13000)
				classLength = 17000; // e.g. 10:45 +90 mins = 12:15 1045+170=1215
			return classLength;

		}
	}


