<?xml version="1.0" encoding="UTF-8"?>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <fo:layout-master-set>
    <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21cm" margin="2cm">
      <fo:region-body margin-top="3cm" margin-bottom="2cm"/>
      <fo:region-before extent="3cm"/>
      <fo:region-after extent="2cm"/>
    </fo:simple-page-master>
  </fo:layout-master-set>

  <fo:page-sequence master-reference="A4">
    <fo:static-content flow-name="xsl-region-before">
      <fo:block text-align="center" font-size="24pt" font-weight="bold" color="#2c3e50" margin-bottom="10pt">
        Kombuis Verslag
      </fo:block>
      <fo:block text-align="center" font-size="10pt" color="#7f8c8d" margin-bottom="5pt">
        Geskep: ${generatedAt?string("yyyy-MM-dd HH:mm:ss")}
      </fo:block>
      <fo:block text-align="center" font-size="10pt" color="#7f8c8d" margin-bottom="5pt">
        Verslag Datum: ${date?string("yyyy-MM-dd HH:mm:ss")}
      </fo:block>
    </fo:static-content>

    <fo:static-content flow-name="xsl-region-after">
      <fo:block text-align="center" font-size="10pt" color="#7f8c8d">
        Bladsy <fo:page-number/> van <fo:page-number-citation ref-id="last-page"/>
      </fo:block>
    </fo:static-content>

    <fo:flow flow-name="xsl-region-body">
      <!-- Detailed Items Section -->
      <fo:block font-size="16pt" font-weight="bold" color="#34495e" margin-bottom="15pt">
        Items
      </fo:block>
      <fo:table table-layout="fixed" width="100%" border="1pt solid #bdc3c7">
        <fo:table-column column-width="10%"/>
        <fo:table-column column-width="90%"/>
        <fo:table-header background-color="#c0c0c0">
          <fo:table-row>
            <fo:table-cell padding="3pt" border="1pt solid #bdc3c7">
              <fo:block font-weight="bold" text-align="center">Aantal</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="3pt" border="1pt solid #bdc3c7">
              <fo:block font-weight="bold">Item Naam</fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-header>
        <fo:table-body>
          <#list items as item>
          <fo:table-row>
            <fo:table-cell padding="2pt" border="1pt solid #bdc3c7">
              <fo:block text-align="center">${item.quantity}</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="2pt" border="1pt solid #bdc3c7">
              <fo:block>${item.name}</fo:block>
            </fo:table-cell>
          </fo:table-row>
          </#list>
        </fo:table-body>
      </fo:table>

      <fo:block id="last-page"/>
    </fo:flow>
  </fo:page-sequence>
</fo:root>
