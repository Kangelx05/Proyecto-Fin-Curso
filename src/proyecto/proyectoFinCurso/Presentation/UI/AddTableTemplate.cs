using Domain.Entities;
using Services;
using Services.Mapping;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using System.Windows.Forms.VisualStyles;

namespace Presentation.UI
{
    public partial class AddTableTemplate : Form
    {
        List<User> users = new List<User>();

        //Selected properties
        public int numTable;
        public int numCustomers;
        public User waiter;
        public AddTableTemplate()
        {
            InitializeComponent();
            loadDropDown();
        }

        public void loadDropDown()
        {
            HttpClient client = new HttpClient();
            ApiClient apiClient = new ApiClient(client);

            foreach (UserResponse user in apiClient.FindAllAsync().Result)
            {
                users.Add(AutoMapperConfig.Mapper.Map<User>(user));
            }

            usersDropDown.DataSource = users;
            usersDropDown.DisplayMember = "Name";
            usersDropDown.ValueMember = "Id";
        }

        private void button1_Click(object sender, EventArgs e)
        {
            numTable = int.Parse(tableNumber.Text);
            numCustomers = int.Parse(num_customers.Text);
            waiter = (User)usersDropDown.SelectedItem;
            DialogResult = DialogResult.OK;
            Close();
        }

        private void button2_Click(object sender, EventArgs e)
        {
            DialogResult = DialogResult.Cancel;
        }
    }
}
