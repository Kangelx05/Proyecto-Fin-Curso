namespace Presentation.UI
{
    partial class AddTableTemplate
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
            this.usersDropDown = new Presentation.Controllers.DropDown();
            this.num_customers = new Presentation.Controllers.ComboBox();
            this.tableNumber = new Presentation.Controllers.ComboBox();
            this.button1 = new System.Windows.Forms.Button();
            this.button2 = new System.Windows.Forms.Button();
            this.SuspendLayout();
            // 
            // usersDropDown
            // 
            this.usersDropDown.LabelText = "Waiter";
            this.usersDropDown.Location = new System.Drawing.Point(314, 11);
            this.usersDropDown.Name = "usersDropDown";
            this.usersDropDown.SelectedValue = null;
            this.usersDropDown.Size = new System.Drawing.Size(148, 40);
            this.usersDropDown.TabIndex = 2;
            this.usersDropDown.ValueMember = "";
            // 
            // num_customers
            // 
            this.num_customers.LabelText = "Customers";
            this.num_customers.Location = new System.Drawing.Point(161, 12);
            this.num_customers.Name = "num_customers";
            this.num_customers.Size = new System.Drawing.Size(109, 40);
            this.num_customers.TabIndex = 1;
            // 
            // tableNumber
            // 
            this.tableNumber.LabelText = "Table number";
            this.tableNumber.Location = new System.Drawing.Point(12, 12);
            this.tableNumber.Name = "tableNumber";
            this.tableNumber.Size = new System.Drawing.Size(109, 40);
            this.tableNumber.TabIndex = 0;
            // 
            // button1
            // 
            this.button1.Location = new System.Drawing.Point(306, 72);
            this.button1.Name = "button1";
            this.button1.Size = new System.Drawing.Size(75, 23);
            this.button1.TabIndex = 3;
            this.button1.Text = "Aceptar";
            this.button1.UseVisualStyleBackColor = true;
            this.button1.Click += new System.EventHandler(this.button1_Click);
            // 
            // button2
            // 
            this.button2.Location = new System.Drawing.Point(387, 72);
            this.button2.Name = "button2";
            this.button2.Size = new System.Drawing.Size(75, 23);
            this.button2.TabIndex = 4;
            this.button2.Text = "Cancelar";
            this.button2.UseVisualStyleBackColor = true;
            this.button2.Click += new System.EventHandler(this.button2_Click);
            // 
            // AddTableTemplate
            // 
            this.AutoScaleDimensions = new System.Drawing.SizeF(6F, 13F);
            this.AutoScaleMode = System.Windows.Forms.AutoScaleMode.Font;
            this.ClientSize = new System.Drawing.Size(478, 107);
            this.Controls.Add(this.button2);
            this.Controls.Add(this.button1);
            this.Controls.Add(this.usersDropDown);
            this.Controls.Add(this.num_customers);
            this.Controls.Add(this.tableNumber);
            this.Name = "AddTableTemplate";
            this.Text = "AddTableTemplate";
            this.ResumeLayout(false);

        }

        #endregion

        private Controllers.ComboBox tableNumber;
        private Controllers.ComboBox num_customers;
        private Controllers.DropDown usersDropDown;
        private System.Windows.Forms.Button button1;
        private System.Windows.Forms.Button button2;
    }
}