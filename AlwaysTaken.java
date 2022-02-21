import java.io.File;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner;

class AlwaysTaken {

    final static DecimalFormat df = new DecimalFormat("0.00");//Format used to print all averages
    private static int fileCount = 0;
    private static float totalAverage = 0;

    public static void main(String [] args){
        
        if(args.length != 1){
            System.out.println("Proper Usage: java AlwaysTaken <individual file or directory of files>");
            System.exit(0);
        }

        //https://stackoverflow.com/questions/25536845/how-to-run-a-java-program-on-all-files-in-a-directory-from-command-line
        File readFile = new File(args[0]);
        if(readFile.isDirectory()) {
            for(File file : readFile.listFiles()) {
                reader(file);//Reader method called on each file
            }
            System.out.println("\nAVERAGE: " + df.format(totalAverage/fileCount) + "%");
        }else {
            reader(readFile);//Reader method called on specific file in command line
        }

    }

    static void reader(File file){
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
            System.out.println(filenameArray[2] + ": " + df.format(alwaysTaken(branch, taken)) + "%");//Output of the strategy method
            fileCount++;
        }
        
    }

    static double alwaysTaken(ArrayList<String> branch, ArrayList<Integer> taken){
        int correct = 0;

        //For every branch encountered, predict always taken
        for(int i = 0; i < branch.size(); i++){
            if(taken.get(i) == 1){
                correct++;
            }
        }

        double difference = ((double) (taken.size() - correct) / taken.size()) * 100;//Misprediction rate is calculated
        //double difference = ((double) correct  / taken.size()) * 100;
        totalAverage += difference;
        return difference;
    }

}