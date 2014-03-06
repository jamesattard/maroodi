package eu.scape_project.HDFSFTPServer.main;

import java.io.Console;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.command.CommandFactory;
import org.apache.ftpserver.command.CommandFactoryFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.ssl.SslConfigurationFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import eu.scape_project.HDFSFTPServer.commands.CDUP;
import eu.scape_project.HDFSFTPServer.commands.DELE;
import eu.scape_project.HDFSFTPServer.commands.APPE;
import eu.scape_project.HDFSFTPServer.commands.CWD;
import eu.scape_project.HDFSFTPServer.commands.LIST;
import eu.scape_project.HDFSFTPServer.commands.MLSD;
import eu.scape_project.HDFSFTPServer.commands.PWD;
import eu.scape_project.HDFSFTPServer.commands.RETR;
import eu.scape_project.HDFSFTPServer.commands.STOR;
import eu.scape_project.HDFSFTPServer.commands.MKD;
import eu.scape_project.HDFSFTPServer.commands.PASS;
import eu.scape_project.HDFSFTPServer.commands.RMD;

 


public class App{
 
	
	
	public static void main(String[] args) throws FtpException{
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory factory = new ListenerFactory();
		
		//set commands for HDFS storage
		CommandFactoryFactory  cf = new CommandFactoryFactory();
		cf.addCommand("STOR", new STOR());
		cf.addCommand("RETR", new RETR());
		cf.addCommand("LIST", new LIST());
		cf.addCommand("MLSD", new MLSD());
		cf.addCommand("APPE", new APPE());
		cf.addCommand("CWD", new CWD());
		cf.addCommand("PWD", new PWD());
		cf.addCommand("PASS", new PASS());
		cf.addCommand("CDUP", new CDUP());
		cf.addCommand("RMD", new RMD());
		cf.addCommand("DELE", new DELE());
		cf.addCommand("MKD", new MKD());
		CommandFactory commandFactory = cf.createCommandFactory();
		
		// set the port of the listener
		factory.setPort(2221);
		factory.setServerAddress("localhost");
		// define SSL configuration
		SslConfigurationFactory ssl = new SslConfigurationFactory();
		ssl.setKeystoreFile(new File("./res/ftpserver.jks"));
		ssl.setKeystorePassword("password");
		// set the SSL configuration for the listener
		factory.setSslConfiguration(ssl.createSslConfiguration());
		factory.setImplicitSsl(true);
		serverFactory.addListener("default", factory.createListener());
		serverFactory.setCommandFactory(commandFactory);
		
		//prepare user manager 
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		userManagerFactory.setFile(new File("./conf/users.properties"));
		//userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());
		serverFactory.setUserManager(userManagerFactory.createUserManager());
		
		// start the server
		FtpServer server = serverFactory.createServer();
		server.start();
		Scanner in = new Scanner(System.in);
		
		while(true){
			String s = in.nextLine();
			if(s.equals("stop")){
				server.stop();
				break;
			} else if(s.equals("createuser")){
				Console console = System.console();
				console.printf("User name: ");
				String username = in.nextLine();
				console.printf("Enter password: ");
				
				char passArray[] = console.readPassword();
				System.out.print("Confirm password: ");
				char passArray2[] = console.readPassword();
				boolean match = Arrays.equals(passArray, passArray2);
				if(!match){
					System.out.println("Passwords didn't match");
				}else {
					UserManager um = serverFactory.getUserManager();
					BaseUser user = new BaseUser();
					user.setName(username);
					user.setPassword(passArray.toString());
					user.setHomeDirectory("/tmp/ftp/" + username);
					List<Authority> auths = new ArrayList<Authority>();
		            Authority writePerm = new WritePermission();
		            auths.add(writePerm);
		            user.setAuthorities(auths);
					um.save(user);
				}
			
				
				
			}
		}
		in.close();

	}

}