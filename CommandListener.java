import java.util.ArrayList;
import java.util.Collections;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
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
						e.getGuild().retrieveMemberById(DataHandler.getNextEntryWrap(santalist, santalist.indexOf(id)))
						.queue(member ->
						{
							String name = member.getUser().getName();
							channel.sendMessage("Your secret santa is " + name
								+ "! Remember the secret santa rules and your secret santa's entry in the doc pinned in mod chat!")
								.queue();
						});
					});
				});
				
				return;
			}
			
			//shows a user in the list who their secret santa is
			if(args[0].equalsIgnoreCase(Main.PREFIX + "secretsanta"))
			{
				e.getAuthor().openPrivateChannel().queue(channel ->
				{
					e.getGuild().retrieveMemberById(DataHandler.getNextEntryWrap(santalist, santalist.indexOf(e.getAuthor().getId())))
					.queue(member ->
					{
						String name = member.getUser().getName();
						channel.sendMessage("Your secret santa is " + name
								+ "! Remember the secret santa rules and your secret santa's entry in the doc pinned in mod chat!")
						.queue();
					});
				});
				return;
			}
		}
	}
}
