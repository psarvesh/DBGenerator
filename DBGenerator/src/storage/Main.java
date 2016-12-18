package storage;

public class Main {

	public static void main(String[] args) {
		boolean isIndex = false;
		String dir = null, inputDir = null;
		try{
			if(args.length == 3){
				inputDir = args[2];
				dir = args[1];
				int which = Integer.parseInt(args[0]);
				if(which == 0){
					isIndex = false;
				} else if(which == 1){
					isIndex = true;
				} else {
					return;
				}
			} else {
				System.out.println("Usage: java -jar jarfile args");
				System.out.println("args: <indexer/pr> <output-dir> <input db-dir> ");
				System.exit(1);
			}
		} catch(Exception e){
			System.out.println("Usage: java -jar jarfile args");
			System.out.println("args: <indexer/pr> <output-dir> <input-dir> ");
			System.exit(1);
		}
		if(isIndex){
			
			
		} else {
			OutputDBWrapper outDB = new OutputDBWrapper(dir);
			InputDBWrapper inDB = new InputDBWrapper(inputDir);
			
		}
		
	}

}
