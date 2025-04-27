using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Entities
{
    public class Product
    {

        public int Id {  get; set; }
        public string Name { get; set; }
        public string Category { get; set; }
        public string Description { get; set; }

        public ValueObjects.Money Price
        {
            get => default;
            set
            {
            }
        }

        public Product() { }
    }
}
