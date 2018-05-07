package gothos.FormCore;

public class SelectboxItem {

	protected Object key;
	protected Object value;

	public Object getKey() {
		return key;
	}

	public void setKey(Object key) {
		this.key = key;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	public SelectboxItem(Object key, Object value){
		this.key = key;
		this.value = value;
	}

	@Override
	public String toString() {
		String string = "";

		if(value != null){
			try {
				string = (String) value;
			}catch (ClassCastException e){

			}
		}

		return string;
	}
}
