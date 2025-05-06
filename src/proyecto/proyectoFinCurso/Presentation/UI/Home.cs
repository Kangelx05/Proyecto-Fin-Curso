using Presentation.Controllers;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;

namespace Presentation.UI
{
    public partial class Home : Form
    {

        private bool _editMode = false;    // Estado actual del lienzo
        private Control _dragCtrl;        // Control que se está arrastrando
        private Control _dragUC;   // ← ahora guardamos el UserControl, no el botón
        private Point _offset;             // Distancia ratón‑origen dentro del control

        public Home()
        {
            InitializeComponent();
            Initialize_Canvas();

        }

        private void Initialize_Canvas()
        {
            canvasPanel.BorderStyle = BorderStyle.FixedSingle;
            canvasPanel.AutoScroll = true;

            add.Click += AddBtn_Click;
            edit.Click += EditBtn_Click;
        }

        private void AddBtn_Click(object sender, EventArgs e)
        {
            var entidad = new TableUiEntity();

            // Enganchamos los eventos de arrastre (sirven tanto si ya estamos en modo edición
            // como si entramos después)
            HookDragEvents(entidad);

            canvasPanel.Controls.Add(entidad);
            entidad.BringToFront();               // por si hay solapamientos
        }

        private void EditBtn_Click(object sender, EventArgs e)
        {
            _editMode = !_editMode;
            edit.Text = _editMode ? "Salir edición" : "Editar lienzo";

            // Cambiamos el cursor para que el usuario sepa que puede arrastrar
            canvasPanel.Cursor = _editMode ? Cursors.SizeAll : Cursors.Default;
        }

        private void Drag_MouseDown(object sender, MouseEventArgs e)
        {
            if (!_editMode || e.Button != MouseButtons.Left) return;

            // 1) Hallar el UserControl raíz sobre el que se hizo clic
            Control c = sender as Control;
            while (c != null && c is not TableUiEntity) c = c.Parent;
            _dragUC = c;                       // podría ser null si pinchamos fuera

            if (_dragUC != null)
            {
                // 2) Calcular la distancia entre cursor y esquina superior‑izq. del UC
                var pAbs = canvasPanel.PointToClient(Control.MousePosition);
                _offset = new Point(pAbs.X - _dragUC.Left,
                                          pAbs.Y - _dragUC.Top);
                _dragUC.Capture = true;
            }
        }

        private void Drag_MouseMove(object? sender, MouseEventArgs e)
        {
            if (!_editMode || _dragUC == null) return;

            var pAbs = canvasPanel.PointToClient(Control.MousePosition);
            _dragUC.Location = new Point(pAbs.X - _offset.X,
                                         pAbs.Y - _offset.Y);
        }

        private void Drag_MouseUp(object sender, MouseEventArgs e)
        {
            if (!_editMode || e.Button != MouseButtons.Left) return;
            _dragUC.Capture = false;
            _dragUC = null;
        }

        private void HookDragEvents(Control root)
        {
            root.MouseDown += Drag_MouseDown;
            root.MouseMove += Drag_MouseMove;
            root.MouseUp += Drag_MouseUp;

            foreach (Control hijo in root.Controls)
                HookDragEvents(hijo);   // recursivo
        }

        private void Menu_Click_1(object sender, EventArgs e)
        {
            if (panelSideMenu.Visible)
            {
                panelSideMenu.Visible = false;
            }
            else
            {
                panelSideMenu.Visible = true;
            }
        }

        private void Home_Load(object sender, EventArgs e)
        {

        }
    }
}
