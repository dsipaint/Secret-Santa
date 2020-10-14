import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class CommandListener extends ListenerAdapter
{
	static ArrayList<String> santalist;
	
	public CommandListener()
	{
		//generate list here
		if((santalist = DataHandler.readNames()).isEmpty())
		{
			santalist = new ArrayList<String>();
			Collections.addAll(santalist, Main.MEMBERS); //add members to list
		}
	}
	
	public void onGuildMessageReceived(GuildMessageReceivedEvent e)
	{
		if(StopListener.isStaff(e.getMember()))
		{
			String msg = e.getMessage().getContentRaw();
			String[] args = msg.split(" ");
			
			//shuffles list and sends everyone their secret santa
			if(args[0].equalsIgnoreCase(Main.PREFIX + "secretsantashuffle"))
			{
				santalist = DataHandler.shuffle(santalist);
				for(int i = 0; i < santalist.size(); i++)
					System.out.println(santalist.get(i));
				
				DataHandler.writeNames(santalist); //save the shuffle
				
				//DM everyone with their secret santa's name
				santalist.forEach(id ->
				{
					e.getGuild().retrieveMemberById(id).complete().getUser().openPrivateChannel().queue(channel ->
					{
						String name = null;
						try
						{
							name = DataHandler.secretSantaGiftTo(santalist, id).getName();
						}
						catch (InterruptedException | ExecutionException e1)
						{
							e1.printStackTrace();
						}
						
						channel.sendMessage("Your secret santa is " + name
								+ "! Remember the secret santa rules and your secret santa's entry in the doc pinned in mod chat!")
								.queue();
					});
				});
				
				return;
			}
		}
	}
	
	public void onPrivateMessageReceived(PrivateMessageReceivedEvent e)
	{
		String msg = e.getMessage().getContentRaw();
		String[] args = msg.split(" ");
		
		//shows a user in the list who their secret santa is
		if(args[0].equalsIgnoreCase(Main.PREFIX + "secretsanta"))
		{
			String name = null;
			try
			{
				name = DataHandler.secretSantaGiftTo(santalist, e.getAuthor().getId()).getName();
			}
			catch (InterruptedException | ExecutionException e1)
			{
				e1.printStackTrace();
			}
			
			e.getChannel().sendMessage("Your secret santa is " + name
					+ "! Remember the secret santa rules and your secret santa's entry in the doc pinned in mod chat!")
			.queue();

			return;
		}
		
		//allows someone to ask their secret santa for their address to send a gift
		if(args[0].equalsIgnoreCase(Main.PREFIX + "requestaddress"))
		{
			//send to NEXT member in list
			try
			{
				DataHandler.secretSantaGiftTo(santalist, e.getAuthor().getId()).openPrivateChannel()
					.queue(channel ->
					{
						channel.sendMessage("Your secret santa is requesting your address so that they can send your gift to you!"
								+ " To send the address to your secret santa, use **" + Main.PREFIX + "sendaddress {your address}** to send your address "
										+ "anonymously to your secret santa- please also remember that you are under no obligation to do this"
										+ " if you don't feel comfortable with it, and that my code will NOT and will NEVER store your address. If"
										+ " you would like to check this yourself, feel free to view the sourcecode for this project: "
										+ "https://github.com/dsipaint/Secret-Santa").queue();
					});
			}
			catch (InterruptedException | ExecutionException e1)
			{
				e1.printStackTrace();
			}
			
			return;
		}
		
		//allows someone to ask their secret santa for their address to send a gift
		if(args[0].equalsIgnoreCase(Main.PREFIX + "sendaddress"))
		{
			//actually send an address
			if(args.length == 1)
				return;
			
			//send to PREVIOUS member in list
			try
			{
				DataHandler.secretSantaGiftFrom(santalist, e.getAuthor().getId()).openPrivateChannel()
					.queue(channel ->
					{
						String addr = "";
						for(int i = 1; i < args.length; i++)
							addr += args[i] + " ";
						
						addr = addr.substring(0, addr.length() - 1);
						
						channel.sendMessage("Your secret santa's address is: " + addr).queue();
					});
				
				e.getChannel().sendMessage("Your address has been sent to your secret santa gifter anonymously. Please also remember "
						+ "that my code does NOT and will NEVER store your address anywhere. If you'd like to check this yourself, I "
						+ "encourage you to check the sourcecode for this project:"
						+ "https://github.com/dsipaint/Secret-Santa").queue();
			}
			catch (InterruptedException | ExecutionException e1)
			{
				e1.printStackTrace();
			}
	}
	}
		
}
