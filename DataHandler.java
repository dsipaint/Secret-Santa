import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Random;

public class DataHandler
{
	static final String FILE_LOC = "names.dat";
	
	//use when shutting down the bot or when shuffling
	public static void writeNames(ArrayList<String> names)
	{
		try
		{
			PrintWriter pw = new PrintWriter(new FileWriter(new File(FILE_LOC)));
			names.forEach(pw::println);
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
	
	@SuppressWarnings("unchecked")
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
	
	public static String getNextEntryWrap(ArrayList<String> list, int index)
	{
		if(index == list.size() - 1) //wrap entries around to start if at end
			return list.get(0);
		
		return list.get(index + 1); //otherwise get next entry
	}
}
