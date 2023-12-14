import java.io.File;
import java.io.IOException;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class DeleteLayerTag {
	

static String compTagName = "";
	public static void removeLayerTag(String componentName, String destinationFilePath, Document templateDoc,
			Transformer transformer, StreamResult result) throws TransformerException, IOException {
		// find the layer tag w.r.t component name
		int index =-1; 
		
		NodeList layers = templateDoc.getElementsByTagName("Layer");
		
		for( int i =0; i< layers.getLength(); i++) {
			Element layer = (Element)layers.item(i);
		if(checkComponentName_removeLayer(layer, compTagName, componentName))
			{
			index = i;
			break;
			}
		}
		
		Node layer_to_delete = layers.item(index);
		System.out.println(((Element)layers.item(index)).getElementsByTagName("ComponentName").item(0).getTextContent() );
		layer_to_delete.getParentNode().removeChild(layer_to_delete);
		
		templateDoc.normalize();
		result = new StreamResult(new File(destinationFilePath));
		transformer.transform(new DOMSource(templateDoc), result);

	}
	
	private static boolean checkComponentName_removeLayer (Element layerElement, String  compName, String value) {

		NodeList requiredNode = layerElement.getElementsByTagName(compName);
		Node component = requiredNode.item(0);
		System.out.println(component.getTextContent());
		if (component.getTextContent().equals(value))
		{	
			System.out.println("Component Found");
			return true;
			
		}

		return false;
	}
	
	public static void main(String[] args) throws Exception {
		
		String templatePath = "3compTemplate.xml";
		
		
		// have to  work on same file -> update the file
		
		

		// final Template XML To be generated
		DocumentBuilderFactory destinationFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder destinationBuilder = destinationFactory.newDocumentBuilder();
		Document templateDocument = destinationBuilder.parse(new File(templatePath)); /**empty template to data copied to Template xml for first Time **/

		
		
		/** Transformer object initialization **/
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		removeLayerTag("ALBAR_SAA", templatePath, templateDocument, transformer, null);
		templateDocument.normalize();
		
		StreamResult result = new StreamResult(new File(templatePath));
		transformer.transform(new DOMSource(templateDocument), result);
		
	}

}
