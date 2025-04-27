using System;
using System.Collections.Generic;
using System.Linq;
using System.Security.Policy;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Entities
{
    public class Table
    {
        

        public int Id { get; set; }

        public User Waiter {  get; set; }

        public ValueObjects.Quantity NumTable
        {
            get => default;
            set
            {
            }
        }

        public ValueObjects.Quantity NumCustomers
        {
            get => default;
            set
            {
            }
        }

        public Enumerations.TableStatus State { get; set; } = Enumerations.TableStatus.Free;

        public Table()
        {
            NumTable = new ValueObjects.Quantity(0, 0);
            NumCustomers = new ValueObjects.Quantity(0,0);
            
        }
    }
}
