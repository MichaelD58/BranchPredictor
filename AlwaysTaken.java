import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

class AlwaysTaken {

    final static DecimalFormat df = new DecimalFormat("0.00");
    public static void main(String [] args){
        
        if(args.length != 1){
            System.out.println("Proper Usage: java AlwaysTaken <individual file or directory of files>");
            System.exit(0);
        }

        //https://stackoverflow.com/questions/25536845/how-to-run-a-java-program-on-all-files-in-a-directory-from-command-line
        File readFile = new File(args[0]);
        if(readFile.isDirectory()) {
            for(File file : readFile.listFiles()) {
                reader(file);
            }
        }else {
            reader(readFile);
        }

    }

    static void reader(File file){
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
            alwaysTaken(branch, taken);
        }
        
    }

    static void alwaysTaken(ArrayList<String> branch, ArrayList<Integer> taken){
        int correct = 0;

        for(int i = 0; i < taken.size(); i++){
            if(taken.get(i) == 1){
                correct++;
            }
        }
        double difference = ((double) (taken.size() - correct) / taken.size()) * 100;
        //double difference = ((double) correct  / taken.size()) * 100;
        System.out.println(df.format(difference));
    }

}