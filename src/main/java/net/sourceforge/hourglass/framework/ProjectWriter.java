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
 * CVS Revision $Revision: 1.8 $
 * Last modified on $Date: 2004/08/12 21:31:15 $ by $Author: mgrant79 $
 *
 */
package net.sourceforge.hourglass.framework;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import net.sourceforge.hourglass.swingui.ExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jdom.output.XMLOutputter;


/**
 * Writes a project to an output stream.
 *
 * @author Mike Grant
 */
public class ProjectWriter {

  /**
   * Creates a ProjectWriter on the given output stream.
   */
  public ProjectWriter(OutputStream os) {
    _writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8), NO_AUTOFLUSH);
    _dateFormat = Utilities.getInstance().createDateFormat();
    _xmlOutputter = new XMLOutputter();
    try {
      initializeProperties();
    }
    catch (ParseException e) {
      log.error("Parse exception initializing properties.", e);
      ExceptionHandler.showUser(e);
    }
    catch (IOException e) {
      log.error("IOException initializing properties.", e);
      ExceptionHandler.showUser(e);
    }
    writeHeader(_writer);
  }


  private void initializeProperties() throws IOException, ParseException {
    Properties p = new Properties();
    p.load(getClass().getClassLoader().getResourceAsStream
                          ("net/sourceforge/hourglass/build.properties"));

    DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
    _buildDate = df.parse((String) p.get("hourglass.build.time"));
    _version = (String) p.get("hourglass.version");
  }


  /**
   * Writes the XML header to the given writer.
   */
  private void writeHeader(PrintWriter writer) {
    writer.println
      ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    writer.print
      ("<hourglass xmlns=\"http://hourglass.sourceforge.net/xml/hourglass\"");
    writer.print(" version=\"");
    writer.print(_version);
    writer.print("\" buildDate=\"");
    writer.print(_dateFormat.format(_buildDate));
    writer.println("\">"); 
  }


  /**
   * Writes the XML footer to the given writer.
   */
  private void writeFooter(PrintWriter writer) {
    writer.println("</hourglass>");
  }


  /**
   * Write the XML footer, and closes the underlying output stream.
   */
  public void close() throws IOException {
    writeFooter(_writer);
    _writer.close();
  }



  /**
   * Writes the project group in parent-first order.
   */
  public void write(ProjectGroup group) {
    Iterator i = group.getRootProject().getChildren().iterator();
    while (i.hasNext()) {
      Project eachTopLevelProject = (Project) i.next();
      writeRecursive(eachTopLevelProject, group);
    }
  }


  /**
   * Recursively writes the given project, parent-first, using the
   * group to examine the hierarchy.
   */
  private void writeRecursive(Project p, ProjectGroup g) {
    write(p, g);
    Iterator i = p.getChildren().iterator();
    while (i.hasNext()) {
      Project eachChild = (Project) i.next();
      writeRecursive(eachChild, g);
    }
  }



  /**
   * Writes the given project to the underlying output stream.
   *
   * @param p the project to write
   * @param g the group to which the project belongs (used for
   *            hierarchy exploration--not written to stream).
   */
  private void write(Project p, ProjectGroup g) {
    _writer.print("<project name=\"" + 
                  _xmlOutputter.escapeAttributeEntities(p.getName()) + 
                  "\" id=\"" + p.getId() + "\" ");
    if (g.getParent(p) != null && 
        !g.getParent(p).equals(g.getRootProject())) {
      _writer.print("parent=\"" + g.getParent(p).getId() + "\" ");
    }
    _writer.println(">");

    _writer.print("<description>");
    getLogger().debug("Description: " + p.getDescription());
    _writer.print(_xmlOutputter.escapeElementEntities(p.getDescription()));
    _writer.println("</description>");

    for (final TimeSpan o : p.getTimeSpans())
    {
      writeTimeSpan(_writer, o);
    }

    writeAttributes(_writer, p);

    _writer.println("</project>");
  }


  /**
   * Writes the given timespan to the writer.
   */
  private void writeTimeSpan(PrintWriter writer, TimeSpan span) {
    writer.print("<timespan start=\"");
    writer.print(_dateFormat.format(span.getStartDate()));
    writer.print("\" end=\"");
    writer.print(_dateFormat.format(span.getEndDate()));
    writer.println("\" />");
  }
  
  private void writeAttributes(PrintWriter writer, Project p) {
      Iterator i = p.getAttributeDomains();
      while (i.hasNext()) {
          String eachDomain = (String) i.next();
          Iterator j = p.getAttributeKeys(eachDomain);
          while (j.hasNext()) {
              String eachName = (String) j.next();
              String eachValue = p.getAttribute(eachDomain, eachName);
              writeAttribute(writer, eachDomain, eachName, eachValue);
          }
      }
  }
  
  private void writeAttribute(PrintWriter writer, String domain, String name, String value) {
      writer.print("<attribute domain=\"");
      writer.print(_xmlOutputter.escapeAttributeEntities(domain));
      writer.print("\" name=\"");
      writer.print(_xmlOutputter.escapeAttributeEntities(name));
      writer.print("\" value=\"");
      writer.print(_xmlOutputter.escapeAttributeEntities(value));
      writer.println("\" />");
  }

  @Deprecated
  private
  Logger getLogger()
  {
    return log;
  }


  private final Logger log = LogManager.getLogger(getClass());
  private PrintWriter _writer;
  private DateFormat _dateFormat;
  private XMLOutputter _xmlOutputter;
  private static boolean NO_AUTOFLUSH = false;

  private Date _buildDate;
  private String _version;
}
