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
    public partial class TableUiEntity : UserControl
    {

        public event EventHandler YellowAlert;
        public event EventHandler RedAlert;
        public event EventHandler Table;


        public Table table {get;set;}
        public TableUiEntity()
        {
            InitializeComponent();
        }

        private void YellowAlert_Click(object sender, EventArgs e)
             => YellowAlert?.Invoke(this, e);

        private void RedAlert_Click(object sender, EventArgs e)
             => RedAlert?.Invoke(this, e);

        private void Table_Click(object sender, EventArgs e)
             => Table?.Invoke(this, e);

        private void tableLayoutPanel1_Paint(object sender, PaintEventArgs e)
        {

        }

        private void tableLayoutPanel2_Paint(object sender, PaintEventArgs e)
        {

        }


        private void tableLayoutPanel3_Paint(object sender, PaintEventArgs e)
        {

        }
    }
}
