package cosc322.amazons;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

public class AmazonsAIPlayerTest {

	public static void main(String[] args) {

		AmazonsAIPlayer p1 = new AmazonsAIPlayer("oops", "pwd", 500);
		AmazonsAIPlayer p2 = new AmazonsAIPlayer("otherguy", "password", 500);
//		HumanPlayer p1 = new HumanPlayer();
//		HumanPlayer p2 = new HumanPlayer();

		if (p1.getGameGUI() == null) {
			p1.Go();
		} else {
			BaseGameGUI.sys_setup();
			java.awt.EventQueue.invokeLater(new Runnable() {
				public void run() {
					p1.Go();
					p2.Go();
				}
			});
		}
		

	}

}
