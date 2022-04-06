//
//
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.File;
import java.io.FileInputStream;

//
//
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;  
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

//
//
public class sFTPClient
{
	//
	//Config Parameters for connecting, transfer
	static String strHost              = "";
	static String strUserName          = "";
	static String strPassword          = "";
	static String strDestinationFolder = "";
	static String strSourceFile 	   = "";

    public static void main( String[] args ) 
    {
		//
		//method to get data from config,xml
		getDataFromXML();

		//
		//method to send file to sFTP server
        send( strSourceFile );

    }//main

	//
	//method to send file to sFTP server
	public static void send( String fileName ) 
    {
		//
		//Parameters for creating sFTP Client
        Session session         = null;
        Channel channel         = null;
        ChannelSftp channelSftp = null;
        System.out.println( "preparing the host information for sftp." );

        try 
        {
            //
            //Creating Session
			JSch jsch   = new JSch();
            session     = jsch.getSession( strUserName, strHost, 22 );
            
            session.setPassword( strPassword );
            java.util.Properties config = new java.util.Properties();
            
            //
            //Connecting to sFTP Server
			config.put( "StrictHostKeyChecking", "no" );
            session.setConfig( config );
            session.connect();

            System.out.println( "Host connected." );
            channel = session.openChannel( "sftp" );
            channel.connect();

            System.out.println( "sftp channel opened and connected." );
            channelSftp = ( ChannelSftp ) channel;
            channelSftp.cd( strDestinationFolder );

			//
			//Creating file and transfering
            File f = new File( fileName );
            channelSftp.put( new FileInputStream(f), f.getName() );
            System.out.println( "File transfered successfully to host." );
        } 
        catch (Exception ex) 
        {
            System.out.println( "Unable to transfer file :: "+ex.getMessage() );
        }
        finally 
        {
			//
			//Closing all the channels and session
            channelSftp.exit();
            System.out.println("sftp Channel exited.");
            channel.disconnect();
            System.out.println("Channel disconnected.");
            session.disconnect();
            System.out.println("Host Session disconnected.");
        }// try-catch-finally

    }// send

	//
	//method to get data from config,xml
	public static void getDataFromXML()
	{
		try
		{
			//
			//Creating instance for xml file
			File 					XMLFile				= new File("./config.xml");
			DocumentBuilderFactory 	BuilderFactory 		= DocumentBuilderFactory.newInstance();

			DocumentBuilder DocBuilder 	= BuilderFactory.newDocumentBuilder();
			Document 		Doc 		= DocBuilder.parse(XMLFile);
			Doc.getDocumentElement().normalize();

			//
			//Getting required element values
			strHost                 = Doc.getElementsByTagName("Host").item(0).getTextContent();
			strUserName             = Doc.getElementsByTagName("UserName").item(0).getTextContent();
			strPassword             = Doc.getElementsByTagName("Password").item(0).getTextContent();
			strDestinationFolder    = Doc.getElementsByTagName("DestinationFolder").item(0).getTextContent();
			strSourceFile 		= Doc.getElementsByTagName("SourceFile").item(0).getTextContent();
		}
		catch(Exception e)
		{
			System.out.println("Unable to get config from config.xml :: "+e.getMessage());
		}// try-catch

	}// getDataFromXML

}// sFTPClient