<?xml version="1.0" encoding="ISO-8859-1"?>

<!--
   - The XSL for transforming a report XML to HTML.
   - 
   - This needs more work.
   -
   - Neil Thier
   -->

<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:template match="report">

<html>

  <body>
    <table border="1">
      <tr>
        <th colspan="2"><xsl:value-of select="@name" /></th>
      </tr>
      <xsl:for-each select="data/grouping">        
        <tr>
          <xsl:for-each select="spanning">
            <td>
              <xsl:value-of select="." />
            </td>
          </xsl:for-each>
          <td align="right"><table border="1" width="100%">
          <xsl:for-each select="row">
            <tr>
            <xsl:for-each select="col">
              <td align="right"><xsl:value-of select="." /></td>
            </xsl:for-each>
            </tr>
          </xsl:for-each>
          </table></td>
        </tr>
      </xsl:for-each>
    </table>
  </body>

</html>

</xsl:template>
</xsl:stylesheet>
