package zss.vip.myspringmvc.xmlparser;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import java.io.InputStream;

public class XMLConfigurationParser {
    public static String ReadXMLBasePackage (String configPath) throws DocumentException {
        SAXReader reader = new SAXReader();
        InputStream inputStream = XMLConfigurationParser.class.getClassLoader().getResourceAsStream(configPath);

        Document document = reader.read(inputStream);
        Element rootElement = document.getRootElement();
        Element element = rootElement.element("component-scan");
        String basePageString = element.attributeValue("base-package");

        return basePageString;
    }

    public static void main(String[] args) throws DocumentException {
        System.out.println(XMLConfigurationParser.ReadXMLBasePackage("applicationContext.xml"));
    }

}
