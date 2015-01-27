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
					//System.out.println ( inCon.readUTF());
					System.out.print("ftp:/> ");
					break;
				case "get":
					//outCon.writeUTF(userInput);
					getData(paraName, inCon, outCon);
					//System.out.println ( inCon.readUTF());
					System.out.print("ftp:/> ");
					break;
				case "quit":
					outCon.writeUTF(userInput);
					outCon.close();
					inCon.close();
					mySocket.close();
					System.out.println("Goodbye!");
					System.exit(1);
				default:
					outCon.writeUTF(userInput);
					System.out.println ( inCon.readUTF());
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
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			int line;
			do{
				line = br.read();
				out.writeUTF(String.valueOf(line));
				
			}while (line != -1);
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
   			
   			try {
   				out.writeUTF("file exists!");
   			} catch (IOException e) {
   				
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}

   		}
   		else{
   			try {
   				out.writeUTF("get " + fileName);
   				String temp = in.readUTF();
   				if (temp.equals("okey")){
   					
   					FileOutputStream fileStream= new FileOutputStream(file);
   					int intInput ;
   					String input;
   					out.writeUTF("ready");
   					do{
   						input = in.readUTF();
   						intInput = Integer.parseInt(input);
   						if (intInput!=-1) fileStream.write(intInput);
   					}while (intInput != -1);

   					fileStream.close();

   				}
   				else out.writeUTF("error");
   				
   				System.out.println(in.readUTF());
   					
   			} catch (IOException e) {
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}
   			
   		}


   	}
}