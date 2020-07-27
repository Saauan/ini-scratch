package ini.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The object representing a list in INI
 *
 */
public class IniList {

	private List<Object> internalList;
	
	public IniList(VirtualFrame frame, Object[] elements){
		internalList = Arrays.asList(elements);
	}
	
	public int getSize() {
		return internalList.size();
	}
	
	public Object getElementAt(int index) {
		return internalList.get(index);
	}
	
	public String toString() {
		StringBuilder msg = new StringBuilder("[");
		int i;
		for(i = 0; i<getSize()-1; i++) {
			msg.append(getElementAt(i) + ", ");
		}
		msg.append(getElementAt(i) + "]");
		return msg.toString();
	}
}
