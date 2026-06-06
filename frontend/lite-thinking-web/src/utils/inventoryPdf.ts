import type { InventoryResponse, ResourceResponse } from '../types';
import { activeValue, normalizeActive } from './forms';

export interface InventoryPdfOptions {
  companyName?: string;
  monthName?: string;
  year?: string;
}

export async function buildInventoryPdf(rows: ResourceResponse[], options: InventoryPdfOptions = {}) {
  const [{ jsPDF }, { default: autoTable }] = await Promise.all([import('jspdf'), import('jspdf-autotable')]);
  const doc = new jsPDF();
  const generatedAt = new Intl.DateTimeFormat('es-CO', {
    dateStyle: 'medium',
    timeStyle: 'short'
  }).format(new Date());

  doc.setFontSize(16);
  doc.text('Reporte de Inventario', 14, 18);
  doc.setFontSize(10);
  doc.text(`Generado: ${generatedAt}`, 14, 26);
  doc.text(`Empresa: ${options.companyName ?? 'Todas'}`, 14, 32);
  doc.text(`Mes: ${options.monthName ?? 'Todos'}`, 78, 32);
  doc.text(`Ano: ${options.year ?? 'Todos'}`, 128, 32);

  const body = rows.map((row) => {
    const inventory = normalizeActive(row) as InventoryResponse;
    return [
      inventory.product?.name ?? '-',
      inventory.company?.name ?? '-',
      String(inventory.stock ?? 0),
      activeValue(inventory) ? 'Activo' : 'Inactivo'
    ];
  });

  autoTable(doc, {
    startY: 40,
    head: [['Producto', 'Empresa', 'Stock', 'Estado']],
    body,
    styles: {
      fontSize: 9,
      cellPadding: 3
    },
    headStyles: {
      fillColor: [15, 107, 103]
    },
    didDrawPage: () => {
      const pageCount = doc.getNumberOfPages();
      doc.setFontSize(9);
      doc.text(`Pagina ${doc.getCurrentPageInfo().pageNumber} de ${pageCount}`, 14, 288);
    }
  });

  return doc;
}

export function downloadInventoryPdf(rows: ResourceResponse[]) {
  return buildInventoryPdf(rows).then((doc) => {
    doc.save(`inventario-${new Date().toISOString().slice(0, 10)}.pdf`);
  });
}

export function downloadFilteredInventoryPdf(rows: ResourceResponse[], options: InventoryPdfOptions, fileName: string) {
  return buildInventoryPdf(rows, options).then((doc) => {
    doc.save(fileName);
  });
}

export async function inventoryPdfBlob(rows: ResourceResponse[], options: InventoryPdfOptions = {}): Promise<Blob> {
  return (await buildInventoryPdf(rows, options)).output('blob');
}
