package noiseReduction;

public class Image {
	private int img_id;
	private int num_objects;
	private int[] labels = new int[2];
	private int[] xmax = new int[2];
	private int[] xmin = new int[2];
	private int[] ymax = new int[2];
	private int[] ymin = new int[2];
	
	public Image(int ID, int label, int xmin, int xmax, int ymin, int ymax){
		this.img_id = ID;
		this.labels[0] = label;
		this.xmin[0] = xmin;
		this.xmax[0] = xmax;
		this.ymin[0] = ymin;
		this.ymax[0] = ymax;
		num_objects = 1;
	}
	
	public void addObject(int label, int xmin, int xmax, int ymin, int ymax) {
		//records new objects to the image by updating its instance variables
		this.labels[1] = label;
		this.xmin[1] = xmin;
		this.xmax[1] = xmax;
		this.ymin[1] = ymin;
		this.ymax[1] = ymax;
		num_objects = 2;
	}
	
	public String toString() {
		String output = "" + img_id + ": ";
		for(int i=0; i<num_objects ;i++) {
			output += labels[i] + " ";
			output += xmin[i] + " ";
			output += xmax[i] + " ";
			output += ymin[i] + " ";
			output += ymax[i] + " ";
			output += "|" + " ";
		}
		
		return output;
	}
	
	
	
	
	
	
	
	
	
	//****************************
	//	All getters and setters
	//****************************
	public int[] getYmin() {
		return ymin;
	}
	public void setYmin(int[] ymin) {
		this.ymin = ymin;
	}
	public int[] getYmax() {
		return ymax;
	}
	public void setYmax(int[] ymax) {
		this.ymax = ymax;
	}
	public int[] getXmin() {
		return xmin;
	}
	public void setXmin(int[] xmin) {
		this.xmin = xmin;
	}
	public int[] getXmax() {
		return xmax;
	}
	public void setXmax(int[] xmax) {
		this.xmax = xmax;
	}
	public int[] getLabels() {
		return labels;
	}
	public void setLabels(int[] labels) {
		this.labels = labels;
	}
	public int getNum_onjects() {
		return num_objects;
	}
	public void setNum_onjects(int num_onjects) {
		this.num_objects = num_onjects;
	}
	public int getImg_id() {
		return img_id;
	}
	public void setImg_id(int img_id) {
		this.img_id = img_id;
	}
}
