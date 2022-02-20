import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

class Profiled {

    final static DecimalFormat df = new DecimalFormat("0.00");
    public static void main(String [] args){
        
        if(args.length == 2){
            if(!(args[1].equals("512")) && !(args[1].equals("1024")) && !(args[1].equals("2048")) && !(args[1].equals("4096"))){
                System.out.println("Table size must be one of the following: 512, 1024, 2048, 4096");
                System.exit(0);
            }

            int tableSize = Integer.parseInt(args[1]);
            readFile(args[0], tableSize);
        }else if (args.length == 1){
            for(int i = 512; i <= 4096; System.out.println("\n")){
                System.out.println(i);
                readFile(args[0], i);
                i *= 2;
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
                reader(file, tableSize);
            }
        }else {
            reader(readFile, tableSize);
        }
    }

    static void reader(File file, int tableSize){
        ArrayList<String> branch = new ArrayList<String>();
        ArrayList<Integer> taken = new ArrayList<Integer>();

        try{
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                String[] split = data.split(" ");
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
        filenameArray = filename.split("\\\\");
        if(taken.size() > 0){
            System.out.println(filenameArray[2] + ": " + df.format(profiled(branch, taken, tableSize)) + "%");
        }
        
    }

    static double profiled(ArrayList<String> branch, ArrayList<Integer> taken, int tableSize){
        int correct = 0;
        int sigBits = (int)(Math.log(tableSize) / Math.log(2));
        HashMap<String, Integer> addressCounter = new HashMap<String, Integer>();
        
        for(int i = 0; i < branch.size(); i++){
            String binary = Long.toBinaryString(Long.parseLong(branch.get(i)));

            if (binary.length() > sigBits) {
                binary = binary.substring(binary.length() - sigBits);
            }else{
                binary = branch.get(i);
            }
            
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
            }else if(addressCounter.size() == tableSize){
                break;
            }
            
   
        }


        for(int i = 0; i < branch.size(); i++){
            String binary = Long.toBinaryString(Long.parseLong(branch.get(i)));

            if (binary.length() > sigBits) {
                binary = binary.substring(binary.length() - sigBits);
            }else{
                binary = branch.get(i);
            }

            if(!addressCounter.containsKey(binary)){
                if(taken.get(i) == 1){
                    correct++;
                }
            }else{            
                if(addressCounter.get(binary) > 0){
                    if(taken.get(i) == 1){
                        correct++;
                    }
                }else{
                    if(taken.get(i) == 0){
                        correct++;
                    }
                }   
            }
            
        }

        double difference = ((double) (taken.size() - correct) / taken.size()) * 100;
        //double difference = ((double) correct  / taken.size()) * 100;
        return difference;
    }


}