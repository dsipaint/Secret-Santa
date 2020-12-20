import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class Main
{
	static JDA jda;
	static final String PREFIX = "^";
	static final String[] MEMBERS = {
			"563109508784848909", //amber
			"361229928462811136", //bry
			"422483625733259265", //daisy
			"353249405299589130", //dan
			"572889362828623875", //gabi
			"475859944101380106", //al
			"272492363857920000", //jokie
			"103994760607326208", //josh
			"128937527750033408", //azure
			"191054375769210880", //lyrica
			"563096076488540160", //macy
			"349192126019534848", //midnight
			"534170829781598209", //park
			"598812717901545502", //ryan
			"330866325037645824", //sophie
			"670043565954564116" //jess
	};
	
	public static void main(String[] args)
	{
		try
		{
			jda = JDABuilder.createDefault("")
					.enableIntents(GatewayIntent.GUILD_MEMBERS, GatewayIntent.DIRECT_MESSAGES)
					.build();
		}
		catch (LoginException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			jda.awaitReady();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
		jda.getGuildById("565623426501443584").loadMembers();
		
		jda.addEventListener(new CommandListener());
		jda.addEventListener(new StopListener());
	}
}
