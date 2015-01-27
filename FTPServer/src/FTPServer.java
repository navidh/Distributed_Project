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

				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

				) {
			String inputLine;
			while ((inputLine = in.readUTF()) != null) {
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
					out.writeUTF( String.valueOf(result));
					break;
				case "delete":
					result = DeleteDirectory(paraName);
					out.writeUTF( String.valueOf(result));
					break;
				case "ls":
					out.writeUTF( ListDirectory());
					break;
				case "pwd":
					out.writeUTF( CurrentDirectory());
					break;
				case "cd":
					out.writeUTF(ChangeDirectory (paraName));
					break;
				case "put":
					receiveFile(paraName, in, out);
					break;	
				case "get":
					SendFile(paraName, in, out);
					break;
				case "quit":
					out.close();
					in.close();
					serverSocket.close();
					System.exit(1);
					
				default:
					out.writeUTF(cmdName + ": command not found");
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
		File dir = new File (directoryName);
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
	
	public static void receiveFile(String fileName, DataInputStream in, DataOutputStream out){

		File file = new File(fileName);
		
		if (file.exists()){
			System.out.println("in IF");
			try {
				out.writeUTF("file exists!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		else{
			try {
				out.writeUTF("ready");
				FileOutputStream fileStream= new FileOutputStream(file);
				int intInput ;
				String input;
				do{
					input = in.readUTF();
					intInput = Integer.parseInt(input);
					if (intInput!=-1) fileStream.write(intInput);
				}while (intInput != -1);
				
				fileStream.close();
				
				out.writeUTF("Done!");
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}


	}

	public static String ChangeDirectory(String directory){
		try {


			if(!directory.isEmpty()){
				File dir;

				if (directory.equals("..")){
					dir = new File (System.getProperty("user.dir"));

					String ParentName = dir.getParentFile().getAbsolutePath();

					System.setProperty("user.dir", ParentName);
					String pwd = System.getProperty("user.dir");
					return pwd;

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
	
	
	public static void SendFile(String fileName,DataInputStream in ,DataOutputStream out ) throws IOException{
 	   File file = new File(fileName);
 	   if (!file.exists()){
 		   System.out.println("No file exists!");
 		   return;
 	   }
 	   out.writeUTF("okey");
 	   String response = in.readUTF();

 	   if (!response.equals("ready")){
 		   System.out.println("File already exists in Server!");
 		   return;
 	   }
 	   
 	   
 	   try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			int line;
			do{
				line = br.read();
				out.writeUTF(String.valueOf(line));
				
			}while (line != -1);
			br.close();
			
			out.writeUTF("Transfer successful!");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
 	   
 	   
    }


}
