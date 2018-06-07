package rotl.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import rotl.entities.SoldierType;
import rotl.entities.SoldiersInfo;
import rotl.entities.SoldiersInfo.S_Info;

public final class XMLParser {

	private static final int MAP_WIDTH = 1000, MAP_HEIGHT = 400, NO_OF_LAYERS = 3;

	public static int[][][] loadXMLMaps(String path) {

		int[][][] layers = new int[MAP_HEIGHT][MAP_WIDTH][NO_OF_LAYERS];

		try {
			
			File inputFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse("resources" + inputFile);
			doc.getDocumentElement().normalize();
			NodeList nList = doc.getElementsByTagName("layer");

			for (int i = 0; i < NO_OF_LAYERS; ++i) {
				Node nNode = nList.item(i);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					String parts[] = (eElement.getElementsByTagName("data").item(0).getTextContent()).split(",");

					for (int j = 0; j < MAP_HEIGHT; ++j) {
						for (int k = 0; k < MAP_WIDTH; ++k) {
							layers[j][k][i] = Integer.parseInt((parts[j * MAP_WIDTH + k]).trim());
						}
					}
				}
			}

			return layers;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return layers;
	}

	public static int getNoOfLayers() {
		return NO_OF_LAYERS;
	}
	
	private static S_Info getSoldierInfo(Element node) {

		S_Info info = new S_Info();

		try {

			Element buy = (Element) node.getElementsByTagName("buy").item(0);
			Element upgrade = (Element) node.getElementsByTagName("upgrade").item(0);

			String[] attributes = { "life", "armor", "attack", "gold", "miss", "dodge", "critical" };

			/** Buy **/
			List<Integer> bStatus = new ArrayList<>();
			List<Double> uStatus = new ArrayList<>();

			for (String attr : attributes)
				bStatus.add(Integer.parseInt(buy.getElementsByTagName(attr).item(0).getTextContent().trim()));

			info.setBLife(bStatus.get(0));
			info.setBArmor(bStatus.get(1));
			info.setBAttack(bStatus.get(2));
			info.setBGold(bStatus.get(3));
			info.setBMiss(bStatus.get(4));
			info.setBDodge(bStatus.get(5));
			info.setBCritical(bStatus.get(6));

			/** Upgrade **/

			for (String attr : attributes)
				uStatus.add(Double.parseDouble(upgrade.getElementsByTagName(attr).item(0).getTextContent().trim()));

			info.setULife(uStatus.get(0));
			info.setUArmor(uStatus.get(1));
			info.setUAttack(uStatus.get(2));
			info.setUGold(uStatus.get(3));
			info.setUMiss(uStatus.get(4));
			info.setUDodge(uStatus.get(5));
			info.setUCritical(uStatus.get(6));

		} catch (Exception ex) {

			System.err.println("Wrong Soldiers XML format !!!");
			ex.printStackTrace();
			return null;
		}

		return info;
	}

	private static void parseTypes(Node sTypes) {

		NodeList list = sTypes.getChildNodes();
		SoldiersInfo sInfo = SoldiersInfo.getInstance();

		for (int i = 0; i < list.getLength(); i++) {

			Node node = list.item(i);

			if (node.getNodeType() == Node.ELEMENT_NODE) {

				S_Info info = getSoldierInfo((Element) node);

				if (info != null) {

					switch (node.getNodeName()) {

					case "defender":
						sInfo.addSoldierInfo(SoldierType.DEFENDER, info);
						break;
					case "fighter":
						sInfo.addSoldierInfo(SoldierType.FIGHTER, info);
						break;
					case "warrior":
						sInfo.addSoldierInfo(SoldierType.WARRIOR, info);
						break;
					default:
						break;
					}
				}
			}
		}
	}

	public static void parseSoldiersInfo(String path) {

		try {

			File inputFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			Element root = doc.getDocumentElement();
			NodeList list = root.getChildNodes();

			for (int i = 0; i < list.getLength(); i++) {

				Node node = list.item(i);

				if (node.getNodeType() == Node.ELEMENT_NODE) {

					switch (node.getNodeName()) {

					case "types":
						parseTypes(node);
						break;
					default:
						break;
					}
				}
			}

		} catch (Exception ex) {

			System.err.println("Soldiers XML parsing error !!!");
			ex.printStackTrace();
		}
	}
}
