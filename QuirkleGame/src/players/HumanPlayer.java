package players;

import views.View;

public class HumanPlayer extends Player {
	private View view;
	
	public HumanPlayer(String name, View view) {
		super(name);
		this.setView(view);
	}

	/**
	 * @return the view
	 */
	public View getView() {
		return view;
	}

	/**
	 * @param view the view to set
	 */
	public void setView(View view) {
		this.view = view;
	}


}
