package ini.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.oracle.truffle.api.frame.VirtualFrame;

/**
 * The object representing a list in INI
 */
public class IniList {

	private List<Object> internalList;

	public IniList(VirtualFrame frame, Object[] elements) {
		internalList = new ArrayList<Object>(Arrays.asList(elements)); /*
																		 * if we did just Arrays.asList(elements), it
																		 * would not behave like a normal list.
																		 */
	}

	public int getSize() {
		return internalList.size();
	}

	public Object getElementAt(int index) {
		if (indexOutOfBounds(index)) {
			throw new IniException("Index out of bounds", null);
		}
		return internalList.get(index);
	}

	public Object setElementAt(int index, Object newValue) {
		if (indexOutOfBounds(index)) {
			if (index == getSize()) {
				internalList.add(newValue);
			} else {
				throw new IniException("Index out of bounds", null);
			}
		}
		internalList.set(index, newValue);
		return newValue;
	}

	private boolean indexOutOfBounds(int index) {
		return (index < 0) || (index >= getSize());
	}

	public String toString() {
		StringBuilder msg = new StringBuilder("[");
		int i;
		for (i = 0; i < getSize() - 1; i++) {
			msg.append(getElementAt(i) + ", ");
		}
		msg.append(getElementAt(i) + "]");
		return msg.toString();
	}
}
