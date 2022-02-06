package sqlAPI;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class XmlParser {

    String filePath;
    File file;
    String tagName;
    String[] attributeNames;
    Document doc;

    public XmlParser(String filePath,String tagName,String[] attributeNames) {
        this.filePath = filePath;
        this.tagName = tagName;
        this.attributeNames = attributeNames;

        try {
            file = new File(this.filePath);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(file);
            doc.getDocumentElement().normalize();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    public ArrayList<HashMap<String, String>> getElementByAttributeValue(String attributeName, String attributeValue) {
        NodeList nodeList = doc.getElementsByTagName(tagName);
        ArrayList<HashMap<String,String>> elementList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<nodeList.getLength();i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType()==Node.ELEMENT_NODE) {
                Element tElement = (Element)node;

                String nodeAtrributeValue = tElement.getAttribute(attributeName);
                if(nodeAtrributeValue.equals(attributeValue)) {
                    HashMap<String, String> hm = new HashMap<String, String>();
                    for(int j=0;j<attributeNames.length;j++) {
                        String index = attributeNames[j];
                        hm.put(index,tElement.getAttribute(index).trim());
                    }
                    hm.put("textContent",tElement.getTextContent().trim());

                    elementList.add(hm);
                }

            }

        }


        return elementList;
    }

    public ArrayList<HashMap<String,String>> getAllElements(){
        NodeList nodeList = doc.getElementsByTagName(tagName);
        ArrayList<HashMap<String,String>> elementList = new ArrayList<HashMap<String,String>>();

        for(int i=0;i<nodeList.getLength();i++) {
            Node node = nodeList.item(i);
            if(node.getNodeType()==Node.ELEMENT_NODE) {
                Element tElement = (Element)node;

                HashMap<String, String> hm = new HashMap<String, String>();
                for(int j=0;j<attributeNames.length;j++) {
                    String index = attributeNames[j];
                    hm.put(index,tElement.getAttribute(index).trim());
                }
                hm.put("textContent",tElement.getTextContent().trim());

                elementList.add(hm);

            }

        }


        return elementList;

    }


}
