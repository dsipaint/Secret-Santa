import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.api.entities.User;

public class DataHandler
{
	static final String FILE_LOC = "names.dat";
	
	//use when shutting down the bot or when shuffling
	public static void writeNames(ArrayList<String> names)
	{
		try
		{
			PrintWriter pw = new PrintWriter(new FileWriter(new File(FILE_LOC)));
			names.forEach(id ->
			{
				pw.println(id);
			});
			pw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public static ArrayList<String> readNames()
	{
		ArrayList<String> names = new ArrayList<String>();
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File(FILE_LOC)));
			
			String line;
			while((line = br.readLine()) != null)
				names.add(line);
			
			br.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		return names;
	}
	
	public static ArrayList<String> shuffle(ArrayList<String> list)
	{
		ArrayList<String> listcopy  = new ArrayList<String>();
		for(String name : list)
			listcopy.add(name);
		
		ArrayList<String> shuffledlist = new ArrayList<String>(listcopy.size()); //empty list of same size
		Random r = new Random();
		
		while(listcopy.size() > 0)
			shuffledlist.add(listcopy.remove(r.nextInt(listcopy.size()))); //remove random item from old list, add to new list
		
		return shuffledlist;
	}
	
	//returns user that [id] is supposed to give a gift TO
	public static User secretSantaGiftTo(ArrayList<String> list, String id) throws InterruptedException, ExecutionException
	{
		return Main.jda.retrieveUserById(getNextEntryWrap(list, list.indexOf(id))).submit().get();
	}
	
	//returns user that [id] is supposed to get a gift FROM
	public static User secretSantaGiftFrom(ArrayList<String> list, String id) throws InterruptedException, ExecutionException
	{
		return Main.jda.retrieveUserById(getPreviousEntryWrap(list, list.indexOf(id))).submit().get();
	}
	
	//the NEXT entry is the person who is receiving the gift from person [index]
	public static String getNextEntryWrap(ArrayList<String> list, int index)
	{
		if(index == list.size() - 1) //wrap entries around to start if at end
			return list.get(0);
		
		return list.get(index + 1); //otherwise get next entry
	}
	
	//the PREVIOUS entry is the person who is giving the gift to person [index]
	public static String getPreviousEntryWrap(ArrayList<String> list, int index)
	{
		if(index == 0) //wrap entries around to end if at start
			return list.get(list.size() - 1);
		
		return list.get(index + -1); //otherwise get previous entry
	}
}
