import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

class TwoBit {

    final static DecimalFormat df = new DecimalFormat("0.00");
    public static void main(String [] args){
        
        if(args.length != 2){
            System.out.println("Proper Usage: java Simulator <individual file or directory of files> <table size>");
            System.exit(0);
        }

        if(!(args[1].equals("512")) && !(args[1].equals("1024")) && !(args[1].equals("2048")) && !(args[1].equals("4096"))){
            System.out.println("Table size must be one of the following: 512, 1024, 2048, 4096");
            System.exit(0);
        }

        //https://stackoverflow.com/questions/25536845/how-to-run-a-java-program-on-all-files-in-a-directory-from-command-line
        File readFile = new File(args[0]);
        int tableSize = Integer.parseInt(args[1]);

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
        
        if(taken.size() > 0){
            twoBit(branch, taken, tableSize);
        }
        
    }

    static void twoBit(ArrayList<String> branch, ArrayList<Integer> taken, int tableSize){
        int correct = 0;
        int sigBits = (int)(Math.log(tableSize) / Math.log(2));
        String[] buffer = new String[tableSize];

        for(int i = 0; i < taken.size(); i++){
            String binary = Long.toBinaryString(Long.parseLong(branch.get(i)));

            if (binary.length() > sigBits) {
                binary = binary.substring(binary.length() - sigBits);
            }else{
                binary = branch.get(i);
            }
            
            int index = Integer.parseInt(binary, 2);
            
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
        }

        double difference = ((double)correct / taken.size()) * 100;
        System.out.println(df.format(difference));
    }

}