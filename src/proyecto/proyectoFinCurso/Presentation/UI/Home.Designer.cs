namespace Presentation.UI
{
    partial class Home
    {
        /// <summary>
        /// Required designer variable.
        /// </summary>
        private System.ComponentModel.IContainer components = null;

        /// <summary>
        /// Clean up any resources being used.
        /// </summary>
        /// <param name="disposing">true if managed resources should be disposed; otherwise, false.</param>
        protected override void Dispose(bool disposing)
        {
            if (disposing && (components != null))
            {
                components.Dispose();
            }
            base.Dispose(disposing);
        }

        #region Windows Form Designer generated code

        /// <summary>
        /// Required method for Designer support - do not modify
        /// the contents of this method with the code editor.
        /// </summary>
        private void InitializeComponent()
        {
            this.panelSideMenu = new System.Windows.Forms.Panel();
            this.button1 = new System.Windows.Forms.Button();
            this.panel1 = new System.Windows.Forms.Panel();
            this.Menu = new System.Windows.Forms.Button();
            this.canvasPanel = new System.Windows.Forms.Panel();
            this.add = new System.Windows.Forms.Button();
            this.edit = new System.Windows.Forms.Button();
            this.panelSideMenu.SuspendLayout();
            this.SuspendLayout();
            // 
            // panelSideMenu
            // 
            this.panelSideMenu.BackColor = System.Drawing.SystemColors.ActiveCaptionText;
            this.panelSideMenu.Controls.Add(this.edit);
            this.panelSideMenu.Controls.Add(this.add);
            this.panelSideMenu.Controls.Add(this.button1);
            this.panelSideMenu.Controls.Add(this.panel1);
            this.panelSideMenu.Dock = System.Windows.Forms.DockStyle.Left;
            this.panelSideMenu.Location = new System.Drawing.Point(0, 0);
            this.panelSideMenu.Name = "panelSideMenu";
            this.panelSideMenu.Size = new System.Drawing.Size(277, 884);
            this.panelSideMenu.TabIndex = 0;
            // 
            // button1
            // 
            this.button1.Dock = System.Windows.Forms.DockStyle.Top;
            this.button1.Location = new System.Drawing.Point(0, 100);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(277, 48);
            this.button1.TabIndex = 1;
            this.button1.Text = "Inventory";
            this.button1.UseVisualStyleBackColor = true;
            // 
            // panel1
            // 
            this.panel1.Dock = System.Windows.Forms.DockStyle.Top;
            this.panel1.Location = new System.Drawing.Point(0, 0);
            this.panel1.Name = "panel1";
            this.panel1.Size = new System.Drawing.Size(277, 100);
            this.panel1.TabIndex = 0;
            // 
            // Menu
            // 
            this.Menu.Location = new System.Drawing.Point(15, 15);
            this.Menu.Name = "Menu";
            this.Menu.Size = new System.Drawing.Size(67, 66);
            this.Menu.TabIndex = 1;
            this.Menu.Text = "Menu";
            this.Menu.UseVisualStyleBackColor = true;
            this.Menu.Click += new System.EventHandler(this.Menu_Click_1);
            // 
            // canvasPanel
            // 
            this.canvasPanel.Dock = System.Windows.Forms.DockStyle.Fill;
            this.canvasPanel.Location = new System.Drawing.Point(277, 0);
            this.canvasPanel.Name = "canvasPanel";
            this.canvasPanel.Size = new System.Drawing.Size(1276, 884);
            this.canvasPanel.TabIndex = 2;
            // 
            // add
            // 
            this.add.Dock = System.Windows.Forms.DockStyle.Top;
            this.add.Location = new System.Drawing.Point(0, 148);
            this.add.Name = "add";
            this.add.Size = new System.Drawing.Size(277, 48);
            this.add.TabIndex = 4;
            this.add.Text = "add";
            this.add.UseVisualStyleBackColor = true;
            // 
            // edit
            // 
            this.edit.Dock = System.Windows.Forms.DockStyle.Top;
            this.edit.Location = new System.Drawing.Point(0, 196);
            this.edit.Name = "edit";
            this.edit.Size = new System.Drawing.Size(277, 48);
            this.edit.TabIndex = 5;
            this.edit.Text = "edit";
            this.edit.UseVisualStyleBackColor = true;
            // 
            // Home
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(1553, 884);
            this.Controls.Add(this.canvasPanel);
            this.Controls.Add(this.Menu);
            this.Controls.Add(this.panelSideMenu);
            this.Name = "Home";
            this.Text = "Home";
            this.Load += new System.EventHandler(this.Home_Load);
            this.panelSideMenu.ResumeLayout(false);
            this.ResumeLayout(false);

        }

        #endregion

        private System.Windows.Forms.Panel panelSideMenu;
        private System.Windows.Forms.Panel panel1;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Button Menu;
        private System.Windows.Forms.Panel canvasPanel;
        private System.Windows.Forms.Button edit;
        private System.Windows.Forms.Button add;
    }
}