using Domain.Entities;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Domain.Services
{
    public interface IStockPolicy
    {
        bool CanSellProduct(Product product, int requestedQuantity);
        bool HasSufficientStock(Order order);
        bool ShouldTriggerRestock(Product product);
    }
}
