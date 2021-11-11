
import java.util.EventObject;

//////https://www.youtube.com/watch?v=YIbA5YH1UeA

public class DetailEvent extends EventObject{

	private static final long serialVersionUID = -717625138112228665L;
	private String text;
	
	public DetailEvent(Object source, String text) {
		super(source);
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
}
