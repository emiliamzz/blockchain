import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

public class BlockChain{
  
  private ArrayList<Block> blocks; //an ArrayList that holds all of the blocks in the BlockChain
  
  public BlockChain(ArrayList<Block> b){
    blocks = b;
  }//end BlockChain
  
  /* Reads Blocks from specified file and adds to the BlockChain. Works under the assumption that everything on the
   * file will work. File should be in the format:
   * >index
   * >timestamp (as a long)
   * >sender
   * >receiver
   * >amount
   * >nonce
   * >expected hash
   * 
   * @param fileName Name of the file to be read
   * @return BlockChain with specified Blocks
   */
  public static BlockChain fromFile(String fileName){
    ArrayList<Block> list = new ArrayList<Block>();
    try{
      BufferedReader br = new BufferedReader(new FileReader(fileName));
      String line;
      while((line = br.readLine()) != null){ //reads the file line by line and assigning variables to each line,
        int index = Integer.parseInt(line);  //assuming that each block is complete
        line = br.readLine();
        long timestamp = Long.parseLong(line);
        line = br.readLine();
        String sender = line;
        line = br.readLine();
        String receiver = line;
        line = br.readLine();
        int amount = Integer.parseInt(line);
        line = br.readLine();
        String nonce = line;
        line = br.readLine();
        String hash = line;
        if(index == 0){
          list.add(0, new Block(0, timestamp, hash, nonce, "00000", new Transaction(sender, receiver, amount)));
        }//end if //adds a new Block to index 0 of the BlockChain, with "00000" as the previousHash
        else{
          list.add(index, new Block(index, timestamp, hash, nonce, list.get(index-1).getHash(),
                                    new Transaction(sender, receiver, amount)));
        }//end else //adds a new Block to the specified index of the BlockChain
      }//end while
      br.close();
    }//end try
    catch(FileNotFoundException e){
      System.out.println("FileNotFoundException. Please reset the program and try again.");
      System.exit(0);
    }//end catch
    catch(IOException e){
      System.out.println("IOException. Please reset the program and try again.");
      System.exit(0);
    }//end catch
    return new BlockChain(list);
  }//end fromFile
  
  /* Creates a file of a specified name and prints the BlockChain to it. It will be in the format:
   * >index
   * >timestamp (as a long)
   * >sender
   * >receiver
   * >amount
   * >nonce
   * >expected hash
   * 
   * @param fileName Name of the file to be created
   */
  public void toFile(String fileName){
    try{
      PrintWriter writer = new PrintWriter(fileName);
      for(int i=0; i<blocks.size(); i++){ //goes through each block one by one and prints the necessary information
        writer.println(i);                //line by line
        writer.println(blocks.get(i).getTimestamp());
        writer.println(blocks.get(i).getSender());
        writer.println(blocks.get(i).getReceiver());
        writer.println(blocks.get(i).getAmount());
        writer.println(blocks.get(i).getNonce());
        writer.println(blocks.get(i).getHash());
      }//end for
      writer.close();
    }//end try
    catch(FileNotFoundException e){
      System.out.println("FileNotFoundException. Please reset the program and try again.");
      System.exit(0);
    }//end catch
  }//end toFile
  
