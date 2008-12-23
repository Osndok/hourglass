/*
 * Hourglass - a time tracking utility.
 * Copyright (C) 2003 Michael K. Grant <mike@acm.jhu.edu>
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * --------------------------------------------------------------------
 *
 * CVS Revision $Revision: 1.9 $
 * Last modified on $Date: 2008/10/25 15:22:51 $ by $Author: ewl $
 *
 */
package net.sourceforge.hourglass.framework;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import java.util.UUID;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parses projects out of their XML format.
 *
 * @author Mike Grant
 */
public class ProjectParser extends DefaultHandler {

  /**
   * Creates a project parser that uses the given ProjectFactory.
   *
   * @param factory the factory used to create projects.
   * @throws SAXException is there is a problem creating the XMLReader
   */
  public ProjectParser(ProjectFactory factory) throws SAXException {
    this(factory, false);
  }


  /**
   * Creates a project parser
   *
   * @param factory the factory to use to create projects.
   * @param isValidating whether to validate against the schema
   * @throws SAXException is there is a problem creating the XMLReader
   */
  public ProjectParser(ProjectFactory factory, boolean isValidating) 
    throws SAXException {
    
    _factory = factory;
    _reader = XMLReaderFactory.createXMLReader
      ("org.apache.xerces.parsers.SAXParser");
    _dateFormat = Utilities.getInstance().createDateFormat();

    hookReader(_reader);
    setReaderProperties(_reader, isValidating);
  }


  private void setReaderProperties(XMLReader reader, boolean isValidating) 
    throws SAXException {
    
    reader.setFeature
      ("http://xml.org/sax/features/validation",
       isValidating);
    reader.setFeature
      ("http://apache.org/xml/features/validation/schema",
       isValidating);
    reader.setFeature
      ("http://apache.org/xml/features/validation/schema-full-checking",
       isValidating);
    reader.setProperty
      ("http://apache.org/xml/properties/schema/external-schemaLocation",
       XSD);

    if (isValidating) {
      _logger.warn("Using schema validation.");
    }
  }
  

  private void hookReader(XMLReader reader) {
    reader.setContentHandler(this);
    reader.setEntityResolver(this);
    reader.setErrorHandler(this);
  }


  /**
   * Returns a map (by id) of projects represented by the input source.
   *
   * @param inputSource the InputSource to parse.
   */
  public ProjectGroup parse(InputSource inputSource) 
    throws IOException, SAXException {

    _logger.debug("Calling parse.");
    _reader.parse(inputSource);

    return _resultGroup;
  }

  /**
   * Returns a map (by id) of projects represented by the input stream.
   *
   * @param inputStream the input stream to parse
   */
  public ProjectGroup parse(InputStream inputStream) 
    throws IOException, SAXException {

    return parse(new InputSource(inputStream));
  }
  
  
  /**
   * Resolves entities
   */
  public InputSource resolveEntity(String publicId, String systemId) 
    throws SAXException {

    if (systemId != null && systemId.indexOf("hourglass.xsd") > -1) {

      InputStream result = getClass().getClassLoader().getResourceAsStream
        ("net/sourceforge/hourglass/framework/hourglass.xsd");

      _logger.debug("Resolving entity " + systemId + " to " + result);

      return new InputSource(result);
    }
    else {
      _logger.warn("Couldn't resolve entity " + systemId);
      return null;
    }
  }


  /**
   * Handles the start of a project element.
   */
  public void handleStartProject(Attributes attrs) {
    UUID id = UUID.fromString(attrs.getValue("id"));
    String name = attrs.getValue("name");
    _logger.info("Parsing project " + id + "/" + name);
    _currentProject = _factory.createProject(_resultGroup, id);
    String parentIdStr = attrs.getValue("parent");
    UUID parentId = (parentIdStr == null) ? null : UUID.fromString(parentIdStr);
    _resultGroup.addProject(_currentProject, parentId);
    _resultGroup.setProjectName(_currentProject, name);
  }


  /**
   * Handles the start of a description element.
   */
  public void handleStartDescription(Attributes attrs) {
    _state = STATE_DESC;
    _buffer = new StringBuffer();
  }


  /**
   * Handles the end of a description element.
   */
  public void handleEndDescription() {
    _logger.debug("Setting description to " + _buffer);
    _resultGroup.setProjectDescription(_currentProject, _buffer.toString());
  }


  /**
   * Handles the start of a timespan element.
   */
  public void handleStartTimespan(Attributes attrs) throws ParseException {
    Date startDate = 
      _dateFormat.parse(attrs.getValue("start"));
    Date endDate = 
      _dateFormat.parse(attrs.getValue("end"));

    _resultGroup.addTimeSpan
      (_currentProject, new TimeSpan(startDate, endDate));
  }

  public void handleStartAttribute(Attributes attrs) {
      String domain = attrs.getValue("domain");
      String name = attrs.getValue("name");
      String value = attrs.getValue("value");
      
      _currentProject.setAttribute(domain, name, value);
  }


  /*
   * ContentHandler implementation
   * ------------------------------------------------------------------
   */
  public void startDocument() throws SAXException {
    _resultGroup = _factory.createProjectGroup();
    _state = STATE_START;
  }


  public void startElement(String namespaceURI, String localName, 
                           String qName, Attributes attrs) 
    throws SAXException {

    if ("project".equals(localName)) {
      handleStartProject(attrs);
    }
    else if ("timespan".equals(localName)) {
      try {
        handleStartTimespan(attrs);
      }
      catch (ParseException p) {
        throw new SAXException(p);
      }
    }
    else if ("description".equals(localName)) {
      handleStartDescription(attrs);
    }
    else if ("attribute".equals(localName)) {
      handleStartAttribute(attrs);
    }
  }    


  public void endElement(String namespaceURI, 
                           String localName,
                           String qName)
    throws SAXException {

    if ("description".equals(localName)) {
      handleEndDescription();
    }
  }
    


  public void characters(char[] ch, int start, int len) {
    switch (_state) {
    case STATE_DESC:
      _buffer.append(ch, start, len);
      break;
    }
  }


  /*
   * ErrorHandler implementation
   * ------------------------------------------------------------------
   */

  public void warning(SAXParseException e) throws SAXException {
    _logger.warn(e);
  }

  public void error(SAXParseException e) throws SAXException {
    _logger.error(e);
    throw e;
  }

  public void fatalError(SAXParseException e) throws SAXException {
    _logger.fatal(e);
    throw e;
  }

  private Logger _logger = Logger.getLogger(getClass());

  private DateFormat _dateFormat;
  private ProjectFactory _factory;
  private XMLReader _reader;
  private ProjectGroup _resultGroup;
  private Project _currentProject;
  private int _state;
  private StringBuffer _buffer;

  private static String NAMESPACE_STR =
    "http://hourglass.sourceforge.net/xml/hourglass";

  private static String XSD = NAMESPACE_STR + " hourglass.xsd";

  private static final int STATE_START = 1;
  private static final int STATE_DESC = 2;

  public static final boolean VALIDATION_ON = true;
  public static final boolean VALIDATION_OFF = false;
}
