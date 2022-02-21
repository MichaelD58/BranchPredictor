import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Profiled {

    final static DecimalFormat df = new DecimalFormat("0.00");//Format used to print all averages
    public static int fileCount = 0;
    public static float totalAverage = 0;

    public static void main(String [] args){
        //If table size within command line
        if(args.length == 2){
            if(!(args[1].equals("512")) && !(args[1].equals("1024")) && !(args[1].equals("2048")) && !(args[1].equals("4096"))){
                System.out.println("Table size must be one of the following: 512, 1024, 2048, 4096");
                System.exit(0);
            }

            int tableSize = Integer.parseInt(args[1]);
            readFile(args[0], tableSize);
            
            System.out.println("\nAVERAGE: " + df.format(totalAverage/fileCount) + "%");
        }else if (args.length == 1){
            //For each of the four suitable table sizes
            for(int i = 512; i <= 4096; System.out.println("\n")){
                fileCount = 0;
                totalAverage = 0;
                System.out.println(i);//Table size printed
                readFile(args[0], i);
                System.out.println("\nAVERAGE: " + df.format(totalAverage/fileCount) + "%");
                i *= 2;//Table size is squared to get the next table size
            }
        }else{
            System.out.println("Proper Usage: java Profiled <individual file or directory of files> <table size>");
            System.exit(0);
        }

    }

    static void readFile(String fileName, int tableSize){
        //https://stackoverflow.com/questions/25536845/how-to-run-a-java-program-on-all-files-in-a-directory-from-command-line
        File readFile = new File(fileName);
        
        if(readFile.isDirectory()) {
            for(File file : readFile.listFiles()) {
                reader(file, tableSize);//Reader method called on each file
            }
        }else {
            reader(readFile, tableSize);//Reader method called on specific file in command line
        }
    }

    static void reader(File file, int tableSize){
        ArrayList<String> branch = new ArrayList<String>();//ArrayList used to store every branch address encountered in order
        ArrayList<Integer> taken = new ArrayList<Integer>();//ArrayList used to store every taken/not taken encountered in order

        try{
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] split = data.split(" ");//Line is split at the space in between
                //Values added to respective ArrayLists
                branch.add(split[0]);
                taken.add(Integer.parseInt(split[1]));
            }
            myReader.close();
        }catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        
        String filename = file.toString();
        String[] filenameArray = new String[3];
        //https://stackoverflow.com/questions/23751618/how-to-split-a-java-string-at-backslash
        filenameArray = filename.split("\\\\");//Filename split at \ to get only the file name and not the directory its in
        //For every file with at least one take/not taken
        if(taken.size() > 0){
            System.out.println(filenameArray[2] + ": " + df.format(profiled(branch, taken, tableSize)) + "%");//Output of the strategy method
            fileCount++;
        }
        
    }

    static double profiled(ArrayList<String> branch, ArrayList<Integer> taken, int tableSize){
        int correct = 0;
        int sigBits = (int)(Math.log(tableSize) / Math.log(2));//SigBits acquired from table size
        HashMap<String, Integer> addressCounter = new HashMap<String, Integer>();//HashMap used to store the adresses encountered and their 1:0 ratio
        
        for(int i = 0; i < branch.size(); i++){
            String binary = Long.toBinaryString(Long.parseLong(branch.get(i)));//Branch address is converted to binary

            //If binary address is longer than sigBits allow, cut it
            if (binary.length() > sigBits) {
                binary = binary.substring(binary.length() - sigBits);
            }else{
                binary = branch.get(i);
            }
            
            //If there are still addresses to be added to the HashMap
            if(addressCounter.size() < tableSize){
                if(!addressCounter.containsKey(binary)){
                    if(taken.get(i) == 0){
                        addressCounter.put(binary, 0);
                    }else{
                        addressCounter.put(binary, 1);
                    }
                }else{
                    if(taken.get(i) == 0){
                        addressCounter.put(binary, addressCounter.get(binary) - 1);
                    }else{
                        addressCounter.put(binary, addressCounter.get(binary) + 1);
                    }
                }
                //HashMap is full, end profiling stage
            }else if(addressCounter.size() == tableSize){
                break;
            }
            
   
        }

        //'Real' run
        for(int i = 0; i < branch.size(); i++){
            String binary = Long.toBinaryString(Long.parseLong(branch.get(i)));//Branch address is converted to binary

            //If binary address is longer than sigBits allow, cut it
            if (binary.length() > sigBits) {
                binary = binary.substring(binary.length() - sigBits);
            }else{
                binary = branch.get(i);
            }

            //If address cannot be found in HashMap, use default prediction
            if(!addressCounter.containsKey(binary)){
                if(taken.get(i) == 1){
                    correct++;
                }
                // if(taken.get(i) == 0){
                //     correct++;
                // }
            }else{         
                //If more non-takens than taken in profiling, predict 0   
                if(addressCounter.get(binary) > 0){
                    if(taken.get(i) == 1){
                        correct++;
                    }
                    //If more takens than non-taken in profiling, predict 1
                }else{
                    if(taken.get(i) == 0){
                        correct++;
                    }
                }   
            }
            
        }

        double difference = ((double) (taken.size() - correct) / taken.size()) * 100;//Misprediction rate is calculated
        //double difference = ((double) correct  / taken.size()) * 100;
        totalAverage += difference;
        return difference;
    }


}