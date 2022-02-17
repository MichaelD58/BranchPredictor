import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

class GShare {

    final static DecimalFormat df = new DecimalFormat("0.00");
    public static void main(String [] args){
        
        if(args.length == 2){
            if(!(args[1].equals("512")) && !(args[1].equals("1024")) && !(args[1].equals("2048")) && !(args[1].equals("4096"))){
                System.out.println("Table size must be one of the following: 512, 1024, 2048, 4096");
                System.exit(0);
            }

            int tableSize = Integer.parseInt(args[1]);
            readFile(args[0], tableSize);
            System.out.println(tableSize);
        }else if (args.length == 1){
            for(int i = 512; i <= 4096; System.out.println("\n")){
                System.out.println(i);
                readFile(args[0], i);
                i *= 2;
            }
        }else{
            System.out.println("Proper Usage: java GShare <individual file or directory of files> <table size>");
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
            System.out.println(filenameArray[2] + ": " + df.format(gShare(branch, taken, tableSize)) + "%");
        }
        
    }

    static double gShare(ArrayList<String> branch, ArrayList<Integer> taken, int tableSize){
        int correct = 0;
        int sigBits = (int)(Math.log(tableSize) / Math.log(2));
        String[] buffer = new String[tableSize];
        String global = "0";

        for(int i = 0; i < taken.size(); i++){
            String binary = Long.toBinaryString(Long.parseLong(branch.get(i)));
            

            if (binary.length() > sigBits) {
                binary = binary.substring(binary.length() - sigBits);
            }else{
                binary = branch.get(i);
            }

            if (global.length() > sigBits) {
                global = global.substring(global.length() - sigBits);
            }

            int a = Integer.parseInt(binary, 2);
            int b = Integer.parseInt(global, 2);
            int index = a ^ b;

            if(buffer[index] == null){
                buffer[index] = "00";
            }
            
            if(buffer[index].equals("00")){
                if(taken.get(i) == 1){
                    buffer[index] = "01";
                }else{
                    correct++;
                }
            }else if(buffer[index].equals("01")){
                if(taken.get(i) == 1){
                    buffer[index] = "11";
                }else if(taken.get(i) == 0){
                    buffer[index] = "00";
                    correct++;
                } 
            }else if(buffer[index].equals("11")){
                if(taken.get(i) == 0){
                    buffer[index] = "10";
                }else{
                    correct++;
                }
            }else{
                if(taken.get(i) == 1){
                    buffer[index] = "11";
                    correct++;
                }else if(taken.get(i) == 0){
                    buffer[index] = "00";
                }
            }
            global += taken.get(i);
        }

        double difference = ((double) (taken.size() - correct) / taken.size()) * 100;
        //double difference = ((double) correct  / taken.size()) * 100;
        return difference;
    }


}