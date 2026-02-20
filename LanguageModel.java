import java.util.HashMap;
import java.util.Random;


public class LanguageModel {

    // The map of this model.
    // Maps windows to lists of charachter data objects.
    HashMap<String, List> CharDataMap;
    
    // The window length used in this model.
    int windowLength;
    
    // The random number generator used by this model. 
	private Random randomGenerator;

    /** Constructs a language model with the given window length and a given
     *  seed value. Generating texts from this model multiple times with the 
     *  same seed value will produce the same random texts. Good for debugging. */
    public LanguageModel(int windowLength, int seed) {
        this.windowLength = windowLength;
        randomGenerator = new Random(seed);
        CharDataMap = new HashMap<String, List>();
    }

    /** Constructs a language model with the given windowgit  length.
     * Generating texts from this model multiple times will produce
     * different random texts. Good for production. */
    public LanguageModel(int windowLength) {
        this.windowLength = windowLength;
        randomGenerator = new Random();
        CharDataMap = new HashMap<String, List>();
    }

    /** Builds a language model from the text in the given file (the corpus). */
	public void train(String fileName) {
		// Your code goes here
    String str = "";
    char c;

    In in = new In(fileName);
    while ((!in.isEmpty()) && (str.length() < windowLength)){
        c = in.readChar();
        str += c;
    }
    while (!in.isEmpty()) {
        c = in.readChar();

        List listt = CharDataMap.get(str);
        if(listt == null){
            listt = new List();
            CharDataMap.put(str, listt);
        }
        listt.update(c);

        str += c;
        str = str.substring(1, str.length());
    }
    for(List lists : CharDataMap.values()){
        calculateProbabilities(lists);
    }
}

    // Computes and sets the probabilities (p and cp fields) of all the
	// characters in the given list. */
	void calculateProbabilities(List probs) {	
        int numberOfSymbols = 0;
        for(int i = 0; i< probs.getSize(); i++){
            numberOfSymbols += probs.get(i).count;
        }
        for(int i = 0; i < probs.getSize(); i++){
            probs.get(i).p = probs.get(i).count/(double)numberOfSymbols;
            probs.get(i).cp = probs.get(i).p + (i > 0 ? probs.get(i-1).cp : 0);
        }
	}

    // Returns a random character from the given probabilities list.
	char getRandomChar(List probs) {
		// Your code goes here
        double r = randomGenerator.nextDouble();
        char charBack = ' ';
        for(int i =0; i < probs.getSize(); i++){
            if(r < probs.get(i).cp){
                charBack = probs.get(i).chr;
                break;
            }
        }
		return charBack;
	}

    /**
	 * Generates a random text, based on the probabilities that were learned during training. 
	 * @param initialText - text to start with. If initialText's last substring of size numberOfLetters
	 * doesn't appear as a key in Map, we generate no text and return only the initial text. 
	 * @param numberOfLetters - the size of text to generate
	 * @return the generated text
	 */
	public String generate(String initialText, int textLength) {
        if (initialText.length()< windowLength){
            return initialText;
        }
        String text = initialText;
        while (text.length() < textLength) {
            String str = text.substring(text.length()-windowLength);
            List listt = CharDataMap.get(str);
            if(listt == null){
                return text;
            }
            text += getRandomChar(listt);
        }
        return text;
	}

    /** Returns a string representing the map of this language model. */
	public String toString() {
		StringBuilder str = new StringBuilder();
		for (String key : CharDataMap.keySet()) {
			List keyProbs = CharDataMap.get(key);
			str.append(key + " : " + keyProbs + "\n");
		}
		return str.toString();
	}

    public static void main(String[] args) {
		int windowLength = Integer.parseInt(args[0]);
        String initialText = args[1];
        int generatedTextLength = Integer.parseInt(args[2]);
        Boolean randomGeneration = args[3].equals("random");
        String fileName = args[4];

        // Create the LanguageModel object
        LanguageModel lm;
        if (randomGeneration)
            lm = new LanguageModel(windowLength);
        else
            lm = new LanguageModel(windowLength, 20);

        // Trains the model, creating the map.
        lm.train(fileName);

        // Generates text, and prints it.
        System.out.println(lm.generate(initialText, generatedTextLength));
    }
}
