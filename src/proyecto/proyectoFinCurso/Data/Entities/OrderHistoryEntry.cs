using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Entities
{
    public class OrderHistoryEntry
    {


        public Order Order
        {
            get => default;
            set
            {
            }
        }


        public User User
        {
            get => default;
            set
            {
            }
        }

        public int Id
        {
            get => default;
            set
            {
            }
        }

        public string Date
        {
            get => default;
            set
            {
            }
        }

        public Enumerations.OrderStatus New_State
        {
            get => default;
            set
            {
            }
        }

        public Enumerations.OrderStatus Old_State
        {
            get => default;
            set
            {
            }
        }

        public OrderHistoryEntry()
        {
            throw new System.NotImplementedException();
        }
    }
}
