package core.framework.test;

import org.springframework.core.io.ClassPathResource;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;

/**
 * @author ebin
 */
public class XMLTest {
    public static String NAMED_NATIVE_QUERY = "named-native-query";
    public static String NAME_ATTRIBUTE = "name";

    public static void main(String[] args) throws IOException, XMLStreamException {
        ClassPathResource classPathResource = new ClassPathResource("TestDomainFinder.xml");
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        XMLEventReader reader = inputFactory.createXMLEventReader(classPathResource.getInputStream());
        XMLEvent event;
        NamedNativeQueryHolder holder = null;
        while (reader.hasNext()) {
            XMLEvent nextEvent = reader.nextEvent();
            if (nextEvent.isStartElement()) {
                StartElement startElement = nextEvent.asStartElement();
                String name = startElement.getName().getLocalPart();
                switch (name) {
                    case "named-native-query" -> {
                        holder = new NamedNativeQueryHolder();
                        holder.name = name;
                    }
                    case "query" -> {
                        if (holder != null) {
                            nextEvent = reader.nextEvent();
                            holder.queryString = nextEvent.asCharacters().getData();
                        }
                    }
                }
            }
            if (nextEvent.isEndElement()) {
                EndElement endElement = nextEvent.asEndElement();
                if (endElement.getName().getLocalPart().equals(NAMED_NATIVE_QUERY)) {
                    System.out.println(holder);
                    holder = null;
                }
            }
        }
    }

    public static class NamedNativeQueryHolder {
        public String name;
        public String queryString;

        @Override
        public String toString() {
            return "NamedNativeQueryHolder{" +
                "name='" + name + '\'' +
                ", queryString='" + queryString + '\'' +
                '}';
        }
    }
}
