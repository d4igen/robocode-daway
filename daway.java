package daway;//pacote do meu robo


import robocode.*;//importação do robocode
import static robocode.util.Utils.normalRelativeAngleDegrees; //torna os angulos normais em relativos, permitindo a movimentação em espiral
import java.awt.*;//importação padrão gráfica do java


/**
 * A ideia inicial era criar um robô que conseguisse dificultar o contato inimigo pela arena, enquanto deixa uma quantidade de dano considerável em campo.
 * Essa ideia surgiu por um motivo:
 * Minecraft. Há uns anos atrás, o Matheus, jogava Minecraft diariamente contra outras pessoas. A tática mais usada era justamente essa, de bater em forma de espiral ao redor do inimigo, dificultando seu contato.
 * 
 * 
 * O código é uma adaptação ao nosso campeonato (configuração de arena e combate contra robôs) do código do  ~ CirclingBot ~ , feito em 2011 pelo Hapiel.
 * 
 * 
 * Explicacao boolean:
 * "A função do operador booleano é, portanto, ajudar os sistemas a definirem melhor os parâmetros de seleção de dados."
 * O que isso quer dizer:
 * A variável boolean permite uma organização melhor da definição de parâmetros, permitindo assim, uma complexidade maior na hora de definir valores.
 * 
 * 
 * daway - a class by (matheus henrique e davi)
 * extends AdvancedRobot > assim como a classe normal "robot", porém com alguns comandos a mais, + especificos
*/

public class dawaybot extends AdvancedRobot {
	boolean movingForward;
	boolean inWall; // // coloca como "true" assim que o robo esta perto da parede, possibilitando determinar comandos como onHitWall.
// O comando "inwall = true" vai definir se ele está distante 50px, senão (else) "inwall = false"

	public void run() {
		// Set colors
		setColors(Color.white,Color.black,Color.red,Color.orange,Color.green);
		

		//	Torna cada elemento do robo independente
		setAdjustRadarForRobotTurn(true);
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		
	// Como eu não quero que o robo acerte a parede ou enfrente algo relacionado a parede, o robo continuamente fica checando se está a 50px da parede
		if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50) {
				inWall = true;
			} else {
			inWall = false;
		}
		
		setAhead(30000); // anda pra frente ate receber outro comando
		setTurnRadarRight(360); // scaneia ate achar inimigo
		movingForward = true; // define andar pra frente como = true
		
		while (true) {
			/**
 			* perto da parede, voltar de ré
			* longe da parede, não fazer nada
 			*/
			if (getX() > 50 && getY() > 50 && getBattleFieldWidth() - getX() > 50 && getBattleFieldHeight() - getY() > 50 && inWall == true) {
				inWall = false;
			}
			if (getX() <= 50 || getY() <= 50 || getBattleFieldWidth() - getX() <= 50 || getBattleFieldHeight() - getY() <= 50 ) {
				if ( inWall == false){
					reverseDirection();
					inWall = true;
				}
			}

			// garante que se o radar parar, o robo vai procurar outro inimigo
			if (getRadarTurnRemaining() == 0.0){
			setTurnRadarRight(360);
			}
			
			execute(); // executa
			
		}
	}
	
		

	public void onHitWall(HitWallEvent e) {
		reverseDirection();
	}

	/**
	 * reverseDirection:  Se atingir a parede, mesmo que a chance seja pequena, é melhor garantir, ele vai para a direção contraria. troca entre andar pra frente e pra tras
 */
	public void reverseDirection() {
		if (movingForward) {
			setBack(40000);
			movingForward = false;
		} else {
			setAhead(40000);
			movingForward = true;
		}
	}

	
	public void onScannedRobot(ScannedRobotEvent e) {
		// calcula a localizacao exata do robo inimigo escaneado
		double absoluteBearing = getHeading() + e.getBearing();
		double bearingFromGun = normalRelativeAngleDegrees(absoluteBearing - getGunHeading());
		double bearingFromRadar = normalRelativeAngleDegrees(absoluteBearing - getRadarHeading());
		
		//faz uma espiral em volta do inimigo, se colocassemos 90 graus, ia ser paralelo sempre
		// 50 e 100 nos valores, faz o dawaybot se mover de pouco em pouco para frente
		if (movingForward){
			setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 50));
		} else {
			setTurnRight(normalRelativeAngleDegrees(e.getBearing() + 100));
		}
		

		// perto o suficiente? atire
		if (Math.abs(bearingFromGun)<=4) {
			setTurnGunRight(bearingFromGun); 
			setTurnRadarRight(bearingFromRadar);	
					//mantem o radar no inimigo
			}
			
		if (getGunHeat() == 0 && getEnergy() > .2) { //configuração do tiro (o pacifismo termina aqui.)
				fire(Math.min(4.5 - Math.abs(bearingFromGun) / 2 - e.getDistance() / 250, getEnergy() - .1));
			} 
			
		else {
			setTurnGunRight(bearingFromGun);
			setTurnRadarRight(bearingFromRadar);
		}
		// gera outro scan, se ver outro robo
		// AVISO -----------------> É SÓ UMA PRECAUÇÃO, CASO O RADAR FALHE!
		if (bearingFromGun == 4) {
			scan();
		}
	}		
	/**
	 * onHitRobot:  recua
	 */
	public void onHitRobot(HitRobotEvent e) {
		// se colidir com outro robo, volta.
		if (e.isMyFault()) {
			reverseDirection();
		}
	}
}
