using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Presentation.Controllers
{
    public partial class ComboBox : UserControl
    {
        public ComboBox()
        {
            InitializeComponent();
        }

        // --- Propiedades expuestas al diseñador ---

        [Category("Apariencia")]
        [Description("Texto que aparece en la etiqueta asociada.")]
        public string LabelText
        {
            get => comboLabel.Text;
            set => comboLabel.Text = value;
        }

        [Category("Datos")]
        [Description("Texto introducido por el usuario.")]
        public override string Text
        {
            get => combo.Text;
            set => combo.Text = value;
        }

        // Reexpone eventos si los necesitas
        public new event EventHandler TextChanged
        {
            add => combo.TextChanged += value;
            remove => combo.TextChanged -= value;
        }

        private void label1_Click(object sender, EventArgs e)
        {

        }

        private void combo_TextChanged(object sender, EventArgs e)
        {

        }
    }
}
