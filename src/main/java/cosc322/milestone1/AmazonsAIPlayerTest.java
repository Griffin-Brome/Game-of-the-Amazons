package cosc322.milestone1;

import java.util.ArrayList;
import java.util.Arrays;

import ygraph.ai.smartfox.games.BaseGameGUI;
import ygraph.ai.smartfox.games.amazons.HumanPlayer;

public class AmazonsAIPlayerTest {

	public static void main(String[] args) {

		AmazonsAIPlayer p1 = new AmazonsAIPlayer(args[0], args[1]);
//		AmazonsAIPlayer p2 = new AmazonsAIPlayer(args[2], args[3]);
		AmazonsAIPlayer p2 = new AmazonsAIPlayer("otherguy", "password");
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
