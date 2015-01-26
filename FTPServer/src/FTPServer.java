import java.net.*;
import java.io.*;

public class FTPServer {
	public static void main(String[] args) throws IOException {

		if (args.length != 1) {
			System.err.println("Usage: java EchoServer <port number>");
			System.exit(1);
		}

		int portNumber = Integer.parseInt(args[0]);

		try (
				ServerSocket serverSocket = new ServerSocket(Integer.parseInt(args[0]));
				Socket clientSocket = serverSocket.accept();     
				PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);                   
				BufferedReader in = new BufferedReader( new InputStreamReader(clientSocket.getInputStream()));
				) {
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				String cmdName="";
				String paraName ="";
				boolean result=false;

				inputLine = inputLine.trim();
				int i = inputLine.indexOf(" ");

				if (i != -1){
					cmdName = inputLine.substring(0, i);
					paraName = inputLine.substring(i+1 , inputLine.length());
				}
				else{
					cmdName = inputLine;
				}

				switch (cmdName){
				case "mkdir":					
					result = CreateDirectory(paraName);
					out.println( result);
					break;
				case "delete":
					result = DeleteDirectory(paraName);
					out.println( result);
					break;
				case "ls":
					out.println( ListDirectory());
					break;
				case "pwd":
					out.println( CurrentDirectory());
					break;
				case "cd":
					out.println( ChangeDirectory(paraName));
					break;
				default:
					out.println(cmdName + ": command not found");
					break;
				}


			}
		} catch (IOException e) {
			System.out.println("Exception caught when trying to listen on port "
					+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
		}


	}

	public static boolean CreateDirectory(String directoryName){	
		File dir = new File (System.getProperty("user.dir"));
		String ParentName = dir.getParentFile().getAbsolutePath();
//		File dir = new File (directoryName);
		System.setProperty("user.dir", ParentName);
		return dir.mkdir();				
	}

	public static boolean DeleteDirectory(String directoryName){
		try{
			File dir = new File(directoryName);
			return dir.delete();
		}
		catch (Exception e ){
			return false;
		}

	}

	public static String ListDirectory(){
		File dir = new File(System.getProperty("user.dir"));
		String childs[] = dir.list();
		String result = "";
		for(String c:childs){
			result += c + "\t";
		}
		return result;
	}

	public static String CurrentDirectory(){
		String pwd = System.getProperty("user.dir");
		return pwd;
	}

	public static String ChangeDirectory(String directory){
		try {


			if(!directory.isEmpty()){
				File dir;

				if (directory.equals("..")){
					dir = new File (System.getProperty("user.dir"));

					String ParentName = dir.getParentFile().getAbsolutePath();
					
//					if (ParentName != null){ 
						System.setProperty("user.dir", ParentName);
						String pwd = System.getProperty("user.dir");
						return pwd;
//					}
//					else{
//						return "no Parent Directory existed!";
//					}
				}
				else{
					dir = new File(directory);
					if(dir.isDirectory() == true){
						System.setProperty("user.dir", dir.getAbsolutePath());
						String pwd = System.getProperty("user.dir");
						return pwd;
					}
					else{
						return directory + " : No such a directory found!";
					}
				}
			}
			else{
				return "No Directory Specified!";
			}
		
		}
		catch (NullPointerException e){
			return "No Parent Directory";
		}
		catch (Exception e) {
			
			return "Error!";
		}
	}
		
}
