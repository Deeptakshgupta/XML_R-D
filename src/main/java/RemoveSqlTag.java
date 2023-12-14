import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sound.sampled.SourceDataLine;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RemoveSqlTag {

	static String sqlTag = "Sql";
	static String compName = "ComponentName";
	static Map<String, String> sqlIdsMap = new HashMap<>();
	
//**************************************************************************************************************************************************//	

	public static void removeSQLs(String componentName, String destinationFilePath, Document templateDoc,
			Transformer transformer, StreamResult result) throws Exception {
		// find the layer tag w.r.t component name
		int index = -1;

		NodeList layers = templateDoc.getElementsByTagName("Layer");

		for (int i = 0; i < layers.getLength(); i++) {
			Element layer = (Element) layers.item(i);
			if (checkComponentName(layer, componentName)) {
				index = i;
				break;
			}
		}

		Node layer_to_delete = layers.item(index);
		
		/** storing id vice indexes in the map*/
		putSqlIdInMap(templateDoc);
		
		NodeList sqls = ((Element) layer_to_delete).getElementsByTagName("SqlId");
		
		
		// removing the references of the sql from the dfl tag 
		for (int i = 0; i < sqls.getLength(); i++) {
		
//			remove_refered_sqlTag
			remove_refered_sqlTag(templateDoc, sqls.item(i) );
			
			System.out.println("Trying to remove the Sql tag with id:" +sqls.item(i).getTextContent());
			
			
		}

		System.out.println(((Element) layers.item(index)).getElementsByTagName("ComponentName").item(0).getTextContent());
		layer_to_delete.getParentNode().removeChild(layer_to_delete);

//		templateDoc.normalize();
//		result = new StreamResult(new File(destinationFilePath));
//		transformer.transform(new DOMSource(templateDoc), result);

	}

	//**************************************************************************************************************************************************//	
	
	private static boolean checkComponentName(Element layerElement, String value) throws Exception {

		NodeList requiredNode = layerElement.getElementsByTagName(compName);
		Node component = requiredNode.item(0);
		System.out.println(component.getTextContent());
		if (component.getTextContent().equals(value)) {
			System.out.println("Component Found");
			return true;

		}

		return false;
	}
	
	//**************************************************************************************************************************************************//

	/** add all sqlIds to a map and parent index reference*/
	public static void putSqlIdInMap(Document sourceDocument) {
		NodeList dflList = sourceDocument.getElementsByTagName("Sqls");

		Node sqlsList = sourceDocument.getElementsByTagName("Sqls").item(0);
		Element sqls = (Element) sqlsList;
		NodeList sqlIds = sqls.getElementsByTagName("SqlId");
		System.out.println(sqlIds.getLength());
		for (int i = 0; i < sqlIds.getLength(); i++) 
		{
			sqlIdsMap.put(sqlIds.item(i).getTextContent(), String.valueOf(i));
		}
	}
	
	//**************************************************************************************************************************************************//
/** remover the referred SQL tags fromDFL using the hashMap and getting the index of the parent tag from the map value*/
	public static boolean remove_refered_sqlTag(Document sourceDocument, Node sqlId) {
		NodeList dflList = sourceDocument.getElementsByTagName("Sqls");
		
		
		Node sqlsList = sourceDocument.getElementsByTagName("Sqls").item(0);
		Element sqls = (Element) sqlsList;
		NodeList sqlIds = sqls.getElementsByTagName("SqlId");
		System.out.println(sqlIds.getLength());
		if(sqlIdsMap.containsKey(sqlId.getTextContent()))
		{
//			System.out.println(sqlIdsMap.get(sqlId.getTextContent()));
			System.out.println("check indexes");
			System.out.println(sqlIds.item(Integer.valueOf(sqlIdsMap.get(sqlId.getTextContent()))).getParentNode().getTextContent());
		}
//		for (int i = 0; i < sqlIds.getLength(); i++) 
//		{
//			System.out.println(sqlIds.item(i).getParentNode().getNodeName());
//		}
		return false;
	}
	
	//**************************************************************************************************************************************************//
	public static void main(String[] args)
			throws TransformerException, IOException, ParserConfigurationException, SAXException {
		String templatePath = "3compTemplate.xml";
		DocumentBuilderFactory destinationFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder destinationBuilder = destinationFactory.newDocumentBuilder();
		Document templateDocument = destinationBuilder.parse(new File(templatePath));
		/** empty template to data copied to Template xml for first Time **/
		try {
			removeSQLs("ALCAPIM_SAA", templatePath, templateDocument, null, null);
			
			for(Map.Entry<String,String> map : sqlIdsMap.entrySet())
			{
				System.out.println(map.getKey()+","+map.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//**************************************************************************************************************************************************//
	/*
	 * private static void traverseXmlTree(Node node) {
	 * 
	 * NodeList childNodes = node.getChildNodes();
	 * 
	 * for (int i = 0; i < childNodes.getLength(); i++) { if
	 * (childNodes.item(i).getNodeType() == node.TEXT_NODE) continue;
	 * 
	 * else { System.out.
	 * println("-----------Entry Tag-------------------------------------" +
	 * node.getNodeName()); System.out.println("Entering into: " +
	 * childNodes.item(i).getNodeName() + " Node");
	 * 
	 * traverseXmlTree(childNodes.item(i)); } } if (node instanceof Element) { //
	 * 
	 * System.out.println("Node name: " + node.getNodeName()); if
	 * (node.getTextContent() != null) System.out.println("Node value: " +
	 * node.getTextContent());// values else System.out. System.out.
	 * System.out.println(
	 * "Null Check Don't Need to Traverse, Also break recurive calls of again printing the child node values that were already printed"
	 * );
	 * 
	 * System.out.println("Child Node:" + node.getChildNodes());
	 * 
	 * }
	 * System.out.println("-----------Exit Tag-------------------------------------"
	 * + node.getNodeName()); }
	 */
}
