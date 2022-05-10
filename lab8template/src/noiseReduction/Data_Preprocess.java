package noiseReduction;

import java.nio.file.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;
import java.util.NoSuchElementException;
import java.lang.SecurityException;
import java.lang.IllegalStateException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.FormatterClosedException;
import java.util.Iterator;
import java.lang.Integer;
import java.lang.StringBuilder;

public class Data_Preprocess {
	
	public static ArrayList<Image> images = new ArrayList<>();
	
	private static Matcher matcher;
	private static Pattern idPattern = Pattern.compile("(img_id:|img_iD:|img_Id:|img_ID:|Img_id:|Img_iD:|Img_Id:|Img_ID:|image_id:|image_iD:|image_Id:|image_ID:|Image_id:|Image_iD:|Image_Id:|Image_ID:)\s+\\b[0-9]{1,4}\\b");
	private static Pattern labelPattern = Pattern.compile("(Label:|label:)\s+\\b[0-9]{1,3}\\b");
	private static Pattern x1Pattern = Pattern.compile("(X1:|x1:|XMin:|Xmin:|xMin:|xmin:)\s+\\b[0-9]{1,4}\\b");
	private static Pattern x2Pattern = Pattern.compile("(X2:|x2:|XMax:|Xmax:|xMax:|xmax:)\s+\\b[0-9]{1,4}\\b");
	private static Pattern y1Pattern = Pattern.compile("(Y1:|y1:|YMin:|Ymin:|yMin:|ymin:)\s+\\b[0-9]{1,4}\\b");
	private static Pattern y2Pattern = Pattern.compile("(Y2:|y2:|YMax:|Ymax:|yMax:|ymax:)\s+\\b[0-9]{1,4}\\b");
	//declaring an array for ease of use
	private static Pattern[] patterns= {idPattern, labelPattern, x1Pattern, x2Pattern, y1Pattern, y2Pattern};
	
	public static void main(String[] args) {
		//preLab();
		
		inLab();
	}
	
	private static void inLab() {
		//locate directory by asking for user input
		Path path2dir;
		while(true) {
			try{
				path2dir = getDirectory();
				break;
			}
			catch(FileNotFoundException e) {
				System.out.println("There is no such directory, please try again");
			}
		}
		//save file paths to a list
		ArrayList<Path> filePaths = getFilePaths(path2dir);
		//only keep that are on April or May
		filePaths = validFiles(filePaths);
		
		for(Path path2file : filePaths) {
			processFile(path2file);
		}
		 
	}
	
	private static Path getDirectory() throws FileNotFoundException {
		System.out.printf("Enter file or directory name:%n");
		Scanner input = new Scanner(System.in);
		String directory = input.nextLine();
		Path path2dir = Paths.get("src").toAbsolutePath();
		path2dir = Paths.get(path2dir.toString(), directory);
		if(Files.exists(path2dir) == false)
			throw new FileNotFoundException();
		return path2dir;
	}
	
	private static ArrayList<Path> getFilePaths(Path path2dir) {
		
		ArrayList<Path> filePaths = new ArrayList<>();
		try (Stream<Path> paths = Files.walk(path2dir)) {
			Iterator<Path> fileWalker = paths.iterator();
			//temp path variable
			Path pathTmp;
		    while(fileWalker.hasNext()) {
		    	pathTmp = fileWalker.next();
		    	if(pathTmp.toString().matches("^.*\\.(txt|pic)"))
		    		filePaths.add(pathTmp);
		    }
		    return filePaths;
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return filePaths;
		}
	}
	
	private static ArrayList<Path> validFiles(ArrayList<Path> allPaths) {
		ArrayList<Path> validFiles = new ArrayList<>();
		Pattern datePattern = Pattern.compile("\\d{1,2}-[45]{1,2}-\\d{4}");
		for(Path path : allPaths) {
			matcher = datePattern.matcher(path.getFileName().toString());
			if(matcher.find())
				validFiles.add(path);
		}
		return validFiles;
	}
	
	private static void processFile(Path path) {
		//initially the program assumed there was only one file to process
		//to be able to use the existing code without different processes' images altering
		//each other, processFile copies images to a backup ArrayList, deletes images' contents
		//and uses it as if there are no other searches.
		//finally, processFile appends its original contents back to images.
		ArrayList<Image> imagesLocal = new ArrayList<Image>();
		imagesLocal.addAll(images);
		images.clear();
		
		StringBuilder builder = new StringBuilder();
		int total;
		int valid = 0;
		System.out.printf("Processing file: %s%n", path.getFileName());
		
		Path path2Write =  Paths.get("").toAbsolutePath();
		path2Write = Paths.get(path2Write.toString(), "src/processed_directory");
		path2Write = Paths.get(path2Write.toString(), "processed_" + path.getFileName().toString());

		ArrayList<String> labels = readFile(path);
		total = labels.size();
		
		//fill the images arraylist with valid labels
		for(String label : labels) {
			if(processLabel(label))
				valid++;
		}
		//construct a string to print
		for(Image image: images) {
			builder.append(image.toString());
			builder.append(System.getProperty("line.separator"));
		}
		
		System.out.printf("Total Labels count: %d%n", total);
		System.out.printf("Valid Labels count: %d%n", valid);
		System.out.printf("Invalid Labels count: %d%n", total-valid);

		//save the data
		writeFile(path2Write, builder);
		System.out.printf("Succesfully created:%s%n", path2Write.getFileName());
		images.addAll(imagesLocal);
	}
		
