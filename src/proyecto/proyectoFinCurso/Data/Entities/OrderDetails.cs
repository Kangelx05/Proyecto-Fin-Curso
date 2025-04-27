using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Entities
{
    public class OrderDetails
    {
        

        public int Id { get; set; }

        public Product Product
        {
            get => default;
            set
            {
            }
        }

        public ValueObjects.Quantity Amount
        {
            get => default;
            set
            {
            }
        }

        public OrderDetails()
        {
            Amount = new ValueObjects.Quantity(1, 1);
        }
    }
}
