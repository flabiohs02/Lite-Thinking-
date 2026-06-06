export interface PreparedEmailResponse {
  delivered: false;
  message: string;
}

export async function prepareInventoryPdfEmail(email: string, pdfBlob: Blob): Promise<PreparedEmailResponse> {
  await Promise.resolve(pdfBlob);
  return {
    delivered: false,
    message: `PDF preparado para envío a ${email}. Falta configurar proveedor de correo/API REST.`
  };
}
