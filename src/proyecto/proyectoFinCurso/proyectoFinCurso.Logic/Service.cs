using iTextSharp.text.pdf;
using iTextSharp.text;
using Logic;
using proyectoFinCurso.Data;

namespace proyectoFinCurso.Logic
{
    public class Service
    {
        public void GenerarPDF(string rutaDestino, List<MenuItem> menuItems)
        {

            Class1 test = new Class1();
            // 1. Crear el documento y establecer tamaño de página, márgenes, etc.
            Document document = new Document(PageSize.A4, 50, 50, 50, 50);

            // 2. Crear un writer para el documento
            PdfWriter writer = PdfWriter.GetInstance(document, new FileStream(rutaDestino, FileMode.Create));

            // 3. Abrir el documento
            document.Open();

            // 4. Agregar elementos al PDF:
            //    - Título
            //    - Secciones
            //    - Elementos (texto, imágenes, tablas, etc.)

            // Ejemplo: Título estilizado
            Font fontTitulo = FontFactory.GetFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph titulo = new Paragraph("Nuestra Carta", fontTitulo);
            titulo.Alignment = Element.ALIGN_CENTER;
            document.Add(titulo);

            // Espacio en blanco
            document.Add(new Paragraph(" "));

            // Ejemplo: Iterar sobre los ítems del menú
            Font fontItemNombre = FontFactory.GetFont(FontFactory.HELVETICA_BOLD, 14);
            Font fontItemDescripcion = FontFactory.GetFont(FontFactory.HELVETICA, 12);
            Font fontItemPrecio = FontFactory.GetFont(FontFactory.HELVETICA_BOLD, 12);

            foreach (var item in menuItems)
            {
                // Nombre
                Paragraph nombre = new Paragraph(item.Nombre, fontItemNombre);
                document.Add(nombre);

                // Descripción
                Paragraph descripcion = new Paragraph(item.Descripcion, fontItemDescripcion);
                descripcion.IndentationLeft = 20; // Sangría para estilo
                document.Add(descripcion);

                // Precio
                Paragraph precio = new Paragraph($"Precio: {item.Precio}", fontItemPrecio);
                precio.Alignment = Element.ALIGN_RIGHT;
                document.Add(precio);

                // Espacio entre cada producto
                document.Add(new Paragraph(" "));
            }

            // 5. Cerrar el documento
            document.Close();
            writer.Close();
        }
    }
}
