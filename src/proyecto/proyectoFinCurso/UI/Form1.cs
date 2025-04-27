using Logic;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Drawing.Printing;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Xml.Linq;

namespace UI
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void button1_Click(object sender, EventArgs e)
        {
            PdfGeneratorService generator = new PdfGeneratorService();

            // Crear una lista de ejemplo con elementos del menú.
            List<MenuItemm> items = new List<MenuItemm>
    {
        new MenuItemm { Nombre = "Taco", Descripcion = "Delicioso taco de carne y queso", Precio = 5.99m },
        new MenuItemm { Nombre = "Burrito", Descripcion = "Burrito grande con frijoles y salsa", Precio = 7.50m }
        // Puedes agregar más elementos según necesites.
    };

            // Definir la ruta donde se guardará el PDF, por ejemplo, en el escritorio.
            string rutaDestino = "C:\\Users\\angel\\Desktop\\testPDF\\test.pdf";

            // Llamar al método para generar el PDF.
            generator.GenerarPDF(rutaDestino, items);

            // Mostrar un mensaje informativo.
            MessageBox.Show("PDF generado en: " + rutaDestino);
        }
    }
}
