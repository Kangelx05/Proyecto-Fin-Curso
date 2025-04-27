using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using iText.Kernel.Pdf;          // Para PdfWriter, PdfDocument
using iText.Layout;              // Para Document
using iText.Layout.Element;      // Para Paragraph, Table, etc.
using iText.Layout.Properties;   // Para TextAlignment, Margin, etc.
using iText.IO.Image;

namespace Logic
{
    public class PdfGeneratorService
    {
        public void GenerarPDF(string rutaDestino, List<MenuItemm> menuItems)
        {
            // 1. Crear un PdfWriter para la ruta de destino
            PdfWriter writer = new PdfWriter(rutaDestino);

            // 2. Crear el PdfDocument a partir del writer
            PdfDocument pdf = new PdfDocument(writer);
            // 3. Crear objeto Document (representa el "lienzo" donde dibujas)
            Document document = new Document(pdf);

            // -- EJEMPLO: Agregar un título --
            Paragraph titulo = new Paragraph("Nuestra Carta")
                .SetTextAlignment(TextAlignment.CENTER)
                .SetFontSize(20);
            document.Add(titulo);

            // Espacio en blanco
            document.Add(new Paragraph(" "));

            // -- Iterar sobre tus elementos del menú y agregarlos como párrafos --
            foreach (var item in menuItems)
            {
                // Nombre
                Paragraph nombre = new Paragraph(item.Nombre)
                    .SetFontSize(14);
                document.Add(nombre);

                // Descripción
                Paragraph descripcion = new Paragraph(item.Descripcion)
                    .SetFontSize(12)
                    .SetMarginLeft(20); // sangría
                document.Add(descripcion);

                // Precio alineado a la derecha
                Paragraph precio = new Paragraph("Precio: " + item.Precio.ToString("C2"))
                    .SetTextAlignment(TextAlignment.RIGHT)
                    .SetFontSize(12);
                document.Add(precio);

                // Separación
                document.Add(new Paragraph(" "));
            }

            // 4. Cerrar el documento
            document.Close();
        }
    }
}
