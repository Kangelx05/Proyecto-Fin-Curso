using Domain.Entities;
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
    public partial class DropDown : UserControl
    {
        public DropDown()
        {
            InitializeComponent();


            combo.Format += (s, e) =>
            {
                if (e.ListItem is User u)
                    e.Value = $"{u.Name} {u.Surnames}";
            };
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

        /// <summary>Lista que alimenta el ComboBox.</summary>
        [Category("Datos")]
        [Description("Lista de objetos que aparecerán en el desplegable.")]
        [AttributeProvider(typeof(IListSource))]            // permite elegir listas en el PropertyGrid
        [DesignerSerializationVisibility(DesignerSerializationVisibility.Hidden)]
        public object DataSource
        {
            get => combo.DataSource;
            set => combo.DataSource = value;
        }

        /// <summary>Propiedad del objeto que se mostrará al usuario.</summary>
        [Category("Datos")]
        [Description("Campo visible de cada elemento.")]
        [DefaultValue("")]
        public string DisplayMember
        {
            get => combo.DisplayMember;
            set => combo.DisplayMember = value;
        }

        /// <summary>Propiedad que actúa como valor real (clave).</summary>
        [Category("Datos")]
        [Description("Campo usado internamente como valor del elemento.")]
        [DefaultValue("Id")]
        public string ValueMember
        {
            get => combo.ValueMember;
            set => combo.ValueMember = value;
        }

        /// <summary>Valor (Id, clave, etc.) del elemento seleccionado.</summary>
        [Browsable(false)]   // no necesitas verlo en el diseñador
        public object SelectedValue
        {
            get => combo.SelectedValue;
            set => combo.SelectedValue = value;
        }

        /// <summary>Valor (Id, clave, etc.) del elemento seleccionado.</summary>
        [Browsable(false)]   // no necesitas verlo en el diseñador
        public object SelectedItem
        {
            get => combo.SelectedItem;
            set => combo.SelectedItem = value;
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
