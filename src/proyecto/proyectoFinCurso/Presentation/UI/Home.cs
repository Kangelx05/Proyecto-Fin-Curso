using AutoMapper;
using Domain.Entities;
using Newtonsoft.Json;
using Presentation.Controllers;
using Services;
using Services.Mapping;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Xml;

namespace Presentation.UI
{
    public partial class Home : Form
    {

        private bool _editMode = false;
        private Control _dragUC;
        private Point _offset;
        private List<Table> tables = new List<Table>();
        private HttpClient client = new HttpClient();
        private ApiClient apiClient ;


        public Home()
        {
            InitializeComponent();
            Initialize_Canvas();
            apiClient = new ApiClient(client);
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

            AddTableTemplate template = new AddTableTemplate();

  

            if (template.ShowDialog() == DialogResult.OK)
            {
                var entidad = new TableUiEntity();

                // Enganchamos los eventos de arrastre (sirven tanto si ya estamos en modo edición
                // como si entramos después)
                HookDragEvents(entidad);

                canvasPanel.Controls.Add(entidad);
                entidad.BringToFront();               // por si hay solapamientos

                entidad.table = new Table();
                entidad.table.Num_Table = new Domain.ValueObjects.Quantity(template.numTable, 0);
                entidad.table.Num_Customers = new Domain.ValueObjects.Quantity(template.numCustomers, 0);
                entidad.table.Waiter = template.waiter;

                apiClient.CreateTableAsync(AutoMapperConfig.Mapper.Map<TableRequest>(entidad.table));

            }
            
           
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

            if (_dragUC is TableUiEntity uc)
            {
                // 1) El control terminó de moverse
                _dragUC.Capture = false;
                _dragUC = null;

                // 2) Actualiza la tabla en memoria…
                var mesa = tables.First(m => m.Id == uc.table.Id);
                mesa.posX = uc.Left;
                mesa.posY = uc.Top;

                // 3) …y en la base de datos (o archivo)
                TableRequest request = AutoMapperConfig.Mapper.Map<TableRequest>(mesa);

                try
                {
                    apiClient.UpdateTableAsync(mesa.Id, request);
                }
                catch (Exception ex)
                {
                    Console.WriteLine(ex.ToString());
                }
                
            }
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

        private void FrmMesas_Load(object sender, EventArgs e)
        {
            cleanControls();



            ICollection<TableResponse> response = apiClient.GetAllTablesAsync().Result;

            // 2) Recorre la colección y pinta cada una
            foreach (TableResponse t in response)
            {
                Table mappedTable = AutoMapperConfig.Mapper.Map<Table>(t);
                tables.Add(mappedTable);
                AñadirMesaAlLienzo(mappedTable);
            }
                
        }

        private void cleanControls()
        {
            canvasPanel.Controls.Clear();
        }
        private void AñadirMesaAlLienzo(Table t)
        {
            // Instancia tu UserControl
            var uc = new TableUiEntity
            {
                table = t,
                Location = new Point(t.posX, t.posY),
            };

            // Engancha los eventos de “drag” si estás usando el código de edición
            HookDragEvents(uc);

            // Añádelo al lienzo
            canvasPanel.Controls.Add(uc);
            uc.BringToFront();
        }

        private void api_Click(object sender, EventArgs e)
        {

        }
    }
}
