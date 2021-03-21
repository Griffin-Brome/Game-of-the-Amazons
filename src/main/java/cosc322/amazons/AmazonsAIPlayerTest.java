package cosc322.amazons;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

import java.io.IOException;

public class AmazonsAIPlayerTest {

	public static void main(String[] args) throws IOException {
			AmazonsAIPlayer p1 = new AmazonsAIPlayer("oops", "pwd", 0);
			AmazonsAIPlayer p2 = new AmazonsAIPlayer("otherguy", "password", true);
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
