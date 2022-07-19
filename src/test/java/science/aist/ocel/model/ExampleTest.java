/*
 * Copyright (c) 2022 the original author or authors.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package science.aist.ocel.model;

import org.testng.Assert;
import org.testng.annotations.Test;
import science.aist.ocel.model.impl.LogRepository;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.GregorianCalendar;

public class ExampleTest {
    private final ObjectFactory factory = new ObjectFactory();

    @Test
    public void testSaveOCEL() throws DatatypeConfigurationException, FileNotFoundException {
        // given
        LogType logType = factory.createLogType();
        GlobalsType globalsType = factory.createGlobalsType();
        globalsType.getStringOrDateOrInt().add(createString("version", "1.0"));
        logType.getGlobal().add(globalsType);

        EventsType eventsType = factory.createEventsType();
        EventType eventType = factory.createEventType();
        eventType.getStringOrDateOrInt().add(createString("id", "1.0"));
        eventType.getStringOrDateOrInt().add(createString("activity", "place order"));
        eventType.getStringOrDateOrInt().add(createDate("timestamp"));
        AttributeListType omap = factory.createAttributeListType();
        omap.setKey("omap");
        omap.getStringOrDateOrInt().add(createString("object-id", "Fire Stick 4k"));
        eventType.getStringOrDateOrInt().add(omap);

        AttributeListType vmap = factory.createAttributeListType();
        vmap.setKey("vmap");
        vmap.getStringOrDateOrInt().add(createString("price", "524.96"));
        eventType.getStringOrDateOrInt().add(vmap);

        eventsType.getEvent().add(eventType);
        logType.getEvents().add(eventsType);

        ObjectsType objectsType = factory.createObjectsType();

        ObjectType objectType = factory.createObjectType();
        objectType.getStringOrDateOrInt().add(createString("id", "Fire Stick 4k"));
        objectType.getStringOrDateOrInt().add(createString("type", "products"));

        AttributeListType listType = factory.createAttributeListType();
        listType.setKey("ovmap");
        listType.getStringOrDateOrInt().add(createString("producer", "B"));
        objectType.getStringOrDateOrInt().add(listType);

        objectsType.getObject().add(objectType);
        logType.getObjects().add(objectsType);

        JAXBElement<LogType> root = factory.createLog(logType);

        XMLRepository<LogType> repo = new LogRepository();

        // when
        repo.save(root, new FileOutputStream("target/test.xmlocel"));

        // then
        Files.exists(Path.of("target", "test.xmlocel"));
    }

    @Test
    public void testLoadOCEL() {
        // given
        XMLRepository<LogType> repo = new LogRepository();

        // when
        JAXBElement<LogType> load = repo.load(getClass().getResourceAsStream("/running-example.xmlocel"));

        // then
        LogType value = load.getValue();
        Assert.assertNotNull(value);
        Assert.assertEquals(value.getEvents().size(), 1);
        Assert.assertEquals(value.getEvents().get(0).getEvent().size(), 22367);
        Assert.assertEquals(value.getGlobal().size(), 3);
        Assert.assertEquals(value.getObjects().size(), 1);
        Assert.assertEquals(value.getObjects().get(0).getObject().size(), 11522);
    }

    private AttributeStringType createString(String key, String value) {
        AttributeStringType string = factory.createAttributeStringType();
        string.setKey(key);
        string.setValue(value);
        return string;
    }

    private AttributeDateType createDate(String key) throws DatatypeConfigurationException {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        AttributeDateType date = factory.createAttributeDateType();
        date.setKey(key);
        date.setValue(xmlGregorianCalendar);
        return date;
    }
}