  /* Validates blockchain by checking hashes to make sure they correspond to the values in the block. Index and
   * previousHash are also checked for consistency. Transactions are also verified to make sure noone spends what they
   * don't have
   * 
   * @return Whether transactions is valid or not
   */
  public boolean validateBlockchain(){
    try{
      ArrayList<String[]> accounts = new ArrayList<String[]>(); //ArrayList of "accounts" that hold the name of the
                                                                //person in [0] and their balance in [1]
      for(int i=0; i<blocks.size(); i++){ //goes through each block one by one
        Block block = blocks.get(i);
        String hash = Sha1.hash(block.toString()); //finds what the hash should be
        if(i == 0){ //if index == 0
          if((!hash.equals(block.getHash())) || (i != block.getIndex()) || (!block.getPreviousHash().equals("00000"))){
            return false; //checks if the actual hash == inputted hash, index == inputted index,
          }//end if       //inputted previous hash == "00000", returns false if at least one is false
          String[] account = {block.getReceiver(), String.valueOf(block.getAmount())};
          accounts.add(account); //adds the account of the person to the accounts ArrayList, as bitcoin isn't a real
        }//end if                //account and is only there to put the original 50 bitcoins into the system
        else{ //if index is anything but 0
          if((!hash.equals(block.getHash())) || (i != block.getIndex()) || //checks if actual hash == inputted hash,
                                                                           //index == inputted index
             (!block.getPreviousHash().equals(blocks.get(i-1).getHash()))){ //inputted previousHash == previousHash
            return false; //returns false if at least one of those statements is false
          }//end if //at this point the Block itself is valid but now we need to check if the transaction is valid
          boolean senderExists = false; //a boolean to see if the sender exists in the accounts ArrayList
          boolean receiverExists = false; //a boolean to see if the receiver exists in the accounts ArrayList
          for(int j=0; j<accounts.size(); j++){ //goes through all of the accounts
            String[] acc = accounts.get(j);
            if(!senderExists && block.getSender().equals(acc[0])){ //checks to see if the sender of the block matches
                                         //the owner of the current account. Skips if the sender has already been found
              if(block.getAmount() > Integer.parseInt(acc[1])){
                return false; //checks if the amount of the transaction is more than what the sender has and returns
              }//end if       //false if true
              String[] account = {block.getSender(), String.valueOf(Integer.parseInt(acc[1]) - block.getAmount())};
              accounts.set(j, account); //deducts the amount of the transaction from the sender's account
              senderExists = true; //sets that the sender exists
            }//end if
            else if(!receiverExists && block.getReceiver().equals(acc[0])){ //checks to see if the receiver of the 
                         //block matches the owner of the current account. Skips if the receiver has already been found
              String[] account = {block.getReceiver(), String.valueOf(Integer.parseInt(acc[1]) + block.getAmount())};
              accounts.set(j, account); //adds the amount of the transaction to the receiver's account
              receiverExists = true; //sets that the receiver exists
            }//end else if
            if(senderExists && receiverExists){
              break; //breaks the loop if both sender and receiver exist as they no longer need to be found
            }//end if
          }//end for
          if(!senderExists){
            return false; //returns false if sender doesn't exist since that means they have no money to send
          }//end if
          if(!receiverExists){
            String[] account = {block.getReceiver(), String.valueOf(block.getAmount())};
            accounts.add(account); //makes a new account for the receiver and gives the balance of the amount
          }//end if
        }//end else
      }//end for
    }//end try
    catch(UnsupportedEncodingException e){
      System.out.println("UnsupportedEncodingException. Please reset the program and try again.");
      System.exit(0);
    }//end catch
    return true;
  }//end validateBlockchain
  
  /* Gets the balance of the specified username
   * 
   * @param username The name of the person whose balance needs to be found
   * @return The balance
   */
  public int getBalance(String username){
    int balance = 0; //initial balance of said user
    for(int i=0; i<blocks.size(); i++){ //goes through each block
      Block block = blocks.get(i);
      if(block.getSender().equals(username)){
        balance -= block.getAmount(); //deducts amount from balance if user is sender
      }//end if
      else if(block.getReceiver().equals(username)){
        balance += block.getAmount(); //adds amount to balance if user is receiver
      }//end else if
    }//end for
    return balance;
  }//end getBalance
  
  /* Adds the specified Block to the BlockChain
   * 
   * @param block The Block to be added to the BlockChain
   */
  public void add(Block block){
    blocks.add(block);
  }//end add
  
  /* Finds a nonce by trial and error that will create a hash that starts with five zeroes in hexadecimal notation
   * 
   * @param timestamp The timestamp of the Block
   * @param transaction The Transaction of the Block
   * @param previousHash The previous Block's hash
   * @return Valid nonce
   */
  public String findNonce(long timestamp, Transaction transaction, String previousHash){
    try{
      String nonce;
      int tries = 0; //the number of tries it takes to find a valid nonce
      for(int a=33; a<127; a++){ //each for loop is for each character in the nonce, going from 33 to 126 inclusive
        for(int b=33; b<127; b++){
          for(int c=33; c<127; c++){
            for(int d=33; d<127; d++){
              for(int e=33; e<127; e++){
                for(int f=33; f<127; f++){
                  for(int g=33; g<127; g++){
                    for(int h=33; h<127; h++){
                      for(int i=33; i<127; i++){
                        for(int j=33; j<127; j++){
                          for(int k=33; k<127; k++){
                            for(int l=33; l<127; l++){
                              for(int m=33; m<127; m++){
                                for(int n=33; n<127; n++){
                                  for(int o=33; o<127; o++){
                                    tries++; //adds one for each try
                                    char[] nonceChar = {(char)a, (char)b, (char)c, (char)d, (char)e, (char)f, (char)g,
                                      (char)h, (char)i, (char)j, (char)k, (char)l, (char)m, (char)n, (char)o};
                                    //creates a char array that changes with each for loop iterartion
                                    nonce = new String(nonceChar); //converts char array into a String
                                    Block block = new Block(0, timestamp, "", nonce, previousHash, transaction);
                                    //creates a block with the necessary inputs for toString() (index and hash are not
                                    //needed)
                                    if(Sha1.hash(block.toString()).substring(0, 5).equals("00000")){
                                      System.out.println("It took " + tries +
                                                         " hash trials to obtain the proof-of-work.");
                                      //prints out the number of tries it took to find the hash
                                      return nonce; //tests out the nonce to see if the hash works and returns the
                                    }//end if       //nonce if the hash is valid
                                  }//end for
                                }//end for
                              }//end for
                            }//end for
                          }//end for
                        }//end for
                      }//end for
                    }//end for
                  }//end for
                }//end for
              }//end for
            }//end for
          }//end for
        }//end for
      }//end for
    }//end try
    catch(UnsupportedEncodingException z){
      System.out.println("UnsupportedEncodingException. Please reset the program and try again.");
      System.exit(0);
    }//end catch
    return null;
  }//end findNonce
  
