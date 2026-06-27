import jsPDF from 'jspdf'
import autoTable from 'jspdf-autotable'

/**
 * Generate and download a CSV file from table data
 */
export function generateCSVReport(
  columns: string[],
  rows: any[][],
  filename: string = 'report'
) {
  // Create CSV header
  const csvHeaders = columns.join(',')
  
  // Create CSV rows - escape values that contain commas, quotes, or newlines
  const csvRows = rows.map(row => 
    row.map(cell => {
      if (cell === null || cell === undefined) return ''
      
      const stringValue = String(cell)
      
      // If the value contains comma, quote, or newline, wrap it in quotes and escape quotes
      if (stringValue.includes(',') || stringValue.includes('"') || stringValue.includes('\n')) {
        return `"${stringValue.replace(/"/g, '""')}"`
      }
      
      return stringValue
    }).join(',')
  ).join('\n')
  
  // Combine headers and rows
  const csvContent = `${csvHeaders}\n${csvRows}`
  
  // Create blob and download
  const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
  const link = document.createElement('a')
  const url = URL.createObjectURL(blob)
  
  link.setAttribute('href', url)
  link.setAttribute('download', `${filename}_${new Date().toISOString().split('T')[0]}.csv`)
  link.style.visibility = 'hidden'
  
  document.body.appendChild(link)
  link.click()
  document.body.removeChild(link)
  
  URL.revokeObjectURL(url)
}

/**
 * Generate and download a PDF file from table data
 */
export function generatePDFReport(
  columns: string[],
  rows: any[][],
  filename: string = 'report',
  title?: string
) {
  // Create new PDF document
  const doc = new jsPDF({
    orientation: columns.length > 5 ? 'landscape' : 'portrait',
    unit: 'mm',
    format: 'a4'
  })
  
  // Add title if provided
  if (title) {
    doc.setFontSize(16)
    doc.text(title, 14, 15)
  }
  
  // Format data for jsPDF autoTable
  const tableData = rows.map(row => 
    row.map(cell => {
      if (cell === null || cell === undefined) return ''
      return String(cell)
    })
  )
  
  // Generate table
  autoTable(doc, {
    head: [columns],
    body: tableData,
    startY: title ? 25 : 15,
    styles: {
      fontSize: 8,
      cellPadding: 2,
      overflow: 'linebreak',
    },
    headStyles: {
      fillColor: [41, 128, 185],
      textColor: 255,
      fontStyle: 'bold',
    },
    alternateRowStyles: {
      fillColor: [245, 245, 245],
    },
    margin: { top: 10, right: 10, bottom: 10, left: 10 },
    didDrawPage: (data: any) => {
      // Add page number at the bottom
      const pageCount = doc.getNumberOfPages()
      doc.setFontSize(8)
      doc.text(
        `Page ${data.pageNumber} of ${pageCount}`,
        doc.internal.pageSize.width / 2,
        doc.internal.pageSize.height - 5,
        { align: 'center' }
      )
      
      // Add generation date at the bottom
      const today = new Date().toLocaleDateString('en-ZA', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      })
      doc.text(
        `Generated: ${today}`,
        doc.internal.pageSize.width - 10,
        doc.internal.pageSize.height - 5,
        { align: 'right' }
      )
    }
  })
  
  // Save the PDF
  doc.save(`${filename}_${new Date().toISOString().split('T')[0]}.pdf`)
}

/**
 * Format table data for export (clean up complex objects)
 */
export function prepareDataForExport(data: any[][]): any[][] {
  return data.map(row =>
    row.map(cell => {
      // Handle complex objects
      if (cell && typeof cell === 'object') {
        // Handle dates
        if (cell instanceof Date) {
          return `${cell.toLocaleDateString('en-ZA')} ${cell.toLocaleTimeString('en-ZA')}`
        }
        // Handle arrays
        if (Array.isArray(cell)) {
          return cell.join(', ')
        }
        // Handle React elements or other objects - convert to string
        return String(cell)
      }
      
      return cell
    })
  )
}
