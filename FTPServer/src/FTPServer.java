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
				ServerSocket serverSocket = new ServerSocket(portNumber);
				Socket clientSocket = serverSocket.accept(); 

				DataInputStream in = new DataInputStream(clientSocket.getInputStream());
				DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());

				) {
			String inputLine;

			while (true) {
				if (!clientSocket.isClosed()){
					inputLine = in.readUTF();
					//System.out.println("<" + inputLine + ">" );
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

						out.writeUTF( DeleteDirectory(paraName));
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
						//					out.close();
						//					in.close();
						//					serverSocket.close();
						//					System.exit(1);
						clientSocket.close();
						break;
					default:
						out.writeUTF(cmdName + ": command not found");
						break;
					}


				}
			}
		} catch (IOException e) {
			//			System.out.println("Exception caught when trying to listen on port "
			//					+ portNumber + " or listening for a connection");
			System.out.println(e.getMessage());
			//			System.out.println(e);
		}


	}

	public static boolean CreateDirectory(String directoryName){		
		File dir = new File (directoryName);
		return dir.mkdir();				
	}

	public static String DeleteDirectory(String directoryName){
		try{
			File dir = new File(directoryName);
			if (!dir.exists()){
				return "No Such File exists!";
			}
			return String.valueOf(dir.delete());
		}
		catch (Exception e ){
			return "No such file exists";
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
		    //System.out.println("in IF");
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

				byte[] buffer = new byte[1024];
				int count;
				long j=1;
				do{
					count=in.read(buffer);
					fileStream.write(buffer, 0, count);
					//out.writeUTF("ready");
					out.writeUTF("next");
					
					//System.out.println(j + "-" + count);
					//j++;
				}while (count/1024>0);

				
				fileStream.close();

				out.writeUTF("Done!");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}


	}

	public static String ChangeDirectory(String path){

		String[] directories = path.split("/");
		//		String current
		for (String directory: directories){
			try {
				if(!directory.isEmpty()){
					File dir;

					if (directory.equals("..")){
						dir = new File (System.getProperty("user.dir"));

						String ParentName = dir.getParentFile().getAbsolutePath();

						System.setProperty("user.dir", ParentName);
						//						String pwd = System.getProperty("user.dir");
						//						return pwd;

					}
					else{
						dir = new File(System.getProperty("user.dir"),directory );
						//						System.out.println(dir.getAbsoluteFile().getPath());
						if(dir.isDirectory()){
							System.setProperty("user.dir", dir.getAbsoluteFile().getPath());
							//							System.out.println(directory);
							//							System.out.println(dir.getAbsoluteFile().getPath());
							//System.out.println(System.getProperty("user.dir"));
							//							System.out.println(dir.getAbsolutePath());
							//							String pwd = System.getProperty("user.dir");
							//							return pwd;
						}
						else{
							return path + " : No such a directory found!";
						}
					}
				}
				else{
					return "No Directory Specified!";
				}
			}
			catch (NullPointerException e){
				//				return "No Parent Directory";
				return e.getMessage();
			}
			catch (Exception e) {

				return "Error!";
			}
		}
		String pwd = System.getProperty("user.dir");
		return pwd;

	}


	public static void SendFile(String fileName,DataInputStream in ,DataOutputStream out ) throws IOException{
		//		System.out.println("Send Start");
		File file = new File(fileName);
		if (!file.exists()){
			//System.out.println("No file exists On Server!");
			out.writeUTF("No file exists!");

			return;
		}
		//System.out.println("after");
		out.writeUTF("okey");
		String response = in.readUTF();

		if (!response.equals("ready")){
			System.out.println("File already exists in Server!");
			return;
		}


		try {
			//			BufferedReader br = new BufferedReader(new FileReader(fileName));
			//			int line;
			//			do{
			//				line = br.read();
			//				out.writeUTF(String.valueOf(line));
			//				
			//			}while (line != -1);
			int count;
			byte[] buffer = new byte[1024];
			BufferedInputStream br = new BufferedInputStream(new FileInputStream(fileName));
			while ((count = br.read(buffer)) > 0) {
				out.write(buffer, 0, count);
				String check = in.readUTF();
				while(!check.equals("next")){
				}
				
			}
			br.close();
			//out.writeUTF("Transfer successful!");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


}
