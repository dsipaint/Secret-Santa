import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

import net.dv8tion.jda.api.EmbedBuilder;
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
								+ "! Remember the secret santa rules and your secret santa's entry in the doc pinned in mod chat, and use " + Main.PREFIX + "sshelp in DMs for help!")
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
			
			e.getChannel().sendMessage("You are buying for " + name
					+ "! Remember the secret santa rules and your person's entry in the doc pinned in mod chat, and use " + Main.PREFIX + "sshelp in DMs for help!")
			.queue();

			return;
		}
		
		//secret santa mail
		if(args[0].equalsIgnoreCase(Main.PREFIX + "ssm"))
		{
			if(args.length == 1)
				return;
			
			// ^ssm give
			if(args[1].equalsIgnoreCase("give"))
			{
				if(args.length == 2)
				{
					e.getChannel().sendMessage("You must provide a message!").queue();
					return;
				}
				
				//send message to person the author is giving a gift to
				try
				{
					DataHandler.secretSantaGiftTo(santalist, e.getAuthor().getId()).openPrivateChannel().queue(channel ->
					{
						//message concatenation
						String ssmessage = "";
						for(int i = 2; i < args.length; i++)
							ssmessage += args[i] + " ";
						
						//send message
						EmbedBuilder message_embed = new EmbedBuilder()
								.setTitle("Secret Santa")
								.setAuthor("Person you are receiving a gift FROM -> you:")
								.setDescription(ssmessage)
								.setColor(65280);
						
						channel.sendMessage(message_embed.build()).queue();
						e.getChannel().sendMessage(message_embed.setAuthor("you -> Person you are giving a gift TO (" + channel.getName() + ")", 
								null, channel.getUser().getAvatarUrl()).build()).queue();
					});
				}
				catch (InterruptedException | ExecutionException e1)
				{
					e1.printStackTrace();
				}
				
				return;
			}
			
			// ^ssm receive
			if(args[1].equalsIgnoreCase("receive"))
			{
				if(args.length == 2)
				{
					e.getChannel().sendMessage("You must provide a message!").queue();
					return;
				}
				
				//send message to person the author is receiving a gift from
				try
				{
					DataHandler.secretSantaGiftFrom(santalist, e.getAuthor().getId()).openPrivateChannel().queue(channel ->
					{
						//message concatenation
						String ssmessage = "";
						for(int i = 2; i < args.length; i++)
							ssmessage += args[i] + " ";
						
						//send message
						EmbedBuilder message_embed = new EmbedBuilder()
								.setTitle("Secret Santa")
								.setAuthor("Person you are giving a gift TO (" + e.getAuthor().getName() + ")  -> you:"
										,null, e.getAuthor().getAvatarUrl())
								.setDescription(ssmessage)
								.setColor(65280);
						
						channel.sendMessage(message_embed.build()).queue();
						e.getChannel().sendMessage(message_embed.setAuthor("you -> Person you are receiving a gift FROM").build()).queue();
					});
				}
				catch (InterruptedException | ExecutionException e1)
				{
					e1.printStackTrace();
				}
				
				return;
			}
		}
		
		if(args[0].equalsIgnoreCase(Main.PREFIX + "sshelp"))
		{
			e.getChannel().sendMessage(new EmbedBuilder()
					.setTitle("Secret Santa")
					.setColor(65280)
					.setDescription("This is al's secret santa code! Here are the commands (all of the commands are DM-only commands):")
					.appendDescription("\n\n**" + Main.PREFIX + "secretsanta:** reminds you who your secret santa is")
					.appendDescription("\n\n**" + Main.PREFIX + "ssm {give/receive} {message}** allows you to contact the person you are "
							+ "buying a gift for and receiving a gift from. **" + Main.PREFIX + "ssm give** will send a message to the person you're giving a gift to, and "
							+ "**" + Main.PREFIX + "ssm receive** will send a message to the person you're receiving a gift from. Remember that these conversations are anonymous!!")
					.build()).queue();
		}
	}
		
}
