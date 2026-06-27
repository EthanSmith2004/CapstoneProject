<?xml version="1.0" encoding="UTF-8"?>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">
  <fo:layout-master-set>
    <fo:simple-page-master master-name="A4" page-height="29.7cm" page-width="21cm" margin="1.5cm">
      <fo:region-body margin-top="3cm" margin-bottom="2cm"/>
      <fo:region-before extent="3cm"/>
      <fo:region-after extent="2cm"/>
    </fo:simple-page-master>
  </fo:layout-master-set>

  <fo:page-sequence master-reference="A4">
    <fo:static-content flow-name="xsl-region-before">
      <fo:block text-align="center" font-size="24pt" font-weight="bold" color="#2c3e50" margin-bottom="10pt">
        Delivery Report
      </fo:block>
      <fo:block text-align="center" font-size="12pt" color="#7f8c8d">
        Report Date: ${date?string("yyyy-MM-dd HH:mm")}
      </fo:block>
      <fo:block text-align="center" font-size="10pt" color="#7f8c8d" margin-top="5pt">
        Created: ${generatedAt?string("yyyy-MM-dd HH:mm:ss")}
      </fo:block>
    </fo:static-content>

    <fo:static-content flow-name="xsl-region-after">
      <fo:block text-align="center" font-size="10pt" color="#7f8c8d">
        Page <fo:page-number/> of <fo:page-number-citation ref-id="last-page"/>
      </fo:block>
    </fo:static-content>

    <fo:flow flow-name="xsl-region-body">
      <!-- Delivery Details Section -->
      <fo:block font-size="16pt" font-weight="bold" color="#34495e" margin-bottom="15pt" margin-top="5pt">
        Delivery
      </fo:block>

      <fo:table table-layout="fixed" width="100%" border="1pt solid #bdc3c7" font-size="9pt">
        <fo:table-column column-width="12%"/>
        <fo:table-column column-width="20%"/>
        <fo:table-column column-width="8%"/>
        <fo:table-column column-width="15%"/>
        <fo:table-column column-width="15%"/>
        <fo:table-column column-width="15%"/>
        <fo:table-column column-width="15%"/>
        <fo:table-header background-color="#c0c0c0c0">
          <fo:table-row>
            <fo:table-cell padding="8pt" border="1pt solid #bdc3c7">
              <fo:block font-weight="bold">Delivery</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="8pt" border="1pt solid #bdc3c7">
              <fo:block font-weight="bold">Item</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="8pt" border="1pt solid #bdc3c7">
              <fo:block font-weight="bold" text-align="center">Qty</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="8pt" border="1pt solid #bdc3c7">
              <fo:block font-weight="bold">Customer</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="8pt" border="1pt solid #bdc3c7">
              <fo:block font-weight="bold">Student/Staff No.</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="8pt" border="1pt solid #bdc3c7">
              <fo:block font-weight="bold">Campus</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="8pt" border="1pt solid #bdc3c7">
              <fo:block font-weight="bold">Residence</fo:block>
            </fo:table-cell>
          </fo:table-row>
        </fo:table-header>
        <fo:table-body>
          <#list deliveries as delivery>
          <fo:table-row>
            <fo:table-cell padding="6pt" border="1pt solid #bdc3c7">
              <fo:block>${delivery.deliveryDate?string("yyyy-MM-dd HH:mm")}</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="6pt" border="1pt solid #bdc3c7">
              <fo:block>${delivery.itemName}</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="6pt" border="1pt solid #bdc3c7">
              <fo:block text-align="center">${delivery.quantity}</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="6pt" border="1pt solid #bdc3c7">
              <fo:block>${delivery.firstName} ${delivery.lastName}</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="6pt" border="1pt solid #bdc3c7">
              <fo:block>${delivery.credentialNumber}</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="6pt" border="1pt solid #bdc3c7">
              <fo:block>${delivery.campusName}</fo:block>
            </fo:table-cell>
            <fo:table-cell padding="6pt" border="1pt solid #bdc3c7">
              <fo:block>${delivery.residenceName}</fo:block>
            </fo:table-cell>
          </fo:table-row>
          </#list>
        </fo:table-body>
      </fo:table>

      <fo:block id="last-page"/>
    </fo:flow>
  </fo:page-sequence>
</fo:root>
