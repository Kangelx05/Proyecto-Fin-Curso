using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Remoting.Metadata.W3cXsd2001;
using System.Security.Policy;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Entities
{
    public class Table
    {
        

        public int Id { get; set; }

        public User Waiter {  get; set; }

        public ValueObjects.Quantity Num_Table { get; set; }

        public ValueObjects.Quantity Num_Customers { get; set; }

        public Enumerations.TableStatus State { get; set; } = Enumerations.TableStatus.Free;

        public int posX { get; set; }

        public int posY { get; set; }

        public Table()
        {
            
            
        }


    }
}