	private static void preLab() {
		StringBuilder builder = new StringBuilder();
		int total;
		int valid = 0;
		
		Path path = Paths.get("").toAbsolutePath();
		path = Paths.get(path.toString(), "src/labels.txt");
		Path path2Write =  Paths.get("").toAbsolutePath();
		path2Write = Paths.get(path2Write.toString(), "src/labels_processed.txt");
		
		ArrayList<String> labels = readFile(path);
		total = labels.size();
		
		//fill the images arraylist with valid labels
		for(String label : labels) {
			if(processLabel(label))
				valid++;
		}
		//construct a string to print
		for(Image image: images) {
			builder.append(image.toString());
			builder.append(System.getProperty("line.separator"));
		}
		//save the data
		System.out.println(builder);
		writeFile(path2Write, builder);
	}
		
	private static ArrayList<String> readFile(Path filePath){
		ArrayList<String> lines = new ArrayList<>();		
		try {
			Scanner readFile = new Scanner(Paths.get(filePath.toUri()));
			while(readFile.hasNextLine()) {
				lines.add(readFile.nextLine());
			}
			readFile.close();
			return lines;			
		}
		catch(IOException | NoSuchElementException | IllegalStateException e) {
			System.err.println("File path you have given isn't valid");
			e.printStackTrace();
			return lines;			
		}
	}
	
	private static ArrayList<String> readFile(String fileName){
		
		ArrayList<String> lines = new ArrayList<>();		
		try {
			Scanner readFile = new Scanner(Paths.get(fileName));
			while(readFile.hasNextLine()) {
				lines.add(readFile.nextLine());
			}
			readFile.close();
			return lines;
		}
		catch(IOException | NoSuchElementException | IllegalStateException e) {
			System.err.println("File path you have given isn't valid");
			e.printStackTrace();
			return lines;			
		}
	}
	
	private static void writeFile(Path path, StringBuilder str) {
		try {
			//System.out.print(path);
			Formatter output = new Formatter(path.toString());
			output.format("%s", str);
			output.close();
		}
		catch(FileNotFoundException | SecurityException | FormatterClosedException e) {
			System.err.println("File path you have given isn't valid");
			e.printStackTrace();
		}
	}
	
	private static boolean processLabel(String label) {
		if(isLabelValid(label) == false) {
			return false;
		}
		int[] tokens = getTokens(label);
		if(isImageNew(tokens[0]) == true) {
			//create image
			images.add( new Image(tokens[0], tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]));
		}
		else {
			//add object to image
			for(Image image : images) {
				if(image.getImg_id() == tokens[0]) {
					image.addObject(tokens[1], tokens[2], tokens[3], tokens[4], tokens[5]);
				}
			}
		}
		
		return true;
	}
	
	private static boolean isLabelValid(String label) {
		//this function checks for imgID, Label, x1, x2, y1, y2 one by one. If any of them are missing it returns false
		//System.out.printf("%n%n");
		for(Pattern pattern : patterns) {
			//System.out.println(label);
			//System.out.println(pattern);
			matcher = pattern.matcher(label);
			if(matcher.find() == false) {				
				//System.out.printf("%nXXXxxXXxx: %s%n", label.substring(0, 20));
				return false;
			}
			else {				
				//System.out.println(matcher.group());
				//System.out.print("||");
			}
		}
		return true;
	}

	private static int[] getTokens(String label) {
		//tokens are stored in an array in the order of:
		//id, label, x1, x2, y1, y2
		String tempStr;
		int[] tokens = new int[patterns.length];
		for(int i=0; i<patterns.length ;i++) {
			matcher = patterns[i].matcher(label);
			if(matcher.find()) {
				tempStr = matcher.group();
				tempStr = tempStr.replaceAll("^.*:\s*", "");
				tokens[i] = Integer.parseInt(tempStr);
			}
		}
		return tokens;
	}
	
	private static boolean isImageNew(int ID) {
		for(Image image : images) {
			if(image.getImg_id() == ID) {
				return false;
			}
		}
		return true;
	}
}

























