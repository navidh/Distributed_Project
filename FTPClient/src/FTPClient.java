import java.io.*;
import java.net.*;

public class FTPClient {
	public static void main(String[] args){
		if (args.length !=2){
			System.err.println("Usage: java EchoClient cf2.cs.uga.edu 8080");
			System.exit(1);
		}

		String hostName = args[0];
		int portNumber = Integer.parseInt(args[1]);

		try(	    		  
				Socket mySocket = new Socket (hostName, portNumber);	    		  
				DataInputStream inCon = new DataInputStream(mySocket.getInputStream());
				BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
				DataOutputStream outCon = new DataOutputStream(mySocket.getOutputStream());	    		 
				){

			String userInput;				
			System.out.print("ftp:/> ");			
			while ((userInput = console.readLine()) != null ){

				String cmdName="";
				String paraName ="";				

				userInput = userInput.trim();
				int i = userInput.indexOf(" ");

				if (i != -1){
					cmdName = userInput.substring(0, i);
					paraName = userInput.substring(i+1 , userInput.length());
				}
				else{
					cmdName = userInput;
				}

				switch (cmdName){
				case "put":

					putData(paraName,inCon, outCon);
					System.out.print("ftp:/> ");
					break;
				case "get":
					getData(paraName, inCon, outCon);
					System.out.print("ftp:/> ");
					break;
				case "quit":
					outCon.writeUTF(userInput);
					outCon.close();
					inCon.close();
					mySocket.close();
					System.out.println("Goodbye!");
					System.exit(1);
					break;					
				default:
					outCon.writeUTF(userInput);
					//					inCon.readUTF()? "dd":"ff";

					String result="";
					String serverResult;
					serverResult = inCon.readUTF();
					if (serverResult.equals("true"))
						result = cmdName + " Done!";
					else
						result = serverResult;
					System.out.println ( result);
					System.out.print("ftp:/> ");
				}										


			} 
		}
		catch (UnknownHostException e){		      
			System.err.println("Don't know about host " + hostName);
			System.exit(1);
		}		catch (IOException e){
			System.err.println("Couldn't get I/O for the connection to " + hostName );
			System.exit (1);
		}

	}

	public static void putData(String fileName,DataInputStream in ,DataOutputStream out ) throws IOException{
		File file = new File(fileName);
		if (!file.exists()){
			System.out.println("No file exists!");
			return;
		}
		out.writeUTF("put " + fileName);
		String response = in.readUTF();

		if (!response.equals("ready")){
			System.out.println("File already exists in Server!");
			return;
		}


		try {
			int count;
			byte[] buffer = new byte[1024];
			BufferedInputStream br = new BufferedInputStream(new FileInputStream(fileName));
			while ((count = br.read(buffer)) > 0) {
				out.write(buffer, 0, count);
				if (in.readUTF().equals("next")){
				}
				
			}
			

			br.close();
			response = in.readUTF();

			System.out.println("Transfer successful!");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static String readFile(String fileName) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		} finally {
			br.close();
		}
	}


	public static void getData(String fileName, DataInputStream in, DataOutputStream out){

		File file = new File(fileName);
		if (file.exists()){   			
			System.out.println("file exists on client!");
		}
		else{
			try {
				out.writeUTF("get " + fileName);
				String temp = in.readUTF();
				if (temp.equals("okey")){
					out.writeUTF("ready");
					FileOutputStream fileStream= new FileOutputStream(file);
					byte[] buffer = new byte[1024];
					int count;
//					long j=1;
					do{
						count=in.read(buffer);
						fileStream.write(buffer, 0, count);
						out.writeUTF("next");
						//System.out.print(j);
						//j++;
					}while (count/1024>0);
					//String Result = in.readUTF();
					//System.out.println(Result);
					fileStream.close();
					System.out.println("Transfer successful!");
				}
				else System.out.println("Error occured on receiving!");

				//				System.out.println("Transfer successful!");

			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("in Error");
				e.printStackTrace();
			}

		}


	}
}