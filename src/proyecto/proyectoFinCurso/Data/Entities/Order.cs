using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Entities
{
    public class Order
    {
        public int Id { get; set; }
        public DateTime Date { get; set; }
        public Enumerations.OrderStatus State
        {
            get => default;
            set
            {
            }
        }

        public Table Table
        {
            get => default;
            set
            {
            }
        }

        public OrderDetails[] OrderDetails
        {
            get => default;
            set
            {
            }
        }

        public Enumerations.OrderStatus OrderStatus
        {
            get => default;
            set
            {
            }
        }

        public Order() { }


    }
}
