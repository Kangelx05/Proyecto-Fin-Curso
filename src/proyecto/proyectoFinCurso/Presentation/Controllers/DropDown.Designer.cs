namespace Presentation.Controllers
{
    partial class DropDown
    {
        /// <summary> 
        /// Variable del diseñador necesaria.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary> 
        /// Limpiar los recursos que se estén usando.
        /// </summary>
        /// <param name="disposing">true si los recursos administrados se deben desechar; false en caso contrario.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Código generado por el Diseñador de componentes

        /// <summary> 
        /// Método necesario para admitir el Diseñador. No se puede modificar
        /// el contenido de este método con el editor de código.
        /// </summary>
        private void InitializeComponent()
        {
            this.comboLabel = new System.Windows.Forms.Label();
            this.tableLayout = new System.Windows.Forms.TableLayoutPanel();
            this.combo = new System.Windows.Forms.ComboBox();
            this.tableLayout.SuspendLayout();
            this.SuspendLayout();
            // 
            // comboLabel
            // 
            this.comboLabel.AutoSize = true;
            this.comboLabel.Location = new System.Drawing.Point(3, 0);
            this.comboLabel.Name = "comboLabel";
            this.comboLabel.Size = new System.Drawing.Size(27, 13);
            this.comboLabel.TabIndex = 0;
            this.comboLabel.Text = "Title";
            this.comboLabel.Click += new System.EventHandler(this.label1_Click);
            // 
            // tableLayout
            // 
            this.tableLayout.ColumnCount = 1;
            this.tableLayout.ColumnStyles.Add(new System.Windows.Forms.ColumnStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayout.Controls.Add(this.comboLabel, 0, 0);
            this.tableLayout.Controls.Add(this.combo, 0, 1);
            this.tableLayout.Dock = System.Windows.Forms.DockStyle.Fill;
            this.tableLayout.Location = new System.Drawing.Point(0, 0);
            this.tableLayout.Name = "tableLayout";
            this.tableLayout.RowCount = 2;
            this.tableLayout.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Absolute, 13F));
            this.tableLayout.RowStyles.Add(new System.Windows.Forms.RowStyle(System.Windows.Forms.SizeType.Percent, 100F));
            this.tableLayout.Size = new System.Drawing.Size(148, 40);
            this.tableLayout.TabIndex = 2;
            // 
            // combo
            // 
            this.combo.Dock = System.Windows.Forms.DockStyle.Fill;
            this.combo.FormattingEnabled = true;
            this.combo.Location = new System.Drawing.Point(3, 16);
            this.combo.Name = "combo";
            this.combo.Size = new System.Drawing.Size(142, 21);
            this.combo.TabIndex = 1;
            // 
            // DropDown
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.Controls.Add(this.tableLayout);
            this.Name = "DropDown";
            this.Size = new System.Drawing.Size(148, 40);
            this.tableLayout.ResumeLayout(false);
            this.tableLayout.PerformLayout();
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Label comboLabel;
        private System.Windows.Forms.TableLayoutPanel tableLayout;
        private System.Windows.Forms.ComboBox combo;
    }
}
