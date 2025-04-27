using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Enumerations
{
    public enum OrderStatus
    {
        Pending,
        Confirmed,
        InPreparation,
        Served,
        Closed,
        Cancelled
    }
}