  /* Gets the Block at the specified index
   * 
   * @param index Index of Block
   * @return Specified Block
   */
  public Block getBlock(int index){
    return blocks.get(index);
  }//end getBlock
  
  /* Returns the amount of Blocks in the BlockChain
   * 
   * @return Amount of Blocks
   */
  public int size(){
    return blocks.size();
  }//end size
  
  public static void main(String[] args){
    try{
      /* 1. Reading a BlockChain from a given file:
       * Asks the user for which file they would like to read. Adds the BlockChain from the specified .txt file to the
       * ArrayList
       */
      Scanner scan = new Scanner(System.in);
      System.out.println("What is the name of the file of the blockchain you'd like to upload?");
      String fileName = scan.next();
      BlockChain blockchain = fromFile(fileName);
      
      /* 2. Validating the BlockChain:
       * Checks to see if the BlockChain is valid. If not, the program loops step 1 until a valid .txt file is entered
       */
      boolean valid = blockchain.validateBlockchain();
      while(!valid){
        System.out.println("The blockchain you entered wasn't valid. Please enter another file name.");
        fileName = scan.next();
        blockchain = fromFile(fileName);
        valid = blockchain.validateBlockchain();
      }//end while
      
      /* Prompting the use for a new Transaction and verifying it
       * Tells the user to enter 1 if they would like to enter a new transaction. If they enter something other than 1,
       * this whole partis skipped. Otherwise, it asks for the sender, receiver, and the amount of the transaction.
       * Then the transaction is validated. If it is not valid, it loops and asks the user to input the sender,
       * receiver, and the amount until a valid transaction is given.
       */
      System.out.println("Would you like to add a transation? Enter 1 for yes, any other number for no.");
      int cont;
      cont = scan.nextInt();
      if(cont == 1){
        boolean add = true;
        while(add){
          valid = false;
          String sender = "";
          String receiver = "";
          int amount = 0;
          while(!valid){
            System.out.println("To add a transaction, first specify who the sender is.");
            sender = scan.next();
            System.out.println("Please specify who the receiver is.");
            receiver = scan.next();
            System.out.println("What is the amount being tranferred?");
            amount = scan.nextInt();
            if(blockchain.getBalance(sender) < amount){
              System.out.println("The sender doesn't have enough money for the transaction. Please add a different " +
                                 "transaction.");
            }//end if
            else{
              valid = true;
            }//end else
          }//end while
          
          /* 4. Adding the Transaction into the BlockChain:
           * This part is skipped if the user didn't want to enter a new Transaction. If the Transaction is found to be
           * valid, the index of the new Block is found. While the nonce is null, findNonce() is looped until a valid
           * nonce is found, each loop having a different timestamp. With the nonce and the timestamp the hash is
           * found. Those are then inputted into a new Block and added to the BlockChain
           */
          System.out.println("Please wait while your transaction is being hashed.");
          int index = blockchain.size();
          String nonce = null;
          long timestamp = 0;
          Transaction transaction = new Transaction(sender, receiver, amount);
          String previousHash = blockchain.getBlock(index-1).getHash();
          while(nonce == null){
            timestamp = System.currentTimeMillis();
            nonce = blockchain.findNonce(timestamp, transaction, previousHash);
          }//end while
          Timestamp time = new Timestamp(timestamp);
          String hash = Sha1.hash(time.toString() + ":" + transaction.toString() + "." + nonce + previousHash);
          blockchain.add(new Block(index, timestamp, hash, nonce, previousHash, transaction));
          
          /* 5. Asking for more Transactions:
           * The user is asked to input 1 if they would like to add more Transactions. If 1 is inputted, the program
           * goes back to step 3
           */
          System.out.println("If you would like to enter another transaction, please enter 1.");
          cont = scan.nextInt();
          if(cont != 1){
            add = false;
          }//end if
        }//end while
      }//end if
      
      /* 6. Saving the BlockChain to a file with specific name:
       * The user is asked for what they would like to name the new BlockChain. It is then saved with the specified
       * name
       */
      System.out.println("What would you like to name the file?");
      fileName = scan.next();
      scan.close();
      blockchain.toFile(fileName);
      System.out.println("Your file has been created.");
    }//end try
    catch(UnsupportedEncodingException e){
      System.out.println("UnsupportedEncodingException. Please reset the program and try again.");
      System.exit(0);
    }//end catch
  }//end main
  
}//end BlockChain