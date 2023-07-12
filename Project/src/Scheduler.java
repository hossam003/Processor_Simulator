import java.util.LinkedList;
import java.util.Queue;

public class Scheduler {
	static int timeSlice = 2;
	Queue<String> readyQ =  new LinkedList<>();
	Queue<String> blockedQ =  new LinkedList<>();
	Queue<String> finishedQ =  new LinkedList<>();
	String running;
	public static String queueToString(Queue<?> queue) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        
        for (Object element : queue) {
            sb.append(element.toString());
            sb.append(", ");
        }

        if (!queue.isEmpty()) {
            sb.delete(sb.length() - 2, sb.length());
        }

        sb.append("]");
        return sb.toString();
    }
	public static void main (String [] args) {
		Scheduler sch = new Scheduler();
		sch.readyQ.add("HH");
		sch.readyQ.add("H1");
		sch.readyQ.add("Hs");
		System.out.print(queueToString(sch.readyQ));
	}
}
