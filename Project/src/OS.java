import java.util.ArrayList;

public class OS {
	Memory mem = new Memory();
	Interpreter inte = new Interpreter();
	Scheduler sch = new Scheduler();
	public void execute(String program) {
		int pc = -1;
		int Start = start(program);
		int End = end(program);
		for (int i = Start; i < End;i++) {
			String word = (String) this.mem.word.get(i);
			if (word.contains("PC")) {
				String[] words = word.split(" ");
				pc = Integer.parseInt(words[1]);
				break;
			}
		}
		String word = (String) this.mem.word.get(pc);
		System.out.println(word);
		int firstSpaceIndex = word.indexOf(" ");
		String result = word.substring(firstSpaceIndex + 1);
		this.inte.interpret(result, this.mem, Start, End, sch, program);
		updatePC(pc+=1, Start, End);
		String lastLine = lastLine(Start, End);
		if (lastLine.equals(word))
			sch.finishedQ.add(program);
	}
	public int start(String processID) {
		int Start = -1;
		for (int i = 0; i < this.mem.word.size();i++) {
			if (this.mem.word.get(i) instanceof String) {
				String word = (String) this.mem.word.get(i);
				if (word.equals(processID)) {
					String[] words = ((String) this.mem.word.get(i+3)).split(" ");
					Start = Integer.parseInt(words[0]);
					break;
				}
			}
		}
		return Start;
	}
	public int end(String processID) {
		int End = -1;
		for (int i = 0; i < this.mem.word.size();i++) {
			if (this.mem.word.get(i) instanceof String) {
				String word = (String) this.mem.word.get(i);
				if (word.equals(processID)) {
					String[] words = ((String) this.mem.word.get(i+3)).split(" ");
					End = Integer.parseInt(words[1]);
					break;
				}
			}
		}
		return End;
	}
	public String lastLine(int Start, int End) {
		String result = "";
		for (int i = Start; i < End; i++) {
			if (this.mem.word.get(i) instanceof String) {
				String word = (String) this.mem.word.get(i);
				if (word.contains("Line")) {
					result = word;
				}
			}
		}
		return result;
	}
	public void updatePC(int newValue, int Start, int End) {
		for (int i = Start; i < End;i++) {
			String word = (String) this.mem.word.get(i);
			if (word.contains("PC")) {
				this.mem.word.set(i, "PC " + newValue);
				break;
			}
		}
	}
	public void clk() {
		for (int i = 0; i < 35; i++) {
			if (i == 0) {
				sch.readyQ.add("Program_1.txt");
				sch.running = sch.readyQ.remove();
				this.mem.readProgram("Program_1.txt");
			}
			else if (i == 1) {
				sch.readyQ.add("Program_2.txt");
				this.mem.readProgram("Program_2.txt");
			}
			else if (i == 4) {
				sch.readyQ.add("Program_3.txt");
				this.mem.readProgram("Program_3.txt");
			}
			if ((i)%Scheduler.timeSlice==0) {
				sch.readyQ.add(sch.running);
				sch.running = sch.readyQ.remove();
			}
			if (sch.finishedQ.size() == 3) {
				sch.running = "None";
				System.out.println("Running: "+sch.running);
				System.out.println("ReadQ"+Scheduler.queueToString(sch.readyQ));
				System.out.println("BlockedQ"+Scheduler.queueToString(sch.blockedQ));
				System.out.println("FinishedQ"+Scheduler.queueToString(sch.finishedQ));
				return;
			}
			System.out.println("Running: "+sch.running);
			System.out.println("ReadQ"+Scheduler.queueToString(sch.readyQ));
			System.out.println("BlockedQ"+Scheduler.queueToString(sch.blockedQ));
			System.out.println("FinishedQ"+Scheduler.queueToString(sch.finishedQ));
			execute(sch.running);
			removeFinishedPrograms();
			System.out.println("----------------------------------------------------------------------------------------------");
		}
	}
	 public void removeFinishedPrograms() {
        ArrayList<String> elementsToRemove = new ArrayList<>();
        for (String element : sch.finishedQ) {
            while (sch.readyQ.contains(element)) {
                sch.readyQ.remove(element);
                elementsToRemove.add(element);
            }
        }
	 }
	 
	public static void main (String [] args) {
		OS os = new OS();
		os.clk();
	}
}
