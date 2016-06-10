import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Qnair{
	private static final ArrayList<Question> questions = new ArrayList<>();
	private static final ArrayList<DemoQuestion> demoQuestions = new ArrayList<>();
	private static final Set<String> rejectableVariables = new HashSet<>(Arrays.asList(new String[]{"TZONE", "LOC", "LDF", "LDE", "AREA", "FSA", "FSA1", "LANG", "IT2", "S1", "INT01", "INT99", "C3"}));
	public static String region = "";//Guess initially

	public static void clearQuestions(){
		questions.clear();
		demoQuestions.clear();
	}

	@SuppressWarnings("UnusedDeclaration")
	public static ArrayList<QuestionBase> getAllQuestions(){
		ArrayList<QuestionBase> temp = new ArrayList<QuestionBase>();
		temp.addAll(questions);
		temp.addAll(demoQuestions);
		return temp;
	}

	public static ArrayList<Question> getQuestions(){
		return questions;
	}

	public static ArrayList<DemoQuestion> getDemoQuestions(){
		return demoQuestions;
	}

	public static boolean isEmpty(){
		return questions.isEmpty() && demoQuestions.isEmpty();
	}

	public static void addQuestion(String var, int cw, String l, String ident, String pos, String skipCon, ArrayList<String[]> choices){
		questions.add(new Question(var, cw, l, ident, pos, skipCon, choices));
	}

	@SuppressWarnings("unused")
	public static void addDemoQuestion(String var, int cw, String l, String ident, String pos, String skipCon, ArrayList<String[]> choices){
		demoQuestions.add(new DemoQuestion(var, cw, l, ident, pos, skipCon, choices));
	}

	public static void addDemoQuestion(DemoQuestion dq){
		demoQuestions.add(dq);
	}

	//This also removes hear again choices
	public static void removeBadQuestions(){
		Iterator<Question> iter = questions.iterator();
		while(iter.hasNext()){
			Question q = iter.next();
			String variable = q.getVariable();

			//Remove questions with no label
			if(q.getLabel().isEmpty()){
				iter.remove();
				Logg.info("Removed " + variable + " - no label");
				continue;
			}

			//Remove questions with no choices
			if(q.getChoices().isEmpty()){
				iter.remove();
				Logg.info("Removed " + variable + " - no choices");
				continue;
			}

			//Remove questions with rejectable variable names
			if(rejectableVariables.contains(variable)){
				iter.remove();
				Logg.info("Removed " + variable + " - is rejectable");
				continue;
			}

			//Remove recruitment questions (hopefully only recruit)
			if(variable.charAt(0) == 'R'){
				iter.remove();
				Logg.info("Removed " + variable + " is recruit");
				continue;
			}

			removeHearAgain(q);
		}

		//Remove demo questions with no label
		Iterator<DemoQuestion> d_iter = demoQuestions.iterator();
		while(d_iter.hasNext()){
			DemoQuestion dq = d_iter.next();
			String variable = dq.getVariable();

			if(dq.getLabel().isEmpty()){
				d_iter.remove();
				Logg.info("Removed " + variable + " - no label");
				continue;
			}

			removeHearAgain(dq);
		}
	}

	//remove "hear again" or "Repeat answers" choice
	private static void removeHearAgain(QuestionBase bq){
		Iterator<String[]> c_iter = bq.getChoices().iterator();
		while(c_iter.hasNext()){
			String[] choice = c_iter.next();
			if(checkHearAgain(choice[1])){
				c_iter.remove();
				Logg.good("Removed 'hear again' choice in " + bq.getVariable());
			}
		}
	}

	private static boolean checkHearAgain(String lbl){
		String lowerLable = lbl.toLowerCase();
		return (lowerLable.contains("hear") && lowerLable.contains("again")) || (lowerLable.contains("repeat") && lowerLable.contains("answers"));
	}

	public static String guessRegion(){
		System.out.println(questions.get(0).getLabel());
		return "";
	}

	@SuppressWarnings("UnusedDeclaration")
	public static void printAll(){
		for(Question q : questions){
			printQuestion(q);
		}

		for(DemoQuestion dq : demoQuestions){
			printQuestion(dq);
		}
	}

	public static void printQuestion(QuestionBase q){
		System.out.println(q.getVariable());
		if(!q.getLabel().isEmpty())
			System.out.println(q.getLabel());
		else
			System.out.println("EMPTY LABEL");
		System.out.println(q.getIdentifier());
		System.out.println(q.getPosition());
		ArrayList<String[]> choices = q.getChoices();
		for(String[] c : choices)
			System.out.println(c[0] + "|--|" + c[1]);
		System.out.println();
	}
}
