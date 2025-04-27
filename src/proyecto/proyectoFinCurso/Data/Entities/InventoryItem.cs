using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Entities
{
    public class InventoryItem
    {
        
        public int Id { get; set; }
        public Product Product { get; set; }

        public ValueObjects.Quantity Amount
        {
            get => default;
            set
            {
            }
        }

        public InventoryItem()
        {
            Amount = new ValueObjects.Quantity(0, 0);
        }

    }
}
